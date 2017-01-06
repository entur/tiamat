package org.rutebanken.tiamat.exporters;

import org.junit.Test;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepositoryImpl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class StreamingPublicationDeliveryTest {

    @Test
    public void stream() throws Exception {

        String publicationDeliveryXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<PublicationDelivery xmlns=\"http://www.netex.org.uk/netex\" xmlns:ns2=\"http://www.opengis.net/gml/3.2\" xmlns:ns3=\"http://www.siri.org.uk/siri\" version=\"any\">\n" +
                "    <PublicationTimestamp>2017-01-06T13:09:42.338+01:00</PublicationTimestamp>\n" +
                "    <ParticipantRef>NSR</ParticipantRef>\n" +
                "    <dataObjects>\n" +
                "        <SiteFrame created=\"2017-01-06T13:09:42.272+01:00\" modification=\"new\" version=\"any\" id=\"NSR:SiteFrame:1\">\n" +
                "        </SiteFrame>\n" +
                "   </dataObjects>\n" +
                "</PublicationDelivery>\n";

        StreamingPublicationDelivery streamingPublicationDelivery = new StreamingPublicationDelivery(new NetexMapper());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();


        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("stop place in publication delivery"));
        stopPlace.setId(10L);

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

}