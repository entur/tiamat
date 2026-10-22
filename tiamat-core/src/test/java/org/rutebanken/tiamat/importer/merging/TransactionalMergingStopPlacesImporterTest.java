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

package org.rutebanken.tiamat.importer.merging;


import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.LimitationStatusEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionalMergingStopPlacesImporterTest extends TiamatIntegrationTest {

    @Autowired
    private TransactionalMergingStopPlacesImporter siteFrameImporter;

    /**
     * This test is implemented to reproduce an issue we had with lazy initialization exception
     * when returning a stop place that is already persisted, found by looking at imported key.
     * The test seems to be easier to reproduce if cache is disabled in {@link org.rutebanken.tiamat.repository.StopPlaceRepository}.
     */
    @Test
    public void lazyInitializationException() {

        int stopPlaces = 2;
        Random random = new Random();

        List<StopPlace> stopPlacesCreated = new ArrayList<>();

        for (int stopPlaceIndex = 0; stopPlaceIndex < stopPlaces; stopPlaceIndex++) {

            StopPlace stopPlace = new StopPlace();
            stopPlace.setNetexId(String.valueOf(stopPlaceIndex * Math.abs(random.nextLong())));

            double longitude = 39.61441 * Math.abs(random.nextDouble());
            double latitude = -144.22765 * Math.abs(random.nextDouble());

            stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
            stopPlacesCreated.add(stopPlace);
        }

        siteFrameImporter.importStopPlaces(stopPlacesCreated, new AtomicInteger());
    }


    @Test
    public void uniqueByIdAndVersion() {

        List<org.entur.netex2.model.StopPlace> stopPlacesWithDuplicates = new ArrayList<>();

        org.entur.netex2.model.StopPlace stopPlace1 = new org.entur.netex2.model.StopPlace();
        stopPlace1.setId("100");
        stopPlace1.setVersion("10");

        org.entur.netex2.model.StopPlace stopPlace2 = new org.entur.netex2.model.StopPlace();
        stopPlace2.setId("100");
        stopPlace2.setVersion("1");

        stopPlacesWithDuplicates.add(stopPlace2);
        stopPlacesWithDuplicates.add(stopPlace1);

        Collection<org.entur.netex2.model.StopPlace> actual = siteFrameImporter.distinctByIdAndHighestVersion(stopPlacesWithDuplicates);

        assertThat(actual).hasSize(1);
        assertThat(actual).containsOnlyOnce(stopPlace1);
        assertThat(actual).extracting(org.entur.netex2.model.StopPlace::getVersion).containsOnly("10");

    }

    /**
     * Test added to reprocude NRP-1366
     */
    @Test
    public void importStopPlaceWithAccessibilityAssessment() {

        StopPlace stopPlace = new StopPlace();
        AccessibilityAssessment aa = new AccessibilityAssessment();
        List<AccessibilityLimitation> limitations = new ArrayList<>();

        AccessibilityLimitation limitation = new AccessibilityLimitation();
        limitation.setWheelchairAccess(LimitationStatusEnumeration.TRUE);

        limitations.add(limitation);

        aa.setLimitations(limitations);
        stopPlace.setAccessibilityAssessment(aa);

        List<StopPlace> sp = new ArrayList<>();
        sp.add(stopPlace);

        AtomicInteger counter = new AtomicInteger();
        Collection<org.entur.netex2.model.StopPlace> importStopPlaces = siteFrameImporter.importStopPlaces(sp, counter);


        assertThat(importStopPlaces).hasSize(1);
        org.entur.netex2.model.StopPlace importedStopPlace = importStopPlaces.iterator().next();
        assertThat(importedStopPlace).isNotNull();
        assertThat(importedStopPlace.getAccessibilityAssessment()).isNotNull();
        assertThat(importedStopPlace.getAccessibilityAssessment().getLimitations()).isNotNull();
        assertThat(importedStopPlace.getAccessibilityAssessment().getLimitations().getAccessibilityLimitation()).isNotNull();
        org.entur.netex2.model.AccessibilityLimitation accessibilityLimitation = importedStopPlace.getAccessibilityAssessment().getLimitations().getAccessibilityLimitation();

        assertThat(accessibilityLimitation).isNotNull();
        assertThat(accessibilityLimitation.getWheelchairAccess()).isNotNull();
        assertThat(accessibilityLimitation.getWheelchairAccess().value()).isEqualTo(limitation.getWheelchairAccess().value());
    }
}