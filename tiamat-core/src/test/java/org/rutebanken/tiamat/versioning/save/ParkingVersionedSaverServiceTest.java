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

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.EntranceEnumeration;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.ParkingArea;
import org.rutebanken.tiamat.model.ParkingCapacity;
import org.rutebanken.tiamat.model.ParkingProperties;
import org.rutebanken.tiamat.model.ParkingUserEnumeration;
import org.rutebanken.tiamat.model.ParkingEntranceForVehicles;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ParkingVersionedSaverServiceTest extends TiamatIntegrationTest {

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private ParkingVersionedSaverService parkingVersionedSaverService;

    @Autowired
    private VersionCreator versionCreator;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /** Reads inside a transaction, because vehicleEntrances is LAZY like parkingAreas and quays. */
    private <T> T inTransaction(Supplier<T> supplier) {
        return transactionTemplate.execute(status -> supplier.get());
    }

    private ParkingEntranceForVehicles entrance(String label) {
        ParkingEntranceForVehicles e = new ParkingEntranceForVehicles();
        e.setLabel(new EmbeddableMultilingualString(label));
        e.setEntranceType(EntranceEnumeration.GATE);
        e.setWidth(new BigDecimal("2.50"));
        e.setHeight(new BigDecimal("2.10"));
        e.setIsEntry(true);
        e.setIsExit(false);
        e.setPublicCode("PC-" + label);
        return e;
    }

    private long entranceRowCount() {
        return inTransaction(() -> em.createQuery("select count(e) from ParkingEntranceForVehicles e", Long.class)
                .getSingleResult());
    }

    @Test
    public void saveNewParking() {

        Parking newVersion = new Parking();

        Point point = geometryFactory.createPoint(new Coordinate(9.84, 59.26));
        newVersion.setCentroid(point);
        newVersion.setParentSiteRef(new SiteRefStructure(stopPlaceRepository.save(new StopPlace()).getNetexId()));

        Parking actual = parkingVersionedSaverService.saveNewVersion(newVersion);
        assertThat(actual.getVersion()).isOne();
    }


    @Test
    public void saveExistingParking() {

        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        Parking existingParking = new Parking();
        Point point = geometryFactory.createPoint(new Coordinate(9.84, 59.26));
        existingParking.setCentroid(point);
        existingParking.setVersion(2L);
        existingParking.setCreated(Instant.now());
        existingParking.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        parkingRepository.save(existingParking);

        Parking newParking = new Parking();
        newParking.setNetexId(existingParking.getNetexId());
        newParking.setName(new EmbeddableMultilingualString("name"));
        newParking.setCentroid(null);
        newParking.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));

        Parking actual = parkingVersionedSaverService.saveNewVersion(newParking);
        assertThat(actual.getCentroid()).isNull();
        assertThat(actual.getVersion()).isEqualTo(3L);
        assertThat(actual.getName().getValue()).isEqualTo(newParking.getName().getValue());
        assertThat(actual.getChanged()).as("changed").isNotNull();
        assertThat(actual.getCreated()).as("created").isNotNull();
    }

    /**
     * Each versioned child of a Parking has a unique constraint on (netex_id, version).
     * Re-attaching an existing child without incrementing its version, as GraphQL's
     * "copy previous version, apply edits" flow does, made the new version's child row clash
     * with the row of the version being replaced.
     */
    @Test
    public void saveExistingParkingIncrementsVersionsOfReattachedChildren() {

        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        Point point = geometryFactory.createPoint(new Coordinate(9.84, 59.26));

        ParkingCapacity firstCapacity = new ParkingCapacity();
        firstCapacity.setNumberOfSpaces(new BigInteger("10"));

        ParkingProperties firstProperties = new ParkingProperties();
        firstProperties.getParkingUserTypes().add(ParkingUserEnumeration.ALL);
        firstProperties.setSpaces(List.of(firstCapacity));

        Parking firstVersion = new Parking();
        firstVersion.setCentroid(point);
        firstVersion.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        firstVersion.setParkingProperties(List.of(firstProperties));

        Parking saved = parkingVersionedSaverService.saveNewVersion(firstVersion);

        assertThat(saved.getParkingProperties()).hasSize(1);
        ParkingProperties savedProperties = saved.getParkingProperties().getFirst();
        assertThat(savedProperties.getNetexId()).as("properties netexId").isNotNull();
        assertThat(savedProperties.getVersion()).as("properties version").isEqualTo(1L);
        assertThat(savedProperties.getSpaces()).hasSize(1);
        ParkingCapacity savedCapacity = savedProperties.getSpaces().getFirst();
        assertThat(savedCapacity.getVersion()).as("capacity version").isEqualTo(1L);

        // Second edit re-attaching the same logical children, carrying over their already
        // persisted netexId and version, as the GraphQL copy-and-edit flow does.
        ParkingCapacity reattachedCapacity = new ParkingCapacity();
        reattachedCapacity.setNetexId(savedCapacity.getNetexId());
        reattachedCapacity.setVersion(savedCapacity.getVersion());
        reattachedCapacity.setNumberOfSpaces(new BigInteger("20"));

        ParkingProperties reattachedProperties = new ParkingProperties();
        reattachedProperties.setNetexId(savedProperties.getNetexId());
        reattachedProperties.setVersion(savedProperties.getVersion());
        reattachedProperties.getParkingUserTypes().add(ParkingUserEnumeration.ALL);
        reattachedProperties.setSpaces(List.of(reattachedCapacity));

        Parking secondEdit = new Parking();
        secondEdit.setNetexId(saved.getNetexId());
        secondEdit.setName(new EmbeddableMultilingualString("name"));
        secondEdit.setCentroid(point);
        secondEdit.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        secondEdit.setParkingProperties(List.of(reattachedProperties));

        Parking secondSaved = parkingVersionedSaverService.saveNewVersion(secondEdit);

        assertThat(secondSaved.getVersion()).as("parking version").isEqualTo(2L);

        ParkingProperties secondProperties = secondSaved.getParkingProperties().getFirst();
        assertThat(secondProperties.getNetexId())
                .as("properties netexId is kept across versions")
                .isEqualTo(savedProperties.getNetexId());
        assertThat(secondProperties.getVersion())
                .as("properties version follows the parking version")
                .isEqualTo(2L);
        assertThat(secondProperties.getSpaces().getFirst().getVersion())
                .as("capacity version follows the parking version")
                .isEqualTo(2L);
    }

    /**
     * A child must follow the version of its parking even when it arrives with a stale version.
     * An import declares every child at version 1, because the source document has no version to
     * map from. A child that keeps that version always moves to version 2 and collides with the
     * row that the previous import wrote.
     *
     * <p>The parking carries one child of each shape that the traversal reaches: properties
     * directly on the parking, a parking area with properties of its own, and a vehicle entrance.
     */
    @Test
    public void saveExistingParkingIncrementsVersionsOfChildrenThatArriveWithAStaleVersion() {

        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        Point point = geometryFactory.createPoint(new Coordinate(9.84, 59.26));

        ParkingArea firstArea = new ParkingArea();
        firstArea.setTotalCapacity(new BigInteger("10"));
        firstArea.setParkingProperties(newProperties());

        Parking firstVersion = new Parking();
        firstVersion.setCentroid(point);
        firstVersion.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        firstVersion.setParkingProperties(List.of(newProperties()));
        firstVersion.setParkingAreas(List.of(firstArea));
        firstVersion.getVehicleEntrances().add(entrance("A"));

        Parking saved = parkingVersionedSaverService.saveNewVersion(firstVersion);
        assertThat(saved.getParkingProperties().getFirst().getVersion()).as("properties version").isEqualTo(1L);

        // The second import repeats the same document. Each child keeps the netexId that the first
        // import assigned, and carries the version from the document, which is 1 again.
        Parking secondSaved = parkingVersionedSaverService.saveNewVersion(
                staleImportOf(saved, stopPlace.getNetexId(), point));

        assertThat(secondSaved.getVersion()).as("parking version").isEqualTo(2L);

        // A third import repeats it once more. Without alignment each child targets version 2
        // again, and collides with the row that the second import wrote.
        Parking thirdSaved = parkingVersionedSaverService.saveNewVersion(
                staleImportOf(secondSaved, stopPlace.getNetexId(), point));

        assertThat(thirdSaved.getVersion()).as("parking version").isEqualTo(3L);

        ParkingProperties thirdProperties = thirdSaved.getParkingProperties().getFirst();
        assertThat(thirdProperties.getVersion())
                .as("properties version follows the parking version, not the version in the document")
                .isEqualTo(3L);
        assertThat(thirdProperties.getSpaces().getFirst().getVersion())
                .as("capacity version follows the parking version, not the version in the document")
                .isEqualTo(3L);

        ParkingArea thirdArea = thirdSaved.getParkingAreas().getFirst();
        assertThat(thirdArea.getVersion())
                .as("area version follows the parking version")
                .isEqualTo(3L);
        assertThat(thirdArea.getParkingProperties().getVersion())
                .as("area properties version follows the parking version")
                .isEqualTo(3L);
        assertThat(thirdArea.getParkingProperties().getSpaces().getFirst().getVersion())
                .as("area capacity version follows the parking version")
                .isEqualTo(3L);

        assertThat(thirdSaved.getVehicleEntrances().iterator().next().getVersion())
                .as("vehicle entrance version follows the parking version")
                .isEqualTo(3L);
    }

    private ParkingProperties newProperties() {
        ParkingCapacity capacity = new ParkingCapacity();
        capacity.setNumberOfSpaces(new BigInteger("10"));

        ParkingProperties properties = new ParkingProperties();
        properties.getParkingUserTypes().add(ParkingUserEnumeration.ALL);
        properties.setSpaces(List.of(capacity));
        return properties;
    }

    /**
     * Builds the next import of a parking that is already stored. Each child keeps the netexId
     * that the previous import gave it, and carries version 1, which is the version an import
     * document declares for every element.
     */
    private Parking staleImportOf(Parking stored, String stopPlaceNetexId, Point point) {

        ParkingArea storedArea = stored.getParkingAreas().getFirst();
        ParkingArea area = new ParkingArea();
        area.setNetexId(storedArea.getNetexId());
        area.setVersion(1L);
        area.setTotalCapacity(new BigInteger("10"));
        area.setParkingProperties(stalePropertiesOf(storedArea.getParkingProperties()));

        ParkingEntranceForVehicles storedEntrance = stored.getVehicleEntrances().iterator().next();
        ParkingEntranceForVehicles vehicleEntrance = entrance("A");
        vehicleEntrance.setNetexId(storedEntrance.getNetexId());
        vehicleEntrance.setVersion(1L);

        Parking next = new Parking();
        next.setNetexId(stored.getNetexId());
        next.setCentroid(point);
        next.setParentSiteRef(new SiteRefStructure(stopPlaceNetexId));
        next.setParkingProperties(List.of(stalePropertiesOf(stored.getParkingProperties().getFirst())));
        next.setParkingAreas(List.of(area));
        next.getVehicleEntrances().add(vehicleEntrance);
        return next;
    }

    private ParkingProperties stalePropertiesOf(ParkingProperties stored) {
        ParkingCapacity capacity = new ParkingCapacity();
        capacity.setNetexId(stored.getSpaces().getFirst().getNetexId());
        capacity.setVersion(1L);
        capacity.setNumberOfSpaces(new BigInteger("10"));

        ParkingProperties properties = new ParkingProperties();
        properties.setNetexId(stored.getNetexId());
        properties.setVersion(1L);
        properties.getParkingUserTypes().add(ParkingUserEnumeration.ALL);
        properties.setSpaces(List.of(capacity));
        return properties;
    }

    /**
     * A ParkingArea carries its own ParkingProperties, which in turn carries its own spaces.
     * Both are versioned and have the same unique constraint on (netex_id, version) as the
     * properties hanging directly off the Parking, so they clash the same way when re-attached.
     */
    @Test
    public void saveExistingParkingIncrementsVersionsOfReattachedParkingAreaChildren() {

        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        Point point = geometryFactory.createPoint(new Coordinate(9.84, 59.26));

        ParkingCapacity firstCapacity = new ParkingCapacity();
        firstCapacity.setNumberOfSpaces(new BigInteger("10"));

        ParkingProperties firstProperties = new ParkingProperties();
        firstProperties.getParkingUserTypes().add(ParkingUserEnumeration.ALL);
        firstProperties.setSpaces(List.of(firstCapacity));

        ParkingArea firstArea = new ParkingArea();
        firstArea.setTotalCapacity(new BigInteger("10"));
        firstArea.setParkingProperties(firstProperties);

        Parking firstVersion = new Parking();
        firstVersion.setCentroid(point);
        firstVersion.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        firstVersion.setParkingAreas(List.of(firstArea));

        Parking saved = parkingVersionedSaverService.saveNewVersion(firstVersion);

        assertThat(saved.getParkingAreas()).hasSize(1);
        ParkingArea savedArea = saved.getParkingAreas().getFirst();
        ParkingProperties savedAreaProperties = savedArea.getParkingProperties();
        assertThat(savedAreaProperties).as("area properties").isNotNull();
        assertThat(savedAreaProperties.getVersion()).as("area properties version").isEqualTo(1L);
        ParkingCapacity savedAreaCapacity = savedAreaProperties.getSpaces().getFirst();
        assertThat(savedAreaCapacity.getVersion()).as("area capacity version").isEqualTo(1L);

        // Second edit re-attaching the same logical children, carrying over their already
        // persisted netexId and version, as the GraphQL copy-and-edit flow does.
        ParkingCapacity reattachedCapacity = new ParkingCapacity();
        reattachedCapacity.setNetexId(savedAreaCapacity.getNetexId());
        reattachedCapacity.setVersion(savedAreaCapacity.getVersion());
        reattachedCapacity.setNumberOfSpaces(new BigInteger("20"));

        ParkingProperties reattachedProperties = new ParkingProperties();
        reattachedProperties.setNetexId(savedAreaProperties.getNetexId());
        reattachedProperties.setVersion(savedAreaProperties.getVersion());
        reattachedProperties.getParkingUserTypes().add(ParkingUserEnumeration.ALL);
        reattachedProperties.setSpaces(List.of(reattachedCapacity));

        ParkingArea reattachedArea = new ParkingArea();
        reattachedArea.setNetexId(savedArea.getNetexId());
        reattachedArea.setVersion(savedArea.getVersion());
        reattachedArea.setTotalCapacity(new BigInteger("20"));
        reattachedArea.setParkingProperties(reattachedProperties);

        Parking secondEdit = new Parking();
        secondEdit.setNetexId(saved.getNetexId());
        secondEdit.setName(new EmbeddableMultilingualString("name"));
        secondEdit.setCentroid(point);
        secondEdit.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        secondEdit.setParkingAreas(List.of(reattachedArea));

        Parking secondSaved = parkingVersionedSaverService.saveNewVersion(secondEdit);

        assertThat(secondSaved.getVersion()).as("parking version").isEqualTo(2L);

        ParkingArea secondArea = secondSaved.getParkingAreas().getFirst();
        assertThat(secondArea.getVersion())
                .as("area version follows the parking version")
                .isEqualTo(2L);

        ParkingProperties secondAreaProperties = secondArea.getParkingProperties();
        assertThat(secondAreaProperties.getNetexId())
                .as("area properties netexId is kept across versions")
                .isEqualTo(savedAreaProperties.getNetexId());
        assertThat(secondAreaProperties.getVersion())
                .as("area properties version follows the parking version")
                .isEqualTo(2L);
        assertThat(secondAreaProperties.getSpaces().getFirst().getVersion())
                .as("area capacity version follows the parking version")
                .isEqualTo(2L);
    }

    /**
     * Regression cover for resolveParentStopPlace, which is the only thing preventing a parking
     * from being saved with a parent site ref that does not resolve.
     */
    @Test
    public void saveParkingWithUnresolvableParentSiteRefIsRejected() {

        Parking parking = new Parking();
        parking.setCentroid(geometryFactory.createPoint(new Coordinate(9.84, 59.26)));
        parking.setParentSiteRef(new SiteRefStructure("NSR:StopPlace:doesnotexist"));

        assertThatThrownBy(() -> parkingVersionedSaverService.saveNewVersion(parking))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("NSR:StopPlace:doesnotexist");

        assertThat(parkingRepository.findAll()).isEmpty();
    }

    /** Saves a parking with two vehicle entrances, reloads it, and checks ids/netexIds/versions and precision-sensitive fields survive. */
    @Test
    public void saveAndReloadParkingWithTwoVehicleEntrances() {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);
        Point point = geometryFactory.createPoint(new Coordinate(9.84, 59.26));

        Parking parking = new Parking();
        parking.setName(new EmbeddableMultilingualString("Parking with entrances"));
        parking.setCentroid(point);
        parking.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        parking.getVehicleEntrances().add(entrance("A"));
        parking.getVehicleEntrances().add(entrance("B"));

        Parking saved = parkingVersionedSaverService.saveNewVersion(parking);
        parkingRepository.flush();

        List<ParkingEntranceForVehicles> reloaded = inTransaction(() -> {
            Parking p = parkingRepository.findFirstByNetexIdOrderByVersionDesc(saved.getNetexId());
            assertThat(p).isNotNull();
            p.getVehicleEntrances().size();
            return List.copyOf(p.getVehicleEntrances());
        });

        assertThat(reloaded).hasSize(2);
        for (ParkingEntranceForVehicles e : reloaded) {
            assertThat(e.getId()).as("entrance must get a database id").isNotNull();
            assertThat(e.getNetexId()).as("entrance must get a netexId").isNotNull();
            assertThat(e.getVersion()).as("entrance must get a version").isNotNull();
            assertThat(e.getWidth()).as("width must survive the round trip").isEqualByComparingTo("2.50");
            assertThat(e.getHeight()).as("height must survive the round trip").isEqualByComparingTo("2.10");
        }
    }

    /** Replacing the entrance list on an update must not leave orphaned rows behind (orphanRemoval). */
    @Test
    public void updateParkingWithChangedVehicleEntranceList_doesNotLeaveOrphanedRows() {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);
        Point point = geometryFactory.createPoint(new Coordinate(9.84, 59.26));

        Parking firstVersion = new Parking();
        firstVersion.setName(new EmbeddableMultilingualString("Parking update"));
        firstVersion.setCentroid(point);
        firstVersion.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        firstVersion.getVehicleEntrances().add(entrance("A"));
        firstVersion.getVehicleEntrances().add(entrance("B"));

        Parking saved = parkingVersionedSaverService.saveNewVersion(firstVersion);
        parkingRepository.flush();

        Parking secondEdit = new Parking();
        secondEdit.setNetexId(saved.getNetexId());
        secondEdit.setName(new EmbeddableMultilingualString("Parking update v2"));
        secondEdit.setCentroid(point);
        secondEdit.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        secondEdit.getVehicleEntrances().add(entrance("C"));

        assertThatCode(() -> parkingVersionedSaverService.saveNewVersion(secondEdit))
                .as("replacing the entrance list must not throw")
                .doesNotThrowAnyException();
        parkingRepository.flush();

        assertThat(entranceRowCount()).as("orphaned entrance rows must not accumulate").isEqualTo(1L);

        int reloadedSize = inTransaction(() ->
                parkingRepository.findFirstByNetexIdOrderByVersionDesc(saved.getNetexId())
                        .getVehicleEntrances().size());
        assertThat(reloadedSize).isEqualTo(1);
    }

    /**
     * The GraphQL copy-and-edit flow can re-attach an entrance carrying the SAME netexId/version
     * as the already-persisted one (see #432). Like every other versioned child of a Parking, the
     * entrance must get its version incremented so the new row does not clash with the row of the
     * version being replaced on parking_entrance_for_vehicles_netex_id_version_constraint.
     */
    @Test
    public void reattachVehicleEntranceWithSameNetexIdAndVersion_incrementsEntranceVersion() {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);
        Point point = geometryFactory.createPoint(new Coordinate(9.84, 59.26));

        Parking firstVersion = new Parking();
        firstVersion.setName(new EmbeddableMultilingualString("Parking reattach"));
        firstVersion.setCentroid(point);
        firstVersion.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        firstVersion.getVehicleEntrances().add(entrance("A"));

        Parking saved = parkingVersionedSaverService.saveNewVersion(firstVersion);
        parkingRepository.flush();

        ParkingEntranceForVehicles savedEntrance = saved.getVehicleEntrances().get(0);
        assertThat(savedEntrance.getVersion()).as("entrance version").isEqualTo(1L);

        ParkingEntranceForVehicles reattached = entrance("A");
        reattached.setNetexId(savedEntrance.getNetexId());
        reattached.setVersion(savedEntrance.getVersion());

        Parking secondEdit = new Parking();
        secondEdit.setNetexId(saved.getNetexId());
        secondEdit.setName(new EmbeddableMultilingualString("Parking reattach v2"));
        secondEdit.setCentroid(point);
        secondEdit.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        secondEdit.getVehicleEntrances().add(reattached);

        Parking secondSaved = parkingVersionedSaverService.saveNewVersion(secondEdit);

        assertThat(secondSaved.getVersion()).as("parking version").isEqualTo(2L);

        ParkingEntranceForVehicles secondEntrance = secondSaved.getVehicleEntrances().get(0);
        assertThat(secondEntrance.getNetexId())
                .as("entrance netexId is kept across versions")
                .isEqualTo(savedEntrance.getNetexId());
        assertThat(secondEntrance.getVersion())
                .as("entrance version follows the parking version")
                .isEqualTo(2L);
    }

    /**
     * The re-import path (MergingParkingImporter.handleAlreadyExistingParking) does
     * versionCreator.createCopy(existing) and then saves it. If createCopy does not deep-copy the
     * entrances, both parking versions would reference the same entrance rows through the
     * parking_vehicle_entrances join table, whose vehicle_entrances_id column is UNIQUE.
     */
    @Test
    public void createCopyThenSaveNewVersion_deepCopiesVehicleEntrances() {
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);
        Point point = geometryFactory.createPoint(new Coordinate(9.84, 59.26));

        Parking firstVersion = new Parking();
        firstVersion.setName(new EmbeddableMultilingualString("Parking createCopy"));
        firstVersion.setCentroid(point);
        firstVersion.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        firstVersion.getVehicleEntrances().add(entrance("A"));
        firstVersion.getVehicleEntrances().add(entrance("B"));

        Parking saved = parkingVersionedSaverService.saveNewVersion(firstVersion);
        parkingRepository.flush();

        Parking copy = inTransaction(() -> {
            Parking managed = parkingRepository.findFirstByNetexIdOrderByVersionDesc(saved.getNetexId());
            managed.getVehicleEntrances().size();
            Parking c = versionCreator.createCopy(managed, Parking.class);
            assertThat(c.getVehicleEntrances()).hasSize(2);
            c.getVehicleEntrances().forEach(e -> assertThat(e.getId())
                    .as("createCopy must produce a genuine deep copy of vehicleEntrances")
                    .isNull());
            return c;
        });

        assertThatCode(() -> parkingVersionedSaverService.saveNewVersion(copy))
                .as("saving a createCopy() of a parking with entrances must not violate "
                        + "parking_vehicle_entrances.vehicle_entrances_id UNIQUE")
                .doesNotThrowAnyException();
    }

}
