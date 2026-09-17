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
import org.springframework.transaction.support.TransactionTemplate;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Regression tests for {@code importType=INITIAL} handling of {@link Parking}.
 * <p>
 * These properties are what a bulk park-and-ride data migration relies on to preserve
 * deterministic, prefix-namespaced ids (e.g. {@code NSR:Parking:<sourceId>}) across repeated
 * imports. The ids below hardcode {@code NSR}, which is the default of {@code netex.validPrefix}
 * and is not overridden in the test profile.
 */
public class ParkingInitialImportTest extends TiamatIntegrationTest {

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private static final String PARENT_STOP_PLACE_ID = "NSR:StopPlace:1000";
    private static final String IMPORTED_ID_KEY = "imported-id";

    /**
     * Batch size for {@link #initialImportOfMultipleParkingsImportsAll}. Keep it well below
     * {@code spring.datasource.hikari.maximumPoolSize}, which is 10 in the test profile, because an import
     * needs about one connection per element plus one for the enclosing transaction. See that test's javadoc.
     */
    private static final int MULTI_IMPORT_BATCH_SIZE = 4;

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

    /** Reads the {@code imported-id} key value off the response's netex object. */
    private List<String> importedIdValue(Parking parking) {
        if (parking.getKeyList() == null) {
            return List.of();
        }
        return parking.getKeyList().getKeyValue().stream()
                .filter(kv -> IMPORTED_ID_KEY.equals(kv.getKey()))
                .map(KeyValueStructure::getValue)
                .toList();
    }

    /**
     * Reads the {@code imported-id} key values back from the database. The importer maps its
     * response from the same instance it just saved, inside the still-open transaction, so an
     * assertion on the response alone cannot distinguish a persisted key value from one that only
     * ever existed in memory. Runs in a transaction because {@code keyValues} is LAZY.
     */
    private Set<String> persistedImportedIds(String netexId) {
        return transactionTemplate.execute(status -> Set.copyOf(
                parkingRepository.findFirstByNetexIdOrderByVersionDesc(netexId).getOriginalIds()));
    }

    /** A valid-prefix, numeric-postfix id survives INITIAL import verbatim. */
    @Test
    public void initialImportPreservesIdWithValidPrefixAndNumericPostfix() throws JAXBException, IOException, SAXException {
        persistParentStopPlace();

        PublicationDeliveryStructure response = importInitial(netexParking("NSR:Parking:99", "Parking 1"));

        List<Parking> parkings = extractParkings(response);
        assertThat(parkings).hasSize(1);
        assertThat(parkings.get(0).getId()).isEqualTo("NSR:Parking:99");

        org.rutebanken.tiamat.model.Parking persisted = parkingRepository.findFirstByNetexIdOrderByVersionDesc("NSR:Parking:99");
        assertThat(persisted).as("Parking persisted with the preserved id").isNotNull();
    }

    /**
     * Claiming a numeric id under the valid prefix advances Tiamat's gapless sequence for
     * Parking, exactly as it would for a Tiamat-minted id. This matters when the Parking table
     * holds few rows: an id claimed by an import then sits in the same low numeric range
     * Tiamat's own auto-generator would otherwise hand out, so failing to advance the sequence
     * would open a collision window.
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

    /** An explicit imported-id key value survives the import untouched. */
    @Test
    public void initialImportPreservesImportedIdKeyValue() throws JAXBException, IOException, SAXException {
        persistParentStopPlace();

        PublicationDeliveryStructure response = importInitial(
                withImportedId(netexParking("NSR:Parking:5", "Parking facility"), "XYZ:Facility:5"));

        List<Parking> parkings = extractParkings(response);
        assertThat(parkings).hasSize(1);
        assertThat(importedIdValue(parkings.get(0))).contains("XYZ:Facility:5");
        assertThat(persistedImportedIds("NSR:Parking:5"))
                .as("the key value must reach the database, not only the response")
                .contains("XYZ:Facility:5");
    }

    /** Re-importing the same document creates no second row, and bumps the version. */
    @Test
    public void initialReImportCreatesNoDuplicateRowAndBumpsVersion() throws JAXBException, IOException, SAXException {
        persistParentStopPlace();

        importInitial(netexParking("NSR:Parking:7", "Parking facility"));
        importInitial(netexParking("NSR:Parking:7", "Parking facility"));

        // Asserts on the ids rather than the entities: AssertJ formats the actual value when an
        // assertion fails, and Parking.toString() would touch the LAZY vehicleEntrances collection
        // outside a session, masking the real failure with a LazyInitializationException.
        List<String> netexIds = parkingRepository.findAll().stream()
                .map(org.rutebanken.tiamat.model.Parking::getNetexId)
                .toList();
        assertThat(netexIds)
                .as("the re-import must update the existing row, not add a second one under a new id")
                .containsExactly("NSR:Parking:7");
        // Not idempotent on the version: the fixture arrives as version 1, and
        // ParkingVersionedSaverService.saveNewVersion() deletes the previous row and calls
        // versionIncrementor.initiateOrIncrement(), which increments whenever netexId is already
        // set (1 -> 2 on the first import, 2 -> 3 on the second).
        assertThat(parkingRepository.findFirstByNetexIdOrderByVersionDesc("NSR:Parking:7").getVersion())
                .isEqualTo(3);
    }

    /** A field present in the first import but absent from the second is dropped (full-replace). */
    @Test
    public void initialReImportRemovesFieldAbsentFromSecondDocument() throws JAXBException, IOException, SAXException {
        persistParentStopPlace();

        Parking withCapacity = netexParking("NSR:Parking:9", "Parking facility")
                .withTotalCapacity(BigInteger.TEN)
                .withPaymentMethods(PaymentMethodEnumeration.CASH);
        importInitial(withCapacity);

        org.rutebanken.tiamat.model.Parking afterFirstImport = parkingRepository.findFirstByNetexIdOrderByVersionDesc("NSR:Parking:9");
        assertThat(afterFirstImport.getTotalCapacity()).isEqualTo(BigInteger.TEN);
        assertThat(afterFirstImport.getPaymentMethods()).isNotEmpty();

        Parking withoutCapacity = netexParking("NSR:Parking:9", "Parking facility");
        importInitial(withoutCapacity);

        org.rutebanken.tiamat.model.Parking afterSecondImport = parkingRepository.findFirstByNetexIdOrderByVersionDesc("NSR:Parking:9");
        assertThat(afterSecondImport.getTotalCapacity()).as("field absent from the re-imported document must be dropped").isNull();
        assertThat(afterSecondImport.getPaymentMethods())
                .as("collection absent from the re-imported document must be cleared")
                .isEmpty();
    }

    /**
     * A batch of several parkings sharing a parent stop place all import successfully.
     *
     * <p>The batch size is deliberately kept well below the connection pool size.
     * {@link org.rutebanken.tiamat.importer.initial.ParallelInitialParkingImporter} carries
     * {@code @Transactional} on the class, so the calling thread holds one connection for the whole batch.
     * It then maps the batch over a {@code parallelStream()}, so each element that runs on a worker thread
     * opens a second transaction and takes another connection. Elements that the stream runs on the calling
     * thread join the transaction that is already open there and need no extra connection.
     * {@code spring.datasource.hikari.maximumPoolSize} is 10 in the test profile. A batch of 10 was observed
     * holding all 10 connections at once ({@code active=10, idle=0, waiting=1}) and then failing with a 30 s
     * connection timeout.
     *
     * <p>Pinning {@code ForkJoinPool.common.parallelism} to 2 did not prevent that failure, which is worth
     * recording because it is the obvious thing to try. This test does not establish why it did not help, so
     * do not treat that setting as a bound. The element count is the lever that was shown to work.
     */
    @Test
    public void initialImportOfMultipleParkingsImportsAll() throws JAXBException, IOException, SAXException {
        persistParentStopPlace();

        Parking[] parkings = new Parking[MULTI_IMPORT_BATCH_SIZE];
        for (int i = 0; i < parkings.length; i++) {
            parkings[i] = netexParking("NSR:Parking:" + (100 + i), "Parking facility " + i);
        }

        PublicationDeliveryStructure response = importInitial(parkings);

        List<Parking> imported = extractParkings(response);
        assertThat(imported).hasSize(MULTI_IMPORT_BATCH_SIZE);
        for (int i = 0; i < parkings.length; i++) {
            String expectedId = "NSR:Parking:" + (100 + i);
            assertThat(parkingRepository.findFirstByNetexIdOrderByVersionDesc(expectedId))
                    .as("parking %s persisted", expectedId)
                    .isNotNull();
        }
    }

    /**
     * Contrast case. A foreign-prefixed id is not claimable, so it is demoted to an imported-id
     * key value and a new gapless id is minted instead - this is the branch that makes
     * {@link #initialImportPreservesIdWithValidPrefixAndNumericPostfix} meaningful (it proves the
     * valid-prefix short-circuit, rather than every id surviving import regardless of prefix).
     */
    @Test
    public void initialImportOfForeignPrefixReplacesIdAndRecordsOriginal() throws JAXBException, IOException, SAXException {
        persistParentStopPlace();

        PublicationDeliveryStructure response = importInitial(netexParking("XYZ:Parking:1", "Foreign prefix"));

        List<Parking> parkings = extractParkings(response);
        assertThat(parkings).hasSize(1);
        assertThat(parkings.get(0).getId()).startsWith("NSR:Parking:").isNotEqualTo("XYZ:Parking:1");
        assertThat(importedIdValue(parkings.get(0))).contains("XYZ:Parking:1");
        assertThat(persistedImportedIds(parkings.get(0).getId()))
                .as("the demoted id must reach the database, not only the response")
                .contains("XYZ:Parking:1");
    }

    /**
     * Known-limitation regression guard. A valid-prefix id with a non-numeric postfix
     * (e.g. {@code NSR:Parking:abc-1}) is neither silently accepted nor silently demoted - it
     * fails the import outright, because {@code NetexIdProvider.claimId()} unconditionally parses
     * the postfix as a {@code Long} once the prefix has matched. Producers that namespace ids
     * with a non-numeric postfix must therefore use a foreign prefix instead.
     */
    @Test
    public void initialImportOfValidPrefixWithNonNumericPostfixThrows() {
        persistParentStopPlace();

        assertThatThrownBy(() -> importInitial(netexParking("NSR:Parking:abc-1", "Parking facility")))
                .hasRootCauseInstanceOf(NumberFormatException.class);
    }
}
