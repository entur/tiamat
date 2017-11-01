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

package org.rutebanken.tiamat.versioning;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import org.junit.Test;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.rutebanken.tiamat.service.TariffZonesLookupService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TariffZoneSaverServiceTest {

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();
    private TariffZoneRepository tariffZoneRepository = mock(TariffZoneRepository.class);
    private TariffZonesLookupService tariffZonesLookupService = mock(TariffZonesLookupService.class);

    private TariffZoneSaverService tariffZoneSaverService = new TariffZoneSaverService(tariffZoneRepository, tariffZonesLookupService);

    @Test
    public void saveNewTariffZone() {

        TariffZone newVersion = new TariffZone();

        Geometry geometry = geometryFactory.createPoint(new Coordinate(9.84, 59.26)).buffer(20);
        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(geometry.getCoordinates()), geometryFactory);
        newVersion.setPolygon(geometryFactory.createPolygon(linearRing, null));


        when(tariffZoneRepository.save(newVersion)).thenAnswer((answer) -> {
            TariffZone tariffZone = (TariffZone) answer.getArguments()[0];
            tariffZone.setNetexId(NetexIdHelper.generateRandomizedNetexId(tariffZone));
            return tariffZone;
        });

        TariffZone actual = tariffZoneSaverService.saveNewVersion(newVersion);
        assertThat(actual.getPolygon()).isNotNull();
        assertThat(actual.getVersion()).isEqualTo(1L);
    }


    @Test
    public void saveExistingTariffZone() {

        TariffZone existingTariffZone = new TariffZone();
        existingTariffZone.setNetexId(NetexIdHelper.generateRandomizedNetexId(existingTariffZone));
        Geometry geometry = geometryFactory.createPoint(new Coordinate(9.84, 59.26)).buffer(20);
        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(geometry.getCoordinates()), geometryFactory);
        existingTariffZone.setPolygon(geometryFactory.createPolygon(linearRing, null));
        existingTariffZone.setVersion(2L);

        when(tariffZoneRepository.findFirstByNetexIdOrderByVersionDesc(existingTariffZone.getNetexId())).thenReturn(existingTariffZone);
        when(tariffZoneRepository.save(existingTariffZone)).thenAnswer((answer) -> answer.getArguments()[0]);

        TariffZone newTariffZone = new TariffZone();
        newTariffZone.setNetexId(existingTariffZone.getNetexId());
        newTariffZone.setName(new EmbeddableMultilingualString("name"));
        newTariffZone.setPolygon(null);

        TariffZone actual = tariffZoneSaverService.saveNewVersion(newTariffZone);
        assertThat(actual.getPolygon()).isNull();
        assertThat(actual.getVersion()).isEqualTo(2L);
        assertThat(actual.getName().getValue()).isEqualTo(newTariffZone.getName().getValue());
    }

}