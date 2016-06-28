package no.rutebanken.tiamat.rest.netex.publicationdelivery;

import no.rutebanken.tiamat.TiamatApplication;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class PublicationDeliveryResourceTest {

    @Autowired
    private PublicationDeliveryResource publicationDeliveryResource;

    @Test
    public void receivePublicationDelivery() throws Exception {

        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<PublicationDelivery version=\"1.0\" xmlns=\"http://www.netex.org.uk/netex\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.netex.org.uk/netex ../../xsd/NeTEx_publication.xsd\">" +
                " <PublicationTimestamp>2016-05-18T15:00:00.0Z</PublicationTimestamp>" +
                " <ParticipantRef>NHR</ParticipantRef>" +
                " <dataObjects>" +
                "  <SiteFrame version=\"01\" id=\"nhr:sf:1\">" +
                "   <stopPlaces>" +
                "    <StopPlace version=\"01\" created=\"2016-04-21T09:00:00.0Z\" id=\"nhr:sp:1\">" +
                "     <Name lang=\"no-NO\">Krokstien</Name>    " +
                "     <TransportMode>bus</TransportMode>" +
                "     <StopPlaceType>onstreetBus</StopPlaceType>" +
                "     <quays>" +
                "      <Quay version=\"01\" created=\"2016-04-21T09:01:00.0Z\" id=\"nhr:sp:1:q:1\">" +
                "       <Centroid>" +
                "        <Location srsName=\"WGS84\">" +
                "         <Longitude>10.8577903</Longitude>" +
                "         <Latitude>59.910579</Latitude>" +
                "        </Location>" +
                "       </Centroid>" +
                "       <Covered>outdoors</Covered>" +
                "       <Lighting>wellLit</Lighting>" +
                "       <QuayType>busStop</QuayType>" +
                "      </Quay>" +
                "     </quays>" +
                "    </StopPlace>" +
                "   </stopPlaces>" +
                "  </SiteFrame>" +
                " </dataObjects>" +
                "</PublicationDelivery>";


        InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));


        Response response = publicationDeliveryResource.receivePublicationDelivery(stream);

        assertThat(response.getStatus()).isEqualTo(200);

    }

}