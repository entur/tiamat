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

package org.rutebanken.tiamat.service.groupofstopplaces;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopPlaceReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class GroupOfStopPlacesCentroidComputerTest extends TiamatIntegrationTest {

    @Autowired
    private GroupOfStopPlacesCentroidComputer groupOfStopPlacesCentroidComputer;

    @Test
    public void compute() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setCentroid(point(60.000, 10.78));

        StopPlace stopPlace2 = new StopPlace();
        stopPlace2.setCentroid(point(60.000, 10.78));

        stopPlaceRepository.save(stopPlace);
        stopPlaceRepository.save(stopPlace2);

        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces();

        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace2.getNetexId()));
        groupOfStopPlacesSaverService.saveNewVersion(groupOfStopPlaces);

        Optional<Point> computedPoint = groupOfStopPlacesCentroidComputer.compute(groupOfStopPlaces);
        assertThat(computedPoint).isPresent();

        assertThat(computedPoint.get()).isEqualTo(stopPlace2.getCentroid());


    }

    private Point point(double longitude, double latitude) {
        return
                geometryFactory.createPoint(
                        new Coordinate(longitude, latitude));
    }
}