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

package org.rutebanken.tiamat.service;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.springframework.data.util.Pair;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TariffZonesLookupServiceTest {


    private TariffZoneRepository tariffZoneRepository = mock(TariffZoneRepository.class);
    private FareZoneRepository fareZoneRepository = mock(FareZoneRepository.class);

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    private TariffZonesLookupService tariffZonesLookupService = new TariffZonesLookupService(tariffZoneRepository,fareZoneRepository, false);

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

        when(tariffZoneRepository.findAllValidTariffZones()).thenReturn(Arrays.asList(firstVersion, secondVersion, anotherOne));


        java.util.function.Supplier<List<Pair<String, Geometry>>> actual = tariffZonesLookupService.getTariffZones();

        assertThat(actual.get()).hasSize(2);

    }

}