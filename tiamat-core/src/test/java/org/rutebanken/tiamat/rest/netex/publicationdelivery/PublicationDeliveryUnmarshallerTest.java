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

package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import org.entur.netex2.model.PublicationDeliveryStructure;
import org.entur.netex2.model.SiteFrame;
import org.entur.netex2.validation.NeTExValidator.NetexVersion;
import org.junit.Test;
import org.rutebanken.tiamat.netex.mapping.NetexMultilingualStringHelper;
import org.springframework.test.util.ReflectionTestUtils;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class PublicationDeliveryUnmarshallerTest {

    /**
     * A stop place as current NeTEx 1.x exports write it: the placeEquipments container carries an
     * entity id, which NeTEx 2.0 restricts to RelationshipId values.
     */
    private static final String STOP_PLACE_WITH_PLACE_EQUIPMENTS_ID = """
            <?xml version="1.0" encoding="UTF-8"?>
            <PublicationDelivery xmlns="http://www.netex.org.uk/netex" version="%s">
                <PublicationTimestamp>2026-10-19T10:00:00</PublicationTimestamp>
                <ParticipantRef>NSR</ParticipantRef>
                <dataObjects>
                    <SiteFrame version="1" id="NSR:SiteFrame:1">
                        <stopPlaces>
                            <StopPlace version="1" id="NSR:StopPlace:1">
                                <Name lang="nor">Oslo S</Name>
                                <placeEquipments id="NSR:PlaceEquipment:1">
                                    <ShelterEquipment version="1" id="NSR:ShelterEquipment:1">
                                        <Seats>4</Seats>
                                    </ShelterEquipment>
                                </placeEquipments>
                            </StopPlace>
                        </stopPlaces>
                    </SiteFrame>
                </dataObjects>
            </PublicationDelivery>""";

    @Test
    public void expectUnmarshalExceptionWhenIncorrectPublicationDeliveryXml() throws IOException, SAXException, JAXBException {

        String notValidPublicationDeliveryXml = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <PublicationDelivery xmlns="http://www.netex.org.uk/netex" xmlns:ns2="http://www.opengis.net/gml/3.2" xmlns:ns3="http://www.siri.org.uk/siri">
                </PublicationDeliivery>""";

        InputStream inputStream = new ByteArrayInputStream(notValidPublicationDeliveryXml.getBytes());

        PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller = new PublicationDeliveryUnmarshaller();

        assertThatThrownBy(() -> publicationDeliveryUnmarshaller.unmarshal(inputStream))
                .isInstanceOf(UnmarshalException.class);

    }

    @Test
    public void expectUnmarshalExceptionWhenIncorrectPublicationDeliveryXmlWithValidation() throws IOException, SAXException {
        String notValidPublicationDeliveryXml = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <PublicationDelivery xmlns="http://www.netex.org.uk/netex" version="1.15:NO-NeTEx-stops:1.5">
                </PublicationDeliivery>""";

        assertThatThrownBy(() -> validatingUnmarshaller().unmarshal(toStream(notValidPublicationDeliveryXml)))
                .isInstanceOf(UnmarshalException.class);
    }

    @Test
    public void acceptsNetex1DocumentWithEntityIdOnPlaceEquipments() throws Exception {
        PublicationDeliveryStructure publicationDelivery = validatingUnmarshaller()
                .unmarshal(toStream(STOP_PLACE_WITH_PLACE_EQUIPMENTS_ID.formatted("1.15:NO-NeTEx-stops:1.5")));

        SiteFrame siteFrame = (SiteFrame) publicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame().getFirst().getValue();
        assertThat(siteFrame.getStopPlaces().getStopPlace()).hasSize(1);
        assertThat(siteFrame.getStopPlaces().getStopPlace().getFirst().getId()).isEqualTo("NSR:StopPlace:1");
        assertThat(NetexMultilingualStringHelper.getValue(siteFrame.getStopPlaces().getStopPlace().getFirst().getName())).isEqualTo("Oslo S");
    }

    @Test
    public void rejectsNetex2DocumentWithEntityIdOnPlaceEquipments() throws Exception {
        PublicationDeliveryUnmarshaller unmarshaller = validatingUnmarshaller();

        assertThatThrownBy(() -> unmarshaller.unmarshal(toStream(STOP_PLACE_WITH_PLACE_EQUIPMENTS_ID.formatted("2.0:NO-NeTEx-stops:1.5"))))
                .isInstanceOf(UnmarshalException.class)
                .hasStackTraceContaining("'NSR:PlaceEquipment:1' is not facet-valid");
    }

    @Test
    public void acceptsNetex2DocumentWithTextElements() throws Exception {
        String netex2Document = """
                <?xml version="1.0" encoding="UTF-8"?>
                <PublicationDelivery xmlns="http://www.netex.org.uk/netex" version="2.0">
                    <PublicationTimestamp>2026-10-19T10:00:00</PublicationTimestamp>
                    <ParticipantRef>NSR</ParticipantRef>
                    <dataObjects>
                        <SiteFrame version="1" id="NSR:SiteFrame:1">
                            <stopPlaces>
                                <StopPlace version="1" id="NSR:StopPlace:1">
                                    <Name><Text lang="nor">Oslo S</Text></Name>
                                </StopPlace>
                            </stopPlaces>
                        </SiteFrame>
                    </dataObjects>
                </PublicationDelivery>""";

        PublicationDeliveryStructure publicationDelivery = validatingUnmarshaller().unmarshal(toStream(netex2Document));

        SiteFrame siteFrame = (SiteFrame) publicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame().getFirst().getValue();
        assertThat(NetexMultilingualStringHelper.getValue(siteFrame.getStopPlaces().getStopPlace().getFirst().getName())).isEqualTo("Oslo S");
    }

    @Test
    public void schemaVersionFromDeclaredVersion() {
        assertThat(PublicationDeliveryUnmarshaller.schemaVersionFor("1.15:NO-NeTEx-stops:1.5")).isEqualTo(NetexVersion.v1_15);
        assertThat(PublicationDeliveryUnmarshaller.schemaVersionFor("1.16:NO-NeTEx-networktimetable:1.6")).isEqualTo(NetexVersion.v1_16);
        assertThat(PublicationDeliveryUnmarshaller.schemaVersionFor("1.04:NO-NeTEx-stops:1.0")).isEqualTo(NetexVersion.V1_0_4beta);
        assertThat(PublicationDeliveryUnmarshaller.schemaVersionFor("2.0:NO-NeTEx-stops:1.5")).isEqualTo(NetexVersion.v2_0);
        assertThat(PublicationDeliveryUnmarshaller.schemaVersionFor("2.0")).isEqualTo(NetexVersion.v2_0);
    }

    @Test
    public void schemaVersionFallsBackToLatest() {
        assertThat(PublicationDeliveryUnmarshaller.schemaVersionFor(null)).isEqualTo(NetexVersion.v2_0);
        assertThat(PublicationDeliveryUnmarshaller.schemaVersionFor("")).isEqualTo(NetexVersion.v2_0);
        assertThat(PublicationDeliveryUnmarshaller.schemaVersionFor("9.9:NO-NeTEx-stops:9.9")).isEqualTo(NetexVersion.v2_0);
        assertThat(PublicationDeliveryUnmarshaller.schemaVersionFor("any")).isEqualTo(NetexVersion.v2_0);
    }

    private static PublicationDeliveryUnmarshaller validatingUnmarshaller() throws IOException, SAXException {
        PublicationDeliveryUnmarshaller unmarshaller = new PublicationDeliveryUnmarshaller();
        ReflectionTestUtils.setField(unmarshaller, "validateAgainstSchema", true);
        return unmarshaller;
    }

    private static InputStream toStream(String xml) {
        return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    }
}
