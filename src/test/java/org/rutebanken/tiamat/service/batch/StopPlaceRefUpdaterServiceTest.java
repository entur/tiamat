package org.rutebanken.tiamat.service.batch;

import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore // TODO
public class StopPlaceRefUpdaterServiceTest extends TiamatIntegrationTest {

    @Autowired
    private StopPlaceRefUpdaterService stopPlaceRefUpdaterService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public StopPlace setup() {
        TariffZone tariffZone = new TariffZone();
        tariffZone.setVersion(1L);

        Point point = geometryFactory.createPoint(new Coordinate(9.84, 59.26));
        Geometry bufferedPoint = point.buffer(20);


        LinearRing tariffZoneLinearRing = new LinearRing(new CoordinateArraySequence(bufferedPoint.getCoordinates()), geometryFactory);
        tariffZone.setPolygon(geometryFactory.createPolygon(tariffZoneLinearRing, null));

        tariffZoneRepository.saveAndFlush(tariffZone);

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setVersion(1L);
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);

        LinearRing topographicPlaceLinearRing = new LinearRing(new CoordinateArraySequence(bufferedPoint.getCoordinates()), geometryFactory);
        topographicPlace.setPolygon(geometryFactory.createPolygon(topographicPlaceLinearRing, null));

        topographicPlaceRepository.saveAndFlush(topographicPlace);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("some stop place"));
        stopPlace.setCentroid(point);
        stopPlace.setVersion(1L);

        stopPlaceRepository.saveAndFlush(stopPlace);

        return stopPlace;
    }

    @Transactional
    @Test
    public void updateAllStopPlaces() {

        StopPlace stopPlace = setup();

        stopPlaceRefUpdaterService.updateAllStopPlaces();
        stopPlaceRepository.flush();

        System.out.println("About to find the updated stop");
        StopPlace actual = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        System.out.println("changed: " + actual.getChanged());


        System.out.println("Check the result");
        assertThat(actual.getTopographicPlace())
                .as("updated stop place topographic place")
                .isNotNull();

        assertThat(actual.getTariffZones())
                .as("updated stop place tariff zones size")
                .hasSize(1);

    }
}