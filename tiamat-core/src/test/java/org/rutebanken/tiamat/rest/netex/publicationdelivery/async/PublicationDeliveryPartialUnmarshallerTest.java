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

package org.rutebanken.tiamat.rest.netex.publicationdelivery.async;

import org.junit.Test;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.rest.netex.publicationdelivery.async.RunnableUnmarshaller.POISON_PARKING;
import static org.rutebanken.tiamat.rest.netex.publicationdelivery.async.RunnableUnmarshaller.POISON_STOP_PLACE;

public class PublicationDeliveryPartialUnmarshallerTest {

    private PublicationDeliveryPartialUnmarshaller publicationDeliveryPartialUnmarshaller = new PublicationDeliveryPartialUnmarshaller(new PublicationDeliveryHelper());

    public PublicationDeliveryPartialUnmarshallerTest() throws IOException, SAXException {
    }


    @Test
    public void partiallyPublicationDeliveryImport() throws Exception {

        String notValidPublicationDeliveryXml = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <PublicationDelivery xmlns="http://www.netex.org.uk/netex" xmlns:ns2="http://www.opengis.net/gml/3.2" xmlns:ns3="http://www.siri.org.uk/siri" version="any">
                    <PublicationTimestamp>2017-01-07T15:28:33.38+01:00</PublicationTimestamp>
                    <ParticipantRef>NSR</ParticipantRef>
                    <dataObjects>
                        <SiteFrame created="2017-01-07T15:28:29.376+01:00" modification="new" version="any" id="NSR:SiteFrame:1">
                           <FrameDefaults>
                                <DefaultLocale>
                                    <TimeZone>Europe/Paris</TimeZone>
                                </DefaultLocale>
                            </FrameDefaults>
                            <topographicPlaces modificationSet="all">
                                <TopographicPlace modification="new" version="any" id="NSR:TopographicPlace:1">
                                    <Name lang="no">Oslo</Name>
                                    <Descriptor>
                                        <Name>Oslo</Name>
                                    </Descriptor>
                                    <TopographicPlaceType>county</TopographicPlaceType>
                                    <CountryRef ref="no"/>
                                </TopographicPlace>
                              </topographicPlaces>
                           <stopPlaces>
                <StopPlace created="2016-12-21T11:44:00.79+01:00" changed="2016-12-21T11:44:00.79+01:00" modification="new" version="21" id="NSR:StopPlace:14" xsi:schemaLocation="" xmlns="http://www.netex.org.uk/netex" xmlns:ns="http://www.opengis.net/gml/3.2" xmlns:ns3="http://www.siri.org.uk/siri" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                    <keyList>
                        <KeyValue>
                            <Key>imported-id</Key>
                            <Value>RUT:StopArea:02360520</Value>
                        </KeyValue>
                    </keyList>
                    <Name lang="no">Rønold</Name>
                    <Centroid>
                        <Location>
                            <Longitude>11.500336</Longitude>
                            <Latitude>60.219095</Latitude>
                        </Location>
                    </Centroid>
                    <AccessModes></AccessModes>
                    <TopographicPlaceRef ref="9"/>
                    <OtherTransportModes></OtherTransportModes>
                    <StopPlaceType>onstreetBus</StopPlaceType>
                    <quays>
                        <Quay created="2016-12-21T11:44:00.788+01:00" changed="2016-12-21T11:44:00.788+01:00" modification="new" version="1" id="NSR:Quay:26">
                            <keyList>
                                <KeyValue>
                                    <Key>imported-id</Key>
                                    <Value>RUT:StopArea:0236052001</Value>
                                </KeyValue>
                            </keyList>
                            <Centroid>
                                <Location>
                                    <Longitude>11.500217</Longitude>
                                    <Latitude>60.219074</Latitude>
                                </Location>
                            </Centroid>
                            <AccessModes></AccessModes>
                            <OtherTransportModes></OtherTransportModes>
                            <CompassBearing>205.0</CompassBearing>
                        </Quay>
                        <Quay created="2016-12-21T11:44:00.789+01:00" changed="2016-12-21T11:44:00.789+01:00" modification="new" version="1" id="NSR:Quay:27">
                            <keyList>
                                <KeyValue>
                                    <Key>imported-id</Key>
                                    <Value>RUT:StopArea:0236052002</Value>
                                </KeyValue>
                            </keyList>
                            <Centroid>
                                <Location>
                                    <Longitude>11.500455</Longitude>
                                    <Latitude>60.219115</Latitude>
                                </Location>
                            </Centroid>
                            <AccessModes></AccessModes>
                            <OtherTransportModes></OtherTransportModes>
                            <CompassBearing>25.0</CompassBearing>
                        </Quay>
                    </quays>
                </StopPlace>
                </stopPlaces>
                </SiteFrame>
                </dataObjects>
                </PublicationDelivery>
                """;

        InputStream inputStream = new ByteArrayInputStream(notValidPublicationDeliveryXml.getBytes());


        UnmarshalResult unmarshalResult = publicationDeliveryPartialUnmarshaller.unmarshal(inputStream);

        assertThat(unmarshalResult).isNotNull();


        readAndVerifyStops(unmarshalResult, 1);
    }

    @Test
    public void testPartialUnmarshallingPublicationDeliveryFromFile() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("publication_delivery/tiamat_publication_delivery.xml").getFile());

        UnmarshalResult unmarshalResult = publicationDeliveryPartialUnmarshaller.unmarshal(new FileInputStream(file));
        assertThat(unmarshalResult).isNotNull();
        readAndVerifyStops(unmarshalResult, 6);
        readAndVerifyParkings(unmarshalResult, 2);
    }

    private void readAndVerifyStops(UnmarshalResult unmarshalResult, int expectedStopCount) throws InterruptedException {
        AtomicInteger stops = new AtomicInteger();
        new EntityQueueProcessor<>(unmarshalResult.getStopPlaceQueue(), new AtomicBoolean(false), stopPlace -> stops.incrementAndGet(), POISON_STOP_PLACE).run();
        assertThat(stops.get()).isEqualTo(expectedStopCount);
    }

    private void readAndVerifyParkings(UnmarshalResult unmarshalResult, int expectedParkingCount) throws InterruptedException {
        AtomicInteger parkings = new AtomicInteger();
        new EntityQueueProcessor<>(unmarshalResult.getParkingQueue(), new AtomicBoolean(false), stopPlace -> parkings.incrementAndGet(), POISON_PARKING).run();
        assertThat(parkings.get()).isEqualTo(expectedParkingCount);
    }
}