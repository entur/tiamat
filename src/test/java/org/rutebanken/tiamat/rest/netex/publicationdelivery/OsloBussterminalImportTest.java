package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.TiamatApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests related to importing Oslo Bussterminal.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class OsloBussterminalImportTest {

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    @Autowired
    private PublicationDeliveryResource publicationDeliveryResource;

    /**
     * Oslo bussterminal.
     * Usually, object structures are preferred for type checks and refactoring.
     * This xml is taken from the log when running in carbon. Could be saved in a separate file.
     */
    private static final String OSLO_BUSSTERMINAL_XML = "<?OSLO_BUSSTERMINAL_XML version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<PublicationDelivery xmlns=\"http://www.netex.org.uk/netex\" xmlns:ns2=\"http://www.opengis.net/gml/3.2\" xmlns:ns3=\"http://www.siri.org.uk/siri\">\n" +
            "   <PublicationTimestamp>2017-01-26T10:55:00.262+01:00</PublicationTimestamp>\n" +
            "   <ParticipantRef>participantRef</ParticipantRef>\n" +
            "   <Description lang=\"no\" textIdType=\"\">Publication delivery from chouette</Description>\n" +
            "   <dataObjects>\n" +
            "      <SiteFrame created=\"2017-01-26T10:55:00.262+01:00\" version=\"1\" id=\"331f175c-3cef-476b-997d-c7282270de2a\">\n" +
            "         <stopPlaces>\n" +
            "            <StopPlace version=\"1\" id=\"RUT:StopArea:03010619\">\n" +
            "               <Name lang=\"no\" textIdType=\"\">Oslo Bussterminal</Name>\n" +
            "               <Centroid>\n" +
            "                  <Location>\n" +
            "                     <Longitude>10.75761549038076481110692839138209819793701171875</Longitude>\n" +
            "                     <Latitude>59.91176224246809312035111361183226108551025390625</Latitude>\n" +
            "                  </Location>\n" +
            "               </Centroid>\n" +
            "               <StopPlaceType>onstreetBus</StopPlaceType>\n" +
            "               <quays>\n" +
            "                  <Quay version=\"1\" id=\"RUT:StopArea:0301061917\">\n" +
            "                     <Name lang=\"no\" textIdType=\"\">Oslo Bussterminal</Name>\n" +
            "                     <Description>Plattform 17</Description>\n" +
            "                     <Centroid>\n" +
            "                        <Location>\n" +
            "                           <Longitude>10.7600201712276106746912773814983665943145751953125</Longitude>\n" +
            "                           <Latitude>59.911577472464529137141653336584568023681640625</Latitude>\n" +
            "                        </Location>\n" +
            "                     </Centroid>\n" +
            "                     <CompassBearing>209.0</CompassBearing>\n" +
            "                  </Quay>\n" +
            "                  <Quay version=\"1\" id=\"RUT:StopArea:0301061930\">\n" +
            "                     <Name lang=\"no\" textIdType=\"\">Oslo Bussterminal</Name>\n" +
            "                     <Description>avstigning</Description>\n" +
            "                     <Centroid>\n" +
            "                        <Location>\n" +
            "                           <Longitude>10.75761549038076481110692839138209819793701171875</Longitude>\n" +
            "                           <Latitude>59.91176224246809312035111361183226108551025390625</Latitude>\n" +
            "                        </Location>\n" +
            "                     </Centroid>\n" +
            "                     <CompassBearing>9.0</CompassBearing>\n" +
            "                  </Quay>\n" +
            "               </quays>\n" +
            "            </StopPlace>\n" +
            "         </stopPlaces>\n" +
            "      </SiteFrame>\n" +
            "   </dataObjects>\n" +
            "</PublicationDelivery>\n";

    @Test
    public void extractPlatformCodeFromDescription() throws Exception {

        PublicationDeliveryStructure publicationDeliveryResponse = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(OSLO_BUSSTERMINAL_XML);

        StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(publicationDeliveryResponse);

        assertThat(actualStopPlace.getName().getValue()).isEqualTo("Oslo bussterminal");

        List<Quay> actualQuays = publicationDeliveryTestHelper.extractQuays(actualStopPlace);
        assertThat(actualQuays).isNotNull().as("quays should not be null");
        assertThat(actualQuays.get(0).getName().getValue()).isEqualTo("17");
        assertThat(actualQuays.get(0).getName().getValue()).isEqualTo("avstigning");
    }
}
