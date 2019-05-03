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

import org.junit.Before;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.junit.Test;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.netex.id.RandomizedTestNetexIdGenerator;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.NetexMappingContextThreadLocal;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryTestHelper;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryUnmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class TariffZoneSaverServiceTest extends TiamatIntegrationTest {

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private TariffZoneSaverService tariffZoneSaverService;

    @Autowired
    private RandomizedTestNetexIdGenerator randomizedTestNetexIdGenerator;

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryHelper;

    @Autowired
    private NetexMapper netexMapper;
    private TariffZone tariffZone;

    @Before
    public void setUp() throws IOException, SAXException, JAXBException {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("publication_delivery/tariff_zones.xml").getFile());

        final PublicationDeliveryStructure publicationDelivery = new PublicationDeliveryUnmarshaller().unmarshal(new FileInputStream(file));

        SiteFrame netexSiteFrame = publicationDeliveryHelper.findSiteFrame(publicationDelivery);

        NetexMappingContextThreadLocal.updateMappingContext(netexSiteFrame);

        List<TariffZone> tiamatTariffZones = netexMapper.getFacade().mapAsList(netexSiteFrame.getTariffZones().getTariffZone(), org.rutebanken.tiamat.model.TariffZone.class);

        tariffZone = tiamatTariffZones.get(0);

    }


    @Test
    public void saveNewTariffZone(){

        TariffZone actual = tariffZoneSaverService.saveNewVersion(tariffZone);
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

        tariffZoneSaverService.saveNewVersion(existingTariffZone);

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