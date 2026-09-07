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
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

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
     * Editing a Parking that already has a {@link ParkingProperties} child a second time (without
     * changing the child itself, e.g. GraphQL's "copy previous version, apply edits" flow
     * re-attaching the same logical child with the same netexId/version) used to throw a Postgres
     * duplicate key violation on {@code parking_properties_netex_id_version_constraint}.
     * <p>
     * Root cause: {@link ParkingVersionedSaverService#saveNewVersion(Parking)} deletes the existing
     * Parking (cascading delete of its ParkingProperties children) and saves the new version in the
     * same flush, without an explicit flush in between. Hibernate's action queue executes entity
     * insertions before entity deletions within a single flush, so the INSERT for the new version's
     * (identically netexId/version-ed) ParkingProperties child raced ahead of the DELETE for the old
     * one, violating the unique constraint.
     */
    @Test
    public void saveExistingParkingWithParkingPropertiesTwice_doesNotThrowDuplicateKey() {

        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        Point point = geometryFactory.createPoint(new Coordinate(9.84, 59.26));

        ParkingProperties firstProperties = new ParkingProperties();
        firstProperties.getParkingUserTypes().add(ParkingUserEnumeration.ALL);

        Parking firstVersion = new Parking();
        firstVersion.setCentroid(point);
        firstVersion.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        firstVersion.setParkingProperties(List.of(firstProperties));

        Parking saved = parkingVersionedSaverService.saveNewVersion(firstVersion);
        assertThat(saved.getParkingProperties()).hasSize(1);
        ParkingProperties savedProperties = saved.getParkingProperties().get(0);

        // Simulate a second, unrelated edit (e.g. only the name changes) where the caller
        // re-attaches the SAME logical ParkingProperties child, carrying over its already
        // persisted netexId/version, as GraphQL's copy-and-edit flow does.
        ParkingProperties reattachedProperties = new ParkingProperties();
        reattachedProperties.setNetexId(savedProperties.getNetexId());
        reattachedProperties.setVersion(savedProperties.getVersion());
        reattachedProperties.getParkingUserTypes().add(ParkingUserEnumeration.ALL);

        Parking secondEdit = new Parking();
        secondEdit.setNetexId(saved.getNetexId());
        secondEdit.setName(new EmbeddableMultilingualString("name"));
        secondEdit.setCentroid(point);
        secondEdit.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        secondEdit.setParkingProperties(List.of(reattachedProperties));

        assertThatCode(() -> parkingVersionedSaverService.saveNewVersion(secondEdit))
                .as("saving a second edit that re-attaches an existing ParkingProperties child " +
                        "must not throw a duplicate key violation on parking_properties_netex_id_version_constraint")
                .doesNotThrowAnyException();
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
     * as the already-persisted one (see #432). This must not violate
     * parking_entrance_for_vehicles_netex_id_version_constraint.
     */
    @Test
    public void reattachVehicleEntranceWithSameNetexIdAndVersion_doesNotThrowDuplicateKey() {
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

        ParkingEntranceForVehicles reattached = entrance("A");
        reattached.setNetexId(savedEntrance.getNetexId());
        reattached.setVersion(savedEntrance.getVersion());

        Parking secondEdit = new Parking();
        secondEdit.setNetexId(saved.getNetexId());
        secondEdit.setName(new EmbeddableMultilingualString("Parking reattach v2"));
        secondEdit.setCentroid(point);
        secondEdit.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));
        secondEdit.getVehicleEntrances().add(reattached);

        assertThatCode(() -> parkingVersionedSaverService.saveNewVersion(secondEdit))
                .as("re-attaching an entrance with an existing (netexId, version) must not throw "
                        + "a duplicate key violation on "
                        + "parking_entrance_for_vehicles_netex_id_version_constraint")
                .doesNotThrowAnyException();
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