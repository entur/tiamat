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
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.ParkingProperties;
import org.rutebanken.tiamat.model.ParkingUserEnumeration;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class ParkingVersionedSaverServiceTest extends TiamatIntegrationTest {

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private ParkingVersionedSaverService parkingVersionedSaverService;

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

}