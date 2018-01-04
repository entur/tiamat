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
import org.rutebanken.tiamat.netex.validation.NetexXmlReferenceValidator;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryUnmarshaller;
import org.rutebanken.tiamat.versioning.TariffZoneSaverService;
import org.rutebanken.tiamat.versioning.TopographicPlaceVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
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

    private NetexXmlReferenceValidator netexXmlReferenceValidator = new NetexXmlReferenceValidator(true);


    @Test
    public void streamStopPlaceIntoPublicationDelivery() throws Exception {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("Some municipality"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        topographicPlace = topographicPlaceVersionedSaverService.saveNewVersion(topographicPlace);

        String tariffZoneId = "CRI:TariffZone:1";

        TariffZone tariffZoneV1 = new TariffZone();
        tariffZoneV1.setNetexId(tariffZoneId);
        tariffZoneV1.setVersion(1L);
        tariffZoneV1 = tariffZoneRepository.save(tariffZoneV1);

        TariffZone tariffZoneV2 = new TariffZone();
        tariffZoneV2.setNetexId(tariffZoneId);
        tariffZoneV2.setVersion(2L);
        tariffZoneV2 = tariffZoneRepository.save(tariffZoneV2);

        TariffZone tariffZoneV3 = new TariffZone();
        tariffZoneV3.setNetexId(tariffZoneId);
        tariffZoneV3.setVersion(3L);
        tariffZoneV3 = tariffZoneRepository.save(tariffZoneV3);

        StopPlace stopPlace1 = new StopPlace(new EmbeddableMultilingualString("stop place in publication delivery"));
        stopPlace1.getTariffZones().add(new TariffZoneRef(tariffZoneId)); // Without version, implicity v3
        stopPlace1 = stopPlaceVersionedSaverService.saveNewVersion(stopPlace1);
        final String stopPlace1NetexId = stopPlace1.getNetexId();

        StopPlace stopPlace2 = new StopPlace(new EmbeddableMultilingualString("another stop place in publication delivery"));
        // StopPlace 2 refers to tariffzone version 1. This must be included in the publication delivery, allthough v2 and v3 exists
        stopPlace2.getTariffZones().add(new TariffZoneRef(tariffZoneV1));
        stopPlace2 = stopPlaceVersionedSaverService.saveNewVersion(stopPlace2);
        final String stopPlace2NetexId = stopPlace2.getNetexId();

        // Allows setting topographic place without lookup.
        // To have the lookup work, topographic place polygon must exist
        stopPlace1.setTopographicPlace(topographicPlace);
        stopPlaceRepository.save(stopPlace1);

        stopPlaceRepository.flush();
        tariffZoneRepository.flush();

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

        System.out.println(xml);

        // The unmarshaller will validate as well. But only if validateAgainstSchema is true
        validate(xml);
        // Validate using own implementation of netex xml reference validator
        netexXmlReferenceValidator.validateNetexReferences(new ByteArrayInputStream(xml.getBytes()), "publicationDelivery");

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryUnmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));

        org.rutebanken.netex.model.SiteFrame siteFrame = publicationDeliveryHelper.findSiteFrame(publicationDeliveryStructure);
        List<org.rutebanken.netex.model.StopPlace> stops = siteFrame.getStopPlaces().getStopPlace();

        assertThat(stops)
                .hasSize(2)
                .as("stops expected")
                .extracting(org.rutebanken.netex.model.StopPlace::getId)
                .containsOnly(stopPlace1.getNetexId(), stopPlace2.getNetexId());


        // Make sure both stops have references to tariff zones and with correct version
        org.rutebanken.netex.model.StopPlace actualStopPlace1 = stops.stream().filter(sp -> sp.getId().equals(stopPlace1NetexId)).findFirst().get();

        assertThat(actualStopPlace1.getTariffZones())
                .as("actual stop place 1 tariff zones")
                .isNotNull();

        org.rutebanken.netex.model.TariffZoneRef actualTariffZoneRefStopPlace1 = actualStopPlace1.getTariffZones().getTariffZoneRef().get(0);

        // Stop place 1 refers to tariff zone v2 implicity beacuse the reference does not contain version value.
        assertThat(actualTariffZoneRefStopPlace1.getRef())
                .as("actual stop place 1 tariff zone ref")
                .isEqualTo(tariffZoneId);

        // Check stop place 2

        org.rutebanken.netex.model.StopPlace actualStopPlace2 = stops.stream().filter(sp -> sp.getId().equals(stopPlace2NetexId)).findFirst().get();
        org.rutebanken.netex.model.TariffZoneRef actualTariffZoneRefStopPlace2 = actualStopPlace2.getTariffZones().getTariffZoneRef().get(0);

        assertThat(actualTariffZoneRefStopPlace2.getRef())
                .as("actual stop place 2 tariff zone ref")
                .isEqualTo(tariffZoneId);

        assertThat(actualTariffZoneRefStopPlace2.getVersion())
                .as("actual tariff zone ref for stop place 2 should point to version 1 of tariff zone")
                .isEqualTo(String.valueOf(tariffZoneV1.getVersion()));

        // Check topographic places

        assertThat(siteFrame.getTopographicPlaces()).isNotNull();
        assertThat(siteFrame.getTopographicPlaces().getTopographicPlace())
                .as("site fra topopgraphic places")
                .isNotNull()
                .hasSize(1)
                .extracting(org.rutebanken.netex.model.TopographicPlace::getId)
                .containsOnly(topographicPlace.getNetexId());

        // Check tariff zones

        assertThat(siteFrame.getTariffZones())
                .as("site fra tariff zones")
                .isNotNull();

        assertThat(siteFrame.getTariffZones().getTariffZone())
                .extracting(tariffZone -> tariffZone.getId() + "-" + tariffZone.getVersion())
                .as("Both tariff zones exists in publication delivery. But not the one not being reffered to (v2)")
                .containsOnly(tariffZoneId + "-" + 1, tariffZoneId + "-" + 3);



    }

    private void validate(String xml) throws JAXBException, IOException, SAXException {
        JAXBContext publicationDeliveryContext = newInstance(PublicationDeliveryStructure.class);
        Unmarshaller unmarshaller = publicationDeliveryContext.createUnmarshaller();

        NeTExValidator neTExValidator = new NeTExValidator();
        unmarshaller.setSchema(neTExValidator.getSchema());
        unmarshaller.unmarshal(new StringReader(xml));
    }

}
