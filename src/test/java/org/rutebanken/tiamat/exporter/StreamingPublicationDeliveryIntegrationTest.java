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

package org.rutebanken.tiamat.exporter;

import org.junit.Test;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.validation.NeTExValidator;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryUnmarshaller;
import org.rutebanken.tiamat.versioning.TariffZoneSaverService;
import org.rutebanken.tiamat.versioning.TopographicPlaceVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static javax.xml.bind.JAXBContext.newInstance;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test streaming publication delivery with h2 database
 * {@link StreamingPublicationDeliveryTest} is without database and spring context.
 */
public class StreamingPublicationDeliveryIntegrationTest extends TiamatIntegrationTest {

    @Autowired
    private StreamingPublicationDelivery streamingPublicationDelivery;

    @Autowired
    private TariffZoneSaverService tariffZoneSaverService;

    @Autowired
    private TopographicPlaceVersionedSaverService topographicPlaceVersionedSaverService;

    @Autowired
    private PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller;

    @Autowired
    private PublicationDeliveryHelper publicationDeliveryHelper;


    @Test
    public void streamStopPlaceIntoPublicationDelivery() throws Exception {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("Some municipality"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        topographicPlaceVersionedSaverService.saveNewVersion(topographicPlace);

        TariffZone tariffZone = new TariffZone();
        tariffZone.setNetexId("CRI:TariffZone:1");
        tariffZoneSaverService.saveNewVersion(tariffZone);

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("stop place in publication delivery"));
        stopPlace.getTariffZones().add(new TariffZoneRef(tariffZone.getNetexId()));
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        // Allows setting topographic place without lookup.
        // To have the lookup work, topographic place polygon must exist
        stopPlace.setTopographicPlace(topographicPlace);
        stopPlaceRepository.save(stopPlace);

        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(
                        StopPlaceSearch.newStopPlaceSearchBuilder()
                                .setVersionValidity(ExportParams.VersionValidity.CURRENT_FUTURE)
                                .build())
                .setTopographicPlaceExportMode(ExportParams.ExportMode.RELEVANT)
                .setTariffZoneExportMode(ExportParams.ExportMode.RELEVANT)
                .build();

        streamingPublicationDelivery.stream(exportParams, byteArrayOutputStream);

        String xml = byteArrayOutputStream.toString();

        // The unmarshaller will validate as well. But only if validateAgainstSchema is true
        validate(xml);

        assertThat(xml)
                .contains("<StopPlace")
                .contains("</PublicationDelivery")
                .contains("</dataObjects>");


        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryUnmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));

        org.rutebanken.netex.model.SiteFrame siteFrame = publicationDeliveryHelper.findSiteFrame(publicationDeliveryStructure);
        List<org.rutebanken.netex.model.StopPlace> stops = siteFrame.getStopPlaces().getStopPlace();

        assertThat(stops)
                .hasSize(1)
                .extracting(org.rutebanken.netex.model.StopPlace::getId)
                .containsOnly(stopPlace.getNetexId());

        assertThat(stops.get(0).getTariffZones()).isNotNull();
        assertThat(stops.get(0).getTariffZones().getTariffZoneRef().get(0).getRef()).isEqualTo(tariffZone.getNetexId());

        assertThat(siteFrame.getTopographicPlaces()).isNotNull();
        assertThat(siteFrame.getTopographicPlaces().getTopographicPlace())
                .isNotNull()
                .hasSize(1)
                .extracting(org.rutebanken.netex.model.TopographicPlace::getId).containsOnly(topographicPlace.getNetexId());

        assertThat(siteFrame.getTariffZones())
                .isNotNull();

        assertThat(siteFrame.getTariffZones().getTariffZone())
                .extracting(org.rutebanken.netex.model.TariffZone::getId).containsOnly(tariffZone.getNetexId());


    }

    private void validate(String xml) throws JAXBException, IOException, SAXException {
        JAXBContext publicationDeliveryContext = newInstance(PublicationDeliveryStructure.class);
        Unmarshaller unmarshaller = publicationDeliveryContext.createUnmarshaller();

        NeTExValidator neTExValidator = new NeTExValidator();
        unmarshaller.setSchema(neTExValidator.getSchema());
        unmarshaller.unmarshal(new StringReader(xml));
    }

}
