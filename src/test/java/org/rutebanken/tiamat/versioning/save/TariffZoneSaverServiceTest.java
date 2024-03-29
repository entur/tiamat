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
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.netex.id.RandomizedTestNetexIdGenerator;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class TariffZoneSaverServiceTest extends TiamatIntegrationTest {

    @Autowired
    private TariffZoneRepository tariffZoneRepository;

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private TariffZoneSaverService tariffZoneSaverService;

    @Autowired
    private RandomizedTestNetexIdGenerator randomizedTestNetexIdGenerator;

    @Test
    public void saveNewTariffZone() {

        TariffZone newVersion = new TariffZone();

        Geometry geometry = geometryFactory.createPoint(new Coordinate(9.84, 59.26)).buffer(20);
        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(geometry.getCoordinates()), geometryFactory);
        newVersion.setPolygon(geometryFactory.createPolygon(linearRing, null));

        TariffZone actual = tariffZoneSaverService.saveNewVersion(newVersion);
        assertThat(actual.getPolygon()).isNotNull();
        assertThat(actual.getVersion()).isEqualTo(1L);
    }


    @Test
    public void saveExistingTariffZone() {

        TariffZone existingTariffZone = new TariffZone();
        existingTariffZone.setNetexId(randomizedTestNetexIdGenerator.generateRandomizedNetexId(existingTariffZone));
        Geometry geometry = geometryFactory.createPoint(new Coordinate(9.84, 59.26)).buffer(20);
        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(geometry.getCoordinates()), geometryFactory);
        existingTariffZone.setPolygon(geometryFactory.createPolygon(linearRing, null));
        existingTariffZone.setVersion(2L);
        var zonedDateTime = ZonedDateTime.of(2020, 12, 01, 00, 00, 00, 000, ZoneId.systemDefault());
        Instant fromDate = zonedDateTime.toInstant();
        existingTariffZone.setValidBetween(new ValidBetween(fromDate,null));
        tariffZoneRepository.save(existingTariffZone);

        TariffZone newTariffZone = new TariffZone();
        newTariffZone.setNetexId(existingTariffZone.getNetexId());
        newTariffZone.setName(new EmbeddableMultilingualString("name"));
        newTariffZone.setPolygon(null);

        TariffZone actual = tariffZoneSaverService.saveNewVersion(newTariffZone);
        assertThat(actual.getPolygon()).isNull();
        assertThat(actual.getVersion()).isEqualTo(3L);
        assertThat(actual.getName().getValue()).isEqualTo(newTariffZone.getName().getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateTariffZoneShouldHaveValidFromDate() {
        TariffZone existingTariffZone = new TariffZone();
        existingTariffZone.setNetexId(randomizedTestNetexIdGenerator.generateRandomizedNetexId(existingTariffZone));
        Geometry geometry = geometryFactory.createPoint(new Coordinate(9.84, 59.26)).buffer(20);
        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(geometry.getCoordinates()), geometryFactory);
        existingTariffZone.setPolygon(geometryFactory.createPolygon(linearRing, null));
        existingTariffZone.setVersion(2L);
        var zonedDateTime = ZonedDateTime.of(2020, 12, 01, 00, 00, 00, 000, ZoneId.systemDefault());
        Instant existingTariffZoneFromDate = zonedDateTime.toInstant();
        existingTariffZone.setValidBetween(new ValidBetween(existingTariffZoneFromDate,null));
        tariffZoneRepository.save(existingTariffZone);

        TariffZone newTariffZone = new TariffZone();
        newTariffZone.setNetexId(existingTariffZone.getNetexId());
        newTariffZone.setName(new EmbeddableMultilingualString("name"));
        newTariffZone.setPolygon(null);
        Instant newTariffZoneFromDate= zonedDateTime.minusDays(1L).toInstant();
        newTariffZone.setValidBetween(new ValidBetween(newTariffZoneFromDate, null));

        tariffZoneSaverService.saveNewVersion(newTariffZone);
    }

}