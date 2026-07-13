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

package org.rutebanken.tiamat.versioning.save;

import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SanitaryEquipment;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TicketingEquipment;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.model.WaitingRoomEquipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.Principal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.rutebanken.tiamat.versioning.save.DefaultVersionedSaverService.MILLIS_BETWEEN_VERSIONS;

@Transactional
public class StopPlaceVersionedSaverServiceTest extends TiamatIntegrationTest {


    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;


    @Test
    public void saveStopPlaceWithInstalledEquipment() {
        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("quay with place equipments"));
        PlaceEquipment quayPlaceEquipment = new PlaceEquipment();
        TicketingEquipment quayTicketingEquipment = new TicketingEquipment();
        quayPlaceEquipment.getInstalledEquipment().add(quayTicketingEquipment);
        quay.setPlaceEquipments(quayPlaceEquipment);

        StopPlace stopPlace = new StopPlace();
        stopPlace.getQuays().add(quay);
        TicketingEquipment ticketingEquipment1 = new TicketingEquipment();
        PlaceEquipment stopPlacePlaceEquipment = new PlaceEquipment();
        stopPlacePlaceEquipment.getInstalledEquipment().add(ticketingEquipment1);
        stopPlace.setPlaceEquipments(stopPlacePlaceEquipment);

        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        assertThat(actualStopPlace).isNotNull();

        assertThat(actualStopPlace.getPlaceEquipments().getInstalledEquipment()).hasSize(1);
        assertThat(actualStopPlace.getPlaceEquipments().getInstalledEquipment().getFirst().getNetexId())
                .isNotNull();

        assertThat(actualStopPlace.getPlaceEquipments().getInstalledEquipment().getFirst().getVersion())
                .isEqualTo(1L);

        Quay actualQuay = actualStopPlace.getQuays().iterator().next();
        assertThat(actualQuay.getVersion()).isEqualTo(1);

        assertThat(actualQuay.getPlaceEquipments().getInstalledEquipment()).hasSize(1);
        assertThat(actualQuay.getPlaceEquipments().getInstalledEquipment().getFirst().getNetexId())
                .isNotNull();

        assertThat(actualQuay.getPlaceEquipments().getInstalledEquipment().getFirst().getVersion())
                .isEqualTo(1L);

        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());
        assertThat(actualStopPlace.getPlaceEquipments().getInstalledEquipment().getFirst().getVersion())
                .isEqualTo(2L);

        actualQuay = actualStopPlace.getQuays().iterator().next();
        assertThat(actualQuay.getPlaceEquipments().getInstalledEquipment().getFirst().getVersion())
                .isEqualTo(2L);



    }

    @Test
    public void newStopPlaceWithQuayVerifyVersionSet() {
        Quay quay1 = new Quay();
        quay1.setName(new EmbeddableMultilingualString("quay1"));

        Quay quay2 = new Quay();
        quay2.setName(new EmbeddableMultilingualString("quay2"));

        StopPlace stopPlace = new StopPlace();
        stopPlace.getQuays().add(quay1);
        stopPlace.getQuays().add(quay2);

        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        assertThat(actualStopPlace).isNotNull();
        assertThat(actualStopPlace.getVersion()).isEqualTo(1);
        actualStopPlace.getQuays().forEach(quay -> assertThat(quay.getVersion()).isEqualTo(1));
    }

    /**
     * NRP-1632
     *
     * @throws Exception
     */
    @Test
    public void newVersionVerifyValidFrom() throws Exception {

        StopPlace oldVersion = new StopPlace(new EmbeddableMultilingualString("Espa"));

        oldVersion = stopPlaceVersionedSaverService.saveNewVersion(oldVersion);

        String newName = "EspaBoller";

        StopPlace newVersion = versionCreator.createCopy(oldVersion, StopPlace.class);
        newVersion.setName(new EmbeddableMultilingualString(newName));

        newVersion = stopPlaceVersionedSaverService.saveNewVersion(oldVersion, newVersion);

        assertThat(newVersion.getVersion()).isGreaterThan(oldVersion.getVersion());
        assertThat(newVersion.getValidBetween()).isNotNull();
        assertThat(newVersion.getValidBetween().getFromDate()).isNotNull();
        assertThat(newVersion.getValidBetween().getToDate()).isNull();


        oldVersion = stopPlaceRepository.findFirstByNetexIdAndVersion(oldVersion.getNetexId(), oldVersion.getVersion());
        assertThat(oldVersion.getValidBetween().getFromDate()).isNotNull();
        assertThat(oldVersion.getValidBetween().getToDate()).isNotNull();

        assertThat(newVersion.getValidBetween().getFromDate().minusMillis(MILLIS_BETWEEN_VERSIONS)).isEqualTo(oldVersion.getValidBetween().getToDate());
    }

    @Test
    public void terminateStopPlaceValidityVerifyValidity() throws Exception {

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("About to get terminated"));
        stopPlace.setVersion(1L);
        stopPlace.setValidBetween(new ValidBetween(Instant.EPOCH));
        stopPlace = stopPlaceRepository.save(stopPlace);

        StopPlace newVersion = versionCreator.createCopy(stopPlace, StopPlace.class);

        Instant now = Instant.now();

        Instant terminated = now.plusSeconds(1);

        newVersion.setValidBetween(new ValidBetween(now, terminated));

        newVersion = stopPlaceVersionedSaverService.saveNewVersion(stopPlace, newVersion);

        assertThat(newVersion.getVersion()).isGreaterThan(stopPlace.getVersion());
        assertThat(newVersion.getValidBetween()).isNotNull();
        assertThat(newVersion.getValidBetween().getFromDate()).isEqualTo(now);
        assertThat(newVersion.getValidBetween().getToDate()).isEqualTo(terminated);
    }

    @Test(expected = Exception.class)
    public void saveStopWithAdjacentSiteTooFarAwayFromEachOther() throws Exception {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Adjacent"));
        stopPlace.setVersion(1L);
        stopPlace.setCentroid(point(60.000, 10.78));
        stopPlace = stopPlaceRepository.save(stopPlace);

        StopPlace adjacentStopPlace = new StopPlace(new EmbeddableMultilingualString("adjacentStopPlace"));
        adjacentStopPlace.setVersion(1L);
        adjacentStopPlace.setCentroid(point(70.000, 10.78));
        adjacentStopPlace = stopPlaceRepository.save(adjacentStopPlace);

        stopPlace.getAdjacentSites().add(new SiteRefStructure(adjacentStopPlace.getNetexId()));
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
    }

    @Test(expected = Exception.class)
    public void saveStopWithAdjacentSiteWithSameId() throws Exception {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Adjacent"));
        stopPlace.setVersion(1L);
        stopPlace.setCentroid(point(60.000, 10.78));
        stopPlace = stopPlaceRepository.save(stopPlace);

        stopPlace.getAdjacentSites().add(new SiteRefStructure(stopPlace.getNetexId()));
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
    }

    @Test
    public void newVersionDefaultFromDateIsSet() throws Exception {

        StopPlace oldVersion = new StopPlace(new EmbeddableMultilingualString("About to get a new version where default from date is set"));
        oldVersion.setVersion(1L);
        oldVersion.setValidBetween(new ValidBetween(Instant.EPOCH));
        oldVersion = stopPlaceRepository.save(oldVersion);

        StopPlace newVersion = versionCreator.createCopy(oldVersion, StopPlace.class);

        newVersion.setValidBetween(new ValidBetween(null));

        newVersion = stopPlaceVersionedSaverService.saveNewVersion(oldVersion, newVersion);

        assertThat(newVersion.getVersion()).isGreaterThan(oldVersion.getVersion());
        assertThat(newVersion.getValidBetween()).isNotNull();
        assertThat(newVersion.getValidBetween().getFromDate()).isNotNull();

        oldVersion = stopPlaceRepository.findFirstByNetexIdAndVersion(oldVersion.getNetexId(), oldVersion.getVersion());
        assertThat(newVersion.getValidBetween().getFromDate().minusMillis(MILLIS_BETWEEN_VERSIONS)).isEqualTo(oldVersion.getValidBetween().getToDate());
    }

    @Test
    public void newVersionNoValidbetweenOnChildObjects() {
        Quay quay = new Quay();
        StopPlace stopPlace = new StopPlace();
        stopPlace.getQuays().add(quay);

        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        StopPlace actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());
        assertThat(actualStopPlace.getQuays().iterator().next().getValidBetween()).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void doNotAcceptExistingAndNewVersionToBeExactlyEqual() {
        StopPlace stopPlace = new StopPlace();
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace, stopPlace);
    }

    @Test(expected = IllegalArgumentException.class)
    public void savingNewVersionShouldOnlyAcceptSameVersion() {

        StopPlace existingVersion = new StopPlace();
        StopPlace newVersion = new StopPlace();
        stopPlaceVersionedSaverService.saveNewVersion(existingVersion, newVersion);
    }

    @Test(expected = IllegalArgumentException.class)
    public void existingVersionMustHaveNetexId() {
        StopPlace existingVersion = new StopPlace();
        StopPlace newVersion = new StopPlace();
        stopPlaceVersionedSaverService.saveNewVersion(existingVersion, newVersion);
    }

    @Test(expected = IllegalArgumentException.class)
    public void doNotAcceptFromDateBeforePreviousVersionFromDate() {
        StopPlace previousVersion = new StopPlace();
        previousVersion.setVersion(1L);

        stopPlaceRepository.save(previousVersion);

        Instant now = Instant.now();

        // No to date
        previousVersion.setValidBetween(new ValidBetween(now.minusSeconds(1000), null));

        StopPlace newVersion = new StopPlace();
        newVersion.setVersion(2L);
        newVersion.setValidBetween(new ValidBetween(previousVersion.getValidBetween().getFromDate().minusSeconds(10)));
        newVersion.setNetexId(previousVersion.getNetexId());

        stopPlaceVersionedSaverService.saveNewVersion(previousVersion, newVersion);
    }

    @Test
    public void saveStopPlaceWithQuayVerifyValuesSet() {
        Quay quay1 = new Quay();
        quay1.setName(new EmbeddableMultilingualString("quay1"));

        Quay quay2 = new Quay();
        quay2.setName(new EmbeddableMultilingualString("quay2"));

        StopPlace stopPlace = new StopPlace();
        String initialStopPlaceName = "Initial name";
        stopPlace.setName(new EmbeddableMultilingualString(initialStopPlaceName));
        stopPlace.getQuays().add(quay1);
        stopPlace.getQuays().add(quay2);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        // Verify that object-references are different
        assertThat(stopPlace != actualStopPlace);

        assertThat(actualStopPlace).isNotNull();
        assertThat(actualStopPlace.getVersion()).isEqualTo(1);
        assertThat(actualStopPlace.getName()).isNotNull();
        assertThat(actualStopPlace.getName().getValue()).isEqualTo(initialStopPlaceName);
        actualStopPlace.getQuays().forEach(quay -> assertThat(quay.getVersion()).isEqualTo(1));

        Map<String, String> versionNameMap = new HashMap<>();
        versionNameMap.put(actualStopPlace.getNetexId() + actualStopPlace.getVersion(), initialStopPlaceName);

        long expectedVersion = actualStopPlace.getVersion();
        String stopPlaceName = null;
        for (int i = 0; i < 3; i++) {
            stopPlaceName = "test " + i;
            StopPlace sp = versionCreator.createCopy(actualStopPlace, StopPlace.class);
            sp.setName(new EmbeddableMultilingualString(stopPlaceName));
            actualStopPlace = stopPlaceVersionedSaverService.saveNewVersion(actualStopPlace, sp);

            // Verify that object-references are different
            assertThat(sp != actualStopPlace);

            actualStopPlace = sp;
            versionNameMap.put(actualStopPlace.getNetexId() + actualStopPlace.getVersion(), stopPlaceName);
            expectedVersion++;
        }

        StopPlace updatedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(actualStopPlace.getNetexId());

        assertThat(updatedStopPlace).isNotNull();
        assertThat(updatedStopPlace.getName()).isNotNull();
        assertThat(updatedStopPlace.getName().getValue()).isEqualTo(stopPlaceName);
        assertThat(updatedStopPlace.getVersion()).isEqualTo(expectedVersion);
        updatedStopPlace.getQuays().forEach(quay -> assertThat(quay.getVersion()).isEqualTo(updatedStopPlace.getVersion()));

        List<StopPlace> all = stopPlaceRepository.findAll();
        all.forEach(sp -> {
            assertThat(versionNameMap.get(sp.getNetexId() + sp.getVersion())).isEqualTo(sp.getName().getValue());
        });
    }

    @Test
    public void updateStopPlaceSameObjectShouldFail() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Initial name"));

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        actualStopPlace.setName(new EmbeddableMultilingualString("Failing StopPlace"));

        boolean failedAsExpected = false;
        try {
            StopPlace fail = stopPlaceVersionedSaverService.saveNewVersion(actualStopPlace, actualStopPlace);
            fail("Saving the same version as new version is not allowed");
        } catch (IllegalArgumentException e) {
            //This should be thrown
            assertThat(e.getMessage()).isEqualTo("Existing and new version must be different objects");
            failedAsExpected = true;
        }
        assertThat(failedAsExpected).isTrue();
    }

    @Test
    public void updateStopPlaceDifferentIdShouldFail() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Initial name"));

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace stopPlace1 = new StopPlace();
        stopPlace1.setName(new EmbeddableMultilingualString("Initial name"));

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace1);

        StopPlace actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());
        StopPlace actualStopPlace1 = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace1.getNetexId());

        actualStopPlace.setName(new EmbeddableMultilingualString("Failing StopPlace"));
        actualStopPlace1.setName(new EmbeddableMultilingualString("Another failing StopPlace"));

        boolean failedAsExpected = false;
        try {
            StopPlace fail = stopPlaceVersionedSaverService.saveNewVersion(actualStopPlace, actualStopPlace1);
            fail("Saving new version of different object is not allowed: " + fail);
        } catch (IllegalArgumentException e) {
            //This should be thrown
            assertThat(e.getMessage()).startsWith("Existing and new entity do not match");
            failedAsExpected = true;
        }
        assertThat(failedAsExpected).isTrue();
    }

    @Test
    public void createNewVersionFromExistingStopPlaceVerifyTwoPersistedCoexistingStops() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.setName(new EmbeddableMultilingualString("versioned stop place"));

        Quay quay = new Quay();
        quay.setVersion(1L);

        stopPlace.getQuays().add(quay);

        stopPlace = stopPlaceRepository.save(stopPlace);

        StopPlace newVersion = versionCreator.createCopy(stopPlace, StopPlace.class);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace, newVersion);
        assertThat(newVersion.getVersion()).isEqualTo(2L);

        StopPlace firstVersion = stopPlaceRepository.findFirstByNetexIdAndVersion(stopPlace.getNetexId(), 1L);
        assertThat(firstVersion).isNotNull();
        StopPlace secondVersion = stopPlaceRepository.findFirstByNetexIdAndVersion(stopPlace.getNetexId(), 2L);
        assertThat(secondVersion).isNotNull();
        assertThat(secondVersion.getQuays()).isNotNull();
        assertThat(secondVersion.getQuays()).hasSize(1);
    }

    @Test
    public void createNewVersionOfStopWithTopographicPlace() {

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlaceRepository.save(topographicPlace);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setTopographicPlace(topographicPlace);
        stopPlace.setVersion(1L);

        StopPlace stopPlace2 = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace newVersion = versionCreator.createCopy(stopPlace2, StopPlace.class);

        // Save it. Reference to topographic place should be kept.
        StopPlace stopPlace3 = stopPlaceVersionedSaverService.saveNewVersion(stopPlace2, newVersion);
        assertThat(stopPlace3.getTopographicPlace()).isNotNull();
    }

    @Test
    public void createNewVersionWithChilds() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.setParentStopPlace(true);

        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace child = new StopPlace();
        child.setVersion(1L);
        child.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId(), stopPlace.getNetexId()));

        StopPlace newVersion = versionCreator.createCopy(stopPlace, StopPlace.class);

        newVersion.getChildren().add(child);

        newVersion = stopPlaceVersionedSaverService.saveNewVersion(stopPlace, newVersion, Instant.now().plusSeconds(1000000005));

        assertThat(newVersion.getChanged())
                .as("new version changed date")
                .isNotNull()
                .isBeforeOrEqualTo(Instant.now());

        newVersion.getChildren().forEach(actualChild -> {
            assertThat(child.getChanged())
                    .as("new version of child changed date")
                    .isNotNull()
                    .isBeforeOrEqualTo(Instant.now());
        });
    }



    @Test
    @Ignore
    public void newVersionOfStopPlaceGetsChangedBySet() {

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlaceRepository.save(topographicPlace);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setTopographicPlace(topographicPlace);
        stopPlace.setVersion(1L);

        StopPlace stopPlace2 = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace newVersion = versionCreator.createCopy(stopPlace2, StopPlace.class);

        final String mockUser = "mockUser";

        Authentication auth = new TestingAuthenticationToken((Principal) () -> mockUser, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        StopPlace stopPlace3 = stopPlaceVersionedSaverService.saveNewVersion(stopPlace2, newVersion);

        assertThat(stopPlace2.getChangedBy()).isNullOrEmpty();
        assertThat(stopPlace3.getChangedBy()).isEqualTo(mockUser);
    }


    @Test
    public void createNewVersionOfStopWithPlaceEquipment() {


        StopPlace stopPlace = new StopPlace();
        PlaceEquipment equipment = new PlaceEquipment();
        SanitaryEquipment sanitaryEquipment = new SanitaryEquipment();
        sanitaryEquipment.setNumberOfToilets(BigInteger.ONE);
        equipment.getInstalledEquipment().add(sanitaryEquipment);

        WaitingRoomEquipment waitingRoomEquipment = new WaitingRoomEquipment();
        waitingRoomEquipment.setHeated(true);
        equipment.getInstalledEquipment().add(waitingRoomEquipment);

        stopPlace.setPlaceEquipments(equipment);
        stopPlace.setVersion(1L);

        StopPlace stopPlace2 = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace newVersion = versionCreator.createCopy(stopPlace2, StopPlace.class);

        // Save it. Reference to topographic place should be kept.
        StopPlace stopPlace3 = stopPlaceVersionedSaverService.saveNewVersion(stopPlace2, newVersion);
        assertThat(stopPlace3.getPlaceEquipments().getInstalledEquipment()).isNotNull();
    }

    @Test
    public void savingChildStopsShouldNotBeAllowed() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setParentStopPlace(false);
        stopPlace.setParentSiteRef(new SiteRefStructure("ref", "1"));

        assertThatThrownBy(() -> stopPlaceVersionedSaverService.saveNewVersion(stopPlace)).isInstanceOf(IllegalArgumentException.class);
    }

    private Point point(double longitude, double latitude) {
        return
                geometryFactory.createPoint(
                        new Coordinate(longitude, latitude));
    }
}
