package org.rutebanken.tiamat.rest.netex.publicationdelivery.async;

import org.junit.Test;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.async.EntityQueueProcessor;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.async.PublicationDeliveryPartialUnmarshaller;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.async.UnmarshalResult;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.rest.netex.publicationdelivery.async.RunnableUnmarshaller.POISON_PARKING;
import static org.rutebanken.tiamat.rest.netex.publicationdelivery.async.RunnableUnmarshaller.POISON_STOP_PLACE;

public class PublicationDeliveryPartialUnmarshallerTest {

    private PublicationDeliveryPartialUnmarshaller publicationDeliveryPartialUnmarshaller = new PublicationDeliveryPartialUnmarshaller();

    public PublicationDeliveryPartialUnmarshallerTest() throws IOException, SAXException {
    }


    @Test
    public void partiallyPublicationDeliveryImport() throws Exception {

        String notValidPublicationDeliveryXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<PublicationDelivery xmlns=\"http://www.netex.org.uk/netex\" xmlns:ns2=\"http://www.opengis.net/gml/3.2\" xmlns:ns3=\"http://www.siri.org.uk/siri\" version=\"any\">\n" +
                "    <PublicationTimestamp>2017-01-07T15:28:33.38+01:00</PublicationTimestamp>\n" +
                "    <ParticipantRef>NSR</ParticipantRef>\n" +
                "    <dataObjects>\n" +
                "        <SiteFrame created=\"2017-01-07T15:28:29.376+01:00\" modification=\"new\" version=\"any\" id=\"NSR:SiteFrame:1\">\n" +
                "            <topographicPlaces modificationSet=\"all\">\n" +
                "                <TopographicPlace modification=\"new\" version=\"any\" id=\"NSR:TopographicPlace:1\">\n" +
                "                    <Name lang=\"no\">Oslo</Name>\n" +
                "                    <Descriptor>\n" +
                "                        <Name>Oslo</Name>\n" +
                "                    </Descriptor>\n" +
                "                    <TopographicPlaceType>county</TopographicPlaceType>\n" +
                "                    <CountryRef ref=\"no\"/>\n" +
                "                </TopographicPlace>\n" +
                "              </topographicPlaces>\n" +
                "           <stopPlaces>\n" +
                "<StopPlace created=\"2016-12-21T11:44:00.79+01:00\" changed=\"2016-12-21T11:44:00.79+01:00\" modification=\"new\" version=\"21\" id=\"NSR:StopPlace:14\" xsi:schemaLocation=\"\" xmlns=\"http://www.netex.org.uk/netex\" xmlns:ns=\"http://www.opengis.net/gml/3.2\" xmlns:ns3=\"http://www.siri.org.uk/siri\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "    <keyList>\n" +
                "        <KeyValue>\n" +
                "            <Key>imported-id</Key>\n" +
                "            <Value>RUT:StopArea:02360520</Value>\n" +
                "        </KeyValue>\n" +
                "    </keyList>\n" +
                "    <Name lang=\"no\">Rønold</Name>\n" +
                "    <Centroid>\n" +
                "        <Location>\n" +
                "            <Longitude>11.500336</Longitude>\n" +
                "            <Latitude>60.219095</Latitude>\n" +
                "        </Location>\n" +
                "    </Centroid>\n" +
                "    <AccessModes></AccessModes>\n" +
                "    <TopographicPlaceRef ref=\"9\"/>\n" +
                "    <OtherTransportModes></OtherTransportModes>\n" +
                "    <StopPlaceType>onstreetBus</StopPlaceType>\n" +
                "    <quays>\n" +
                "        <Quay created=\"2016-12-21T11:44:00.788+01:00\" changed=\"2016-12-21T11:44:00.788+01:00\" modification=\"new\" version=\"1\" id=\"NSR:Quay:26\">\n" +
                "            <keyList>\n" +
                "                <KeyValue>\n" +
                "                    <Key>imported-id</Key>\n" +
                "                    <Value>RUT:StopArea:0236052001</Value>\n" +
                "                </KeyValue>\n" +
                "            </keyList>\n" +
                "            <Centroid>\n" +
                "                <Location>\n" +
                "                    <Longitude>11.500217</Longitude>\n" +
                "                    <Latitude>60.219074</Latitude>\n" +
                "                </Location>\n" +
                "            </Centroid>\n" +
                "            <AccessModes></AccessModes>\n" +
                "            <OtherTransportModes></OtherTransportModes>\n" +
                "            <CompassBearing>205.0</CompassBearing>\n" +
                "        </Quay>\n" +
                "        <Quay created=\"2016-12-21T11:44:00.789+01:00\" changed=\"2016-12-21T11:44:00.789+01:00\" modification=\"new\" version=\"1\" id=\"NSR:Quay:27\">\n" +
                "            <keyList>\n" +
                "                <KeyValue>\n" +
                "                    <Key>imported-id</Key>\n" +
                "                    <Value>RUT:StopArea:0236052002</Value>\n" +
                "                </KeyValue>\n" +
                "            </keyList>\n" +
                "            <Centroid>\n" +
                "                <Location>\n" +
                "                    <Longitude>11.500455</Longitude>\n" +
                "                    <Latitude>60.219115</Latitude>\n" +
                "                </Location>\n" +
                "            </Centroid>\n" +
                "            <AccessModes></AccessModes>\n" +
                "            <OtherTransportModes></OtherTransportModes>\n" +
                "            <CompassBearing>25.0</CompassBearing>\n" +
                "        </Quay>\n" +
                "    </quays>\n" +
                "</StopPlace>\n" +
                "</stopPlaces>\n" +
                "</SiteFrame>\n" +
                "</dataObjects>\n" +
                "</PublicationDelivery>\n";

        InputStream inputStream = new ByteArrayInputStream(notValidPublicationDeliveryXml.getBytes());


        UnmarshalResult unmarshalResult = publicationDeliveryPartialUnmarshaller.unmarshal(inputStream);

        assertThat(unmarshalResult).isNotNull();


        readAndVerifyStops(unmarshalResult, 1);
    }

    @Test
    public void testImportingPublicationDeliveryFromFile() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("publication_delivery/restoring_import.xml").getFile());

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