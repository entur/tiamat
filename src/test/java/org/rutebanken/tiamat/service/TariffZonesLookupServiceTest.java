package org.rutebanken.tiamat.service;

import com.google.common.base.Supplier;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import org.junit.Ignore;
import org.junit.Test;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.springframework.data.util.Pair;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TariffZonesLookupServiceTest {


    private TariffZoneRepository tariffZoneRepository = mock(TariffZoneRepository.class);

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    private TariffZonesLookupService tariffZonesLookupService = new TariffZonesLookupService(tariffZoneRepository);

    @Ignore
    @Test
    public void getTariffZones() {

        TariffZone firstVersion = new TariffZone();
        firstVersion.setNetexId("NSR:TariffZone:1");
        firstVersion.setVersion(1L);

        Point point = geometryFactory.createPoint(new Coordinate(9.84, 59.26));
        Geometry geometry =  point.buffer(20);
        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(geometry.getCoordinates()), geometryFactory);
        firstVersion.setPolygon(geometryFactory.createPolygon(linearRing, null));

        TariffZone secondVersion = new TariffZone();
        secondVersion.setNetexId("NSR:TariffZone:1");
        secondVersion.setVersion(2L);
        secondVersion.setPolygon(geometryFactory.createPolygon(linearRing, null));

        TariffZone anotherOne = new TariffZone();
        anotherOne.setNetexId("NSR:TariffZone:2");
        anotherOne.setVersion(3L);
        anotherOne.setPolygon(geometryFactory.createPolygon(linearRing, null));

        when(tariffZoneRepository.findAll()).thenReturn(Arrays.asList(firstVersion, secondVersion, anotherOne));


        Supplier<List<Pair<String, Polygon>>> actual = tariffZonesLookupService.getTariffZones();

        assertThat(actual.get()).hasSize(2);

    }

}