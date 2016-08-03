package no.rutebanken.tiamat.rest.netex.publicationdelivery;

import no.rutebanken.netex.model.*;
import no.rutebanken.tiamat.TiamatApplication;

import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class PublicationDeliveryResourceTest {

    @Autowired
    private PublicationDeliveryResource publicationDeliveryResource;

    @Autowired
    private PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller;


    /**
     * When sending a stop place with the same ID twice, the same stop place must be returned.
     */
    @Test
    public void publicationDeliveryWithDuplicateStopPlace() throws Exception {

        StopPlace stopPlace = new StopPlace()
                .withId("123123")
                .withCentroid(new SimplePoint_VersionStructure()
                    .withLocation(new LocationStructure()
                            .withLatitude(new BigDecimal("9"))
                            .withLongitude(new BigDecimal("71"))));

        SiteFrame siteFrame = new SiteFrame();
        siteFrame.withStopPlaces(new StopPlacesInFrame_RelStructure()
                .withStopPlace(stopPlace));

        PublicationDeliveryStructure publicationDelivery = new PublicationDeliveryStructure()
                .withDataObjects(new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame)));

        PublicationDeliveryStructure firstResponse = publicationDeliveryResource.receivePublicationDelivery(publicationDelivery);
        PublicationDeliveryStructure secondResponse = publicationDeliveryResource.receivePublicationDelivery(publicationDelivery);

        StopPlace firstStopPlace = findFirstStopPlace(firstResponse);
        StopPlace secondStopPlace = findFirstStopPlace(secondResponse);

        assertThat(secondStopPlace.getId()).isEqualTo(firstStopPlace.getId());
    }

    private StopPlace findFirstStopPlace(PublicationDeliveryStructure publicationDeliveryStructure) {
        return publicationDeliveryStructure.getDataObjects()
                .getCompositeFrameOrCommonFrame()
                .stream()
                .map(JAXBElement::getValue)
                .filter(commonVersionFrameStructure -> commonVersionFrameStructure instanceof SiteFrame)
                .flatMap(commonVersionFrameStructure -> ((SiteFrame) commonVersionFrameStructure).getStopPlaces().getStopPlace().stream())
                .findFirst().get();
    }

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
                "     <Centroid>" +
                "      <Location srsName=\"WGS84\">" +
                "       <Longitude>10.8577903</Longitude>" +
                "       <Latitude>59.910579</Latitude>" +
                "      </Location>" +
                "     </Centroid>" +
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