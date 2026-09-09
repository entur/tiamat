/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import jakarta.xml.bind.JAXBException;
import org.junit.Test;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.ParkingsInFrame_RelStructure;
import org.rutebanken.netex.model.PaymentMethodEnumeration;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.SiteRefStructure;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.ImportType;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Regression tests for {@code importType=INITIAL} handling of {@link Parking}.
 * <p>
 * These properties are what the Liipi park-and-ride migration (DPO-4802) relies on to
 * preserve deterministic, prefix-namespaced ids (e.g. {@code FSR:Parking:<facilityId>}) across
 * repeated imports. The prefix used here must always track {@code netex.validPrefix} (which
 * defaults to {@code NSR} and is not overridden in the test profile) rather than a specific
 * deployment's value - see DPO-4956 and {@code generated-docs/plan_DPO-4956-initial-import-tests.md}
 * in the fintraffic workspace.
 * <p>
 * All fixtures here use fields already present in core Tiamat (upstream {@code entur/master}) -
 * none depend on the Fintraffic {@code src/ext} extensions, since this class targets
 * {@code entur/tiamat} directly.
 */
public class ParkingInitialImportTest extends TiamatIntegrationTest {

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    private static final String PARENT_STOP_PLACE_ID = "NSR:StopPlace:1000";
    private static final String IMPORTED_ID_KEY = "imported-id";

    private void persistParentStopPlace() {
        org.rutebanken.tiamat.model.StopPlace stopPlace = new org.rutebanken.tiamat.model.StopPlace();
        stopPlace.setNetexId(PARENT_STOP_PLACE_ID);
        stopPlace.setVersion(1);
        stopPlaceRepository.save(stopPlace);
    }

    private Parking netexParking(String id, String name) {
        Parking parking = new Parking()
                .withVersion("1")
                .withParentSiteRef(new SiteRefStructure().withRef(PARENT_STOP_PLACE_ID));
        if (id != null) {
            parking.setId(id);
        }
        if (name != null) {
            parking.withName(new MultilingualString().withValue(name));
        }
        return parking;
    }

    private Parking withImportedId(Parking parking, String importedId) {
        return parking.withKeyList(new KeyListStructure()
                .withKeyValue(new KeyValueStructure().withKey(IMPORTED_ID_KEY).withValue(importedId)));
    }

    private PublicationDeliveryStructure importInitial(Parking... parkings) throws JAXBException, IOException, SAXException {
        SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame();
        siteFrame.withParkings(new ParkingsInFrame_RelStructure().withParking(parkings));
        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryTestHelper.publicationDelivery(siteFrame);

        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.INITIAL;

        return publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure, importParams);
    }

    private List<Parking> extractParkings(PublicationDeliveryStructure publicationDeliveryStructure) {
        return publicationDeliveryTestHelper.findSiteFrame(publicationDeliveryStructure).getParkings().getParking();
    }

    /**
     * Reads the {@code imported-id} key value straight off the response's netex object, avoiding a
     * round trip through the JPA-backed repository (whose {@code keyValues} collection is lazy and
     * requires an open Hibernate session that a test method does not otherwise have).
     */
    private List<String> importedIdValue(Parking parking) {
        if (parking.getKeyList() == null) {
            return List.of();
        }
        return parking.getKeyList().getKeyValue().stream()
                .filter(kv -> IMPORTED_ID_KEY.equals(kv.getKey()))
                .map(KeyValueStructure::getValue)
                .toList();
    }

    /** T1 - covers R1: a valid-prefix, numeric-postfix id survives INITIAL import verbatim. */
    @Test
    public void initialImportPreservesIdWithValidPrefixAndNumericPostfix() throws JAXBException, IOException, SAXException {
        persistParentStopPlace();

        PublicationDeliveryStructure response = importInitial(netexParking("NSR:Parking:99", "Liipi 1"));

        List<Parking> parkings = extractParkings(response);
        assertThat(parkings).hasSize(1);
        assertThat(parkings.get(0).getId()).isEqualTo("NSR:Parking:99");

        org.rutebanken.tiamat.model.Parking persisted = parkingRepository.findFirstByNetexIdOrderByVersionDesc("NSR:Parking:99");
        assertThat(persisted).as("Parking persisted with the preserved id").isNotNull();
    }

    /**
     * T2 - covers R2: claiming a numeric id under the valid prefix advances Tiamat's gapless
     * sequence for Parking, exactly as it would for a Tiamat-minted id. This matters because
     * production's Parking table currently has very few rows, so an id claimed by the Liipi
     * import sits in the same low numeric range Tiamat's own auto-generator would otherwise
     * hand out - see the id-collision race window in plan_liipi-data-import.md.
     */
    @Test
    public void initialImportOfNumericIdAdvancesGaplessSequence() throws JAXBException, IOException, SAXException {
        persistParentStopPlace();

        importInitial(netexParking("NSR:Parking:1", "Claimed"));

        // Foreign prefix: not claimable, so NetexIdMapper demotes it and NetexIdProvider mints a
        // fresh auto-generated id (NeTEx requires @id to be present, so this - not a bare null id -
        // is how the auto-generation path is reached through a full XML import).
        PublicationDeliveryStructure response = importInitial(netexParking("XYZ:Parking:temp", "Auto-generated"));

        List<Parking> parkings = extractParkings(response);
        assertThat(parkings).hasSize(1);
        assertThat(parkings.get(0).getId())
                .as("the generator must skip the already-claimed id 1")
                .isEqualTo("NSR:Parking:2");
    }

    /** T3 - covers R3: an explicit imported-id key value survives the import untouched. */
    @Test
    public void initialImportPreservesImportedIdKeyValue() throws JAXBException, IOException, SAXException {
        persistParentStopPlace();

        PublicationDeliveryStructure response = importInitial(
                withImportedId(netexParking("NSR:Parking:5", "Liipi facility"), "LIIPI:Facility:5"));

        List<Parking> parkings = extractParkings(response);
        assertThat(parkings).hasSize(1);
        assertThat(importedIdValue(parkings.get(0))).contains("LIIPI:Facility:5");
    }

    /** T4 - covers R4 and R5: re-importing the same document is idempotent (one row, version bumped). */
    @Test
    public void initialImportIsIdempotentOnReRun() throws JAXBException, IOException, SAXException {
        persistParentStopPlace();

        importInitial(netexParking("NSR:Parking:7", "Liipi facility"));
        importInitial(netexParking("NSR:Parking:7", "Liipi facility"));

        List<org.rutebanken.tiamat.model.Parking> all = parkingRepository.findAll().stream()
                .filter(p -> "NSR:Parking:7".equals(p.getNetexId()))
                .toList();
        assertThat(all).hasSize(1);
        // ParkingVersionedSaverService.saveNewVersion() deletes the previous row and increments the
        // version unconditionally (versionIncrementor.initiateOrIncrement()), even for a first import
        // (1 -> 2). A second, identical import therefore lands on version 3, not 2.
        assertThat(all.get(0).getVersion()).isEqualTo(3);
    }

    /** T5 - covers R6: a field present in the first import but absent from the second is dropped (full-replace). */
    @Test
    public void initialReImportRemovesFieldAbsentFromSecondDocument() throws JAXBException, IOException, SAXException {
        persistParentStopPlace();

        Parking withCapacity = netexParking("NSR:Parking:9", "Liipi facility")
                .withTotalCapacity(BigInteger.TEN)
                .withPaymentMethods(PaymentMethodEnumeration.CASH);
        importInitial(withCapacity);

        org.rutebanken.tiamat.model.Parking afterFirstImport = parkingRepository.findFirstByNetexIdOrderByVersionDesc("NSR:Parking:9");
        assertThat(afterFirstImport.getTotalCapacity()).isEqualTo(BigInteger.TEN);

        Parking withoutCapacity = netexParking("NSR:Parking:9", "Liipi facility");
        importInitial(withoutCapacity);

        org.rutebanken.tiamat.model.Parking afterSecondImport = parkingRepository.findFirstByNetexIdOrderByVersionDesc("NSR:Parking:9");
        assertThat(afterSecondImport.getTotalCapacity()).as("field absent from the re-imported document must be dropped").isNull();
    }

    /** T6 - covers R7: a batch of several parkings sharing a parent stop place all import successfully. */
    @Test
    public void initialImportOfMultipleParkingsImportsAll() throws JAXBException, IOException, SAXException {
        persistParentStopPlace();

        Parking[] parkings = new Parking[10];
        for (int i = 0; i < parkings.length; i++) {
            parkings[i] = netexParking("NSR:Parking:" + (100 + i), "Liipi facility " + i);
        }

        PublicationDeliveryStructure response = importInitial(parkings);

        List<Parking> imported = extractParkings(response);
        assertThat(imported).hasSize(10);
        for (int i = 0; i < parkings.length; i++) {
            String expectedId = "NSR:Parking:" + (100 + i);
            assertThat(parkingRepository.findFirstByNetexIdOrderByVersionDesc(expectedId))
                    .as("parking %s persisted", expectedId)
                    .isNotNull();
        }
    }

    /**
     * T7 - covers R8: contrast case. A foreign-prefixed id is not claimable, so it is demoted to
     * an imported-id key value and a new gapless id is minted instead - this is the branch that
     * makes T1's assertion meaningful (proves the valid-prefix short-circuit, rather than every
     * id surviving import regardless of prefix).
     */
    @Test
    public void initialImportOfForeignPrefixReplacesIdAndRecordsOriginal() throws JAXBException, IOException, SAXException {
        persistParentStopPlace();

        PublicationDeliveryStructure response = importInitial(netexParking("XYZ:Parking:1", "Foreign prefix"));

        List<Parking> parkings = extractParkings(response);
        assertThat(parkings).hasSize(1);
        assertThat(parkings.get(0).getId()).startsWith("NSR:Parking:").isNotEqualTo("XYZ:Parking:1");
        assertThat(importedIdValue(parkings.get(0))).contains("XYZ:Parking:1");
    }

    /**
     * T8 - covers R9: a known-limitation regression guard. A valid-prefix id with a non-numeric
     * postfix (the migration's original, now-abandoned id scheme, e.g. FSR:Parking:liipi-1) is
     * not silently accepted or silently demoted - it fails the import outright, because
     * NetexIdProvider.claimId() unconditionally parses the postfix as a Long once the prefix has
     * matched. This documents the exact defect found while writing T1 against the old scheme, so
     * the same mistake is not reintroduced unnoticed. See
     * digitraffic-tis-parking-netex-migration PR #14 and plan_liipi-data-import.md.
     */
    @Test
    public void initialImportOfValidPrefixWithNonNumericPostfixThrows() {
        persistParentStopPlace();

        assertThatThrownBy(() -> importInitial(netexParking("NSR:Parking:liipi-1", "Liipi facility")))
                .hasRootCauseInstanceOf(NumberFormatException.class);
    }
}
