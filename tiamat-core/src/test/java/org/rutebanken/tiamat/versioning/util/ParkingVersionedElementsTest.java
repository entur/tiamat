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

package org.rutebanken.tiamat.versioning.util;

import org.junit.Test;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.ParkingArea;
import org.rutebanken.tiamat.model.ParkingCapacity;
import org.rutebanken.tiamat.model.ParkingEntranceForVehicles;
import org.rutebanken.tiamat.model.ParkingProperties;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ParkingVersionedElementsTest {

    private final ParkingVersionedElements parkingVersionedElements = new ParkingVersionedElements();

    /**
     * Every entity below a parking has a unique constraint on (netex_id, version), so the
     * traversal must reach all of them. A branch that the traversal misses keeps a stale version
     * and breaks that constraint on the next save.
     */
    @Test
    public void visitsTheParkingAndEveryVersionedEntityBelowIt() {

        Parking parking = new Parking();
        parking.setNetexId("NSR:Parking:1");

        ParkingCapacity capacity = new ParkingCapacity();
        ParkingProperties properties = new ParkingProperties();
        properties.setSpaces(List.of(capacity));
        parking.setParkingProperties(List.of(properties));

        ParkingCapacity areaCapacity = new ParkingCapacity();
        ParkingProperties areaProperties = new ParkingProperties();
        areaProperties.setSpaces(List.of(areaCapacity));
        ParkingArea parkingArea = new ParkingArea();
        parkingArea.setParkingProperties(areaProperties);
        parking.setParkingAreas(List.of(parkingArea));

        ParkingEntranceForVehicles vehicleEntrance = new ParkingEntranceForVehicles();
        parking.getVehicleEntrances().add(vehicleEntrance);

        List<EntityInVersionStructure> visited = new ArrayList<>();
        parkingVersionedElements.forEach(parking, visited::add);

        assertThat(visited).containsExactlyInAnyOrder(
                parking,
                properties,
                capacity,
                parkingArea,
                areaProperties,
                areaCapacity,
                vehicleEntrance);
    }

    /** A parking with no children at all must not fail the traversal. */
    @Test
    public void visitsOnlyTheParkingWhenItHasNoChildren() {

        Parking parking = new Parking();
        parking.setNetexId("NSR:Parking:1");

        List<EntityInVersionStructure> visited = new ArrayList<>();
        parkingVersionedElements.forEach(parking, visited::add);

        assertThat(visited).containsExactly(parking);
    }
}
