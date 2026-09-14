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
import org.rutebanken.tiamat.model.ParkingArea;
import org.rutebanken.tiamat.model.ParkingCapacity;
import org.rutebanken.tiamat.model.ParkingProperties;
import org.rutebanken.tiamat.model.ParkingUserEnumeration;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

}
