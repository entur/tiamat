package org.rutebanken.tiamat.exporter;

import org.junit.Test;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.validation.NeTExValidator;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepositoryImpl;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javax.xml.bind.JAXBContext.newInstance;
import static org.assertj.core.api.Assertions.assertThat;

public class StreamingPublicationDeliveryTest {

    private StreamingPublicationDelivery streamingPublicationDelivery = new StreamingPublicationDelivery(new NetexMapper());

    @Test
    public void streamStopPlaceIntoPublicationDelivery() throws Exception {

        String publicationDeliveryXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<PublicationDelivery xmlns=\"http://www.netex.org.uk/netex\" xmlns:ns2=\"http://www.opengis.net/gml/3.2\" xmlns:ns3=\"http://www.siri.org.uk/siri\" version=\"any\">\n" +
                "    <PublicationTimestamp>2017-01-06T13:09:42.338+01:00</PublicationTimestamp>\n" +
                "    <ParticipantRef>NSR</ParticipantRef>\n" +
                "    <dataObjects>\n" +
                "        <SiteFrame created=\"2017-01-06T13:09:42.272+01:00\" modification=\"new\" version=\"any\" id=\"NSR:SiteFrame:1\">\n" +
                "        </SiteFrame>\n" +
                "   </dataObjects>\n" +
                "</PublicationDelivery>\n";


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        StopPlace stopPlace = new StopPlace().withName(new MultilingualString().withValue("stop place in publication delivery"));
        stopPlace.setId("10");

        BlockingQueue<StopPlace> stopPlaces = new ArrayBlockingQueue<>(2);
        stopPlaces.put(stopPlace);
        stopPlaces.put(StopPlaceRepositoryImpl.POISON_PILL);

        streamingPublicationDelivery.stream(publicationDeliveryXml, stopPlaces, byteArrayOutputStream);

        String xml = byteArrayOutputStream.toString();

        assertThat(xml)
                .contains("<StopPlace")
                .contains("</PublicationDelivery")
                .contains("</dataObjects>");
    }

    @Test
    public void streamStopPlaceIntoPublicationDeliveryWithTopographicPlace() throws Exception {

        String publicationDeliveryXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<PublicationDelivery xmlns=\"http://www.netex.org.uk/netex\" xmlns:ns2=\"http://www.opengis.net/gml/3.2\" xmlns:ns3=\"http://www.siri.org.uk/siri\" version=\"any\">\n" +
                "    <PublicationTimestamp>2017-01-06T13:09:42.338+01:00</PublicationTimestamp>\n" +
                "    <ParticipantRef>NSR</ParticipantRef>\n" +
                "    <dataObjects>\n" +
                "        <SiteFrame created=\"2017-01-06T13:09:42.272+01:00\" modification=\"new\" version=\"any\" id=\"NSR:SiteFrame:1\">\n" +
                "           <topographicPlaces>\n" +
                "               <TopographicPlace modification=\"new\" version=\"any\" id=\"NSR:TopographicPlace:3\">\n" +
                "                    <Name lang=\"no\">Akershus</Name>\n" +
                "                    <Descriptor>\n" +
                "                        <Name>Akershus</Name>\n" +
                "                    </Descriptor>\n" +
                "                    <TopographicPlaceType>county</TopographicPlaceType>\n" +
                "                    <CountryRef ref=\"no\"/>\n" +
                "                </TopographicPlace>\n" +
                "           </topographicPlaces>\n" +
                "        </SiteFrame>\n" +
                "   </dataObjects>\n" +
                "</PublicationDelivery>\n";


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        StopPlace stopPlace = new StopPlace().withName(new MultilingualString().withValue("stop place in publication delivery"));
        stopPlace.setId("10");

        BlockingQueue<StopPlace> stopPlaces = new ArrayBlockingQueue<>(2);
        stopPlaces.put(stopPlace);
        stopPlaces.put(StopPlaceRepositoryImpl.POISON_PILL);

        streamingPublicationDelivery.stream(publicationDeliveryXml, stopPlaces, byteArrayOutputStream);

        String xml = byteArrayOutputStream.toString();

        assertThat(xml)
                .contains("<StopPlace")
                .contains("<topographicPlaces")
                .contains("</topographicPlaces>")
                .contains("</PublicationDelivery")
                .contains("</dataObjects>");
    }

    @Test
    public void streamStopPlaceAndValidateResult() throws Exception {

        String publicationDeliveryXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<PublicationDelivery xmlns=\"http://www.netex.org.uk/netex\" xmlns:ns2=\"http://www.opengis.net/gml/3.2\" xmlns:ns3=\"http://www.siri.org.uk/siri\" version=\"any\">\n" +
                "    <PublicationTimestamp>2017-01-06T13:09:42.338+01:00</PublicationTimestamp>\n" +
                "    <ParticipantRef>NSR</ParticipantRef>\n" +
                "    <dataObjects>\n" +
                "        <SiteFrame created=\"2017-01-06T13:09:42.272+01:00\" modification=\"new\" version=\"any\" id=\"NSR:SiteFrame:1\">\n" +
                "           <topographicPlaces>\n" +
                "               <TopographicPlace modification=\"new\" version=\"any\" id=\"NSR:TopographicPlace:3\">\n" +
                "                    <Name lang=\"no\">Akershus</Name>\n" +
                "                    <Descriptor>\n" +
                "                        <Name>Akershus</Name>\n" +
                "                    </Descriptor>\n" +
                "                    <TopographicPlaceType>county</TopographicPlaceType>\n" +
                "                    <CountryRef ref=\"no\"/>\n" +
                "                </TopographicPlace>\n" +
                "           </topographicPlaces>\n" +
                "        </SiteFrame>\n" +
                "   </dataObjects>\n" +
                "</PublicationDelivery>\n";


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        StopPlace stopPlace = new StopPlace().withName(new MultilingualString().withValue("stop place in publication delivery"));
        stopPlace.setId("16");
        stopPlace.setVersion("2");

        BlockingQueue<StopPlace> stopPlaces = new ArrayBlockingQueue<>(2);
        stopPlaces.put(stopPlace);
        stopPlaces.put(StopPlaceRepositoryImpl.POISON_PILL);

        streamingPublicationDelivery.stream(publicationDeliveryXml, stopPlaces, byteArrayOutputStream);

        String xml = byteArrayOutputStream.toString();
        System.out.println(xml);

        validate(xml);
    }

    private void validate(String xml) throws JAXBException, IOException, SAXException {
        JAXBContext publicationDeliveryContext = newInstance(PublicationDeliveryStructure.class);
        Unmarshaller unmarshaller = publicationDeliveryContext.createUnmarshaller();

        NeTExValidator neTExValidator = new NeTExValidator();
        unmarshaller.setSchema(neTExValidator.getSchema());
        unmarshaller.unmarshal(new StringReader(xml));
    }

}