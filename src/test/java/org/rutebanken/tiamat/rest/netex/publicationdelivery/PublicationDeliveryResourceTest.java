package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.TiamatApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.netexmapping.NetexIdMapper.ORIGINAL_ID_KEY;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class PublicationDeliveryResourceTest {

    @Autowired
    private PublicationDeliveryResource publicationDeliveryResource;

    /**
     * When sending a stop place with the same ID twice, the same stop place must be returned.
     * When importing multiple stop places and those exists, make sure no Lazy Initialization Exception is thrown.
     */
    @Test
    public void publicationDeliveryWithDuplicateStopPlace() throws Exception {

        StopPlace stopPlace = new StopPlace()
                .withId("NSR:StopPlace:123123")
                .withCentroid(new SimplePoint_VersionStructure()
                    .withLocation(new LocationStructure()
                            .withLatitude(new BigDecimal("9"))
                            .withLongitude(new BigDecimal("71"))));

        StopPlace stopPlace2 = new StopPlace()
                .withId("NSR:StopPlace:123123")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("10"))
                                .withLongitude(new BigDecimal("72"))));


        PublicationDeliveryStructure publicationDelivery = createPublicationDeliveryWithStopPlace(stopPlace, stopPlace2);

        PublicationDeliveryStructure response = publicationDeliveryResource.importPublicationDelivery(publicationDelivery);

        List<StopPlace> result = extractStopPlace(response);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(result.get(1).getId());
    }

    @Test
    public void publicationDeliveryWithStopPlaceAndQuay() throws Exception {

        StopPlace stopPlace = new StopPlace()
                .withId("NSR:StopPlace:123123")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))))
                .withQuays(new Quays_RelStructure()
                    .withQuayRefOrQuay(new Quay()
                        .withName(new MultilingualString().withValue("quay"))
                        .withCentroid(new SimplePoint_VersionStructure()
                            .withLocation(new LocationStructure()
                                    .withLatitude(new BigDecimal("9.1"))
                                    .withLongitude(new BigDecimal("71.2"))))));

        PublicationDeliveryStructure publicationDelivery = createPublicationDeliveryWithStopPlace(stopPlace);

        PublicationDeliveryStructure firstResponse = publicationDeliveryResource.importPublicationDelivery(publicationDelivery);

        StopPlace actualStopPlace = findFirstStopPlace(firstResponse);

        assertThat(actualStopPlace.getQuays()).isNotNull().as("quays should not be null");

        Quay quay = actualStopPlace.getQuays()
                .getQuayRefOrQuay().stream()
                    .filter(object -> object instanceof Quay)
                    .map(object -> ((Quay) object))
                    .findFirst()
                    .get();


        assertThat(quay.getName().getValue()).isEqualTo("quay");
        assertThat(quay.getId()).isNotNull();

    }

    @Test
    public void importStopPlaceWithoutCoordinates() throws Exception {

        String chouetteId = "OPP:StopArea:123";

        StopPlace stopPlace = new StopPlace()
                .withId(chouetteId)
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withName(new MultilingualString().withValue("quay"))));

        PublicationDeliveryStructure firstPublicationDelivery = createPublicationDeliveryWithStopPlace(stopPlace);
        PublicationDeliveryStructure response = publicationDeliveryResource.importPublicationDelivery(firstPublicationDelivery);

        StopPlace actualStopPlace = findFirstStopPlace(response);

        assertThat(actualStopPlace).isNotNull();

    }

    @Test
    public void matchStopPlaceWithoutCoordinates() throws Exception {

        String chouetteId = "HED:StopArea:321321";

        StopPlace stopPlace = new StopPlace()
                .withId(chouetteId)
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withName(new MultilingualString().withValue("quay"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("9.1"))
                                                .withLongitude(new BigDecimal("71.2"))))));

        PublicationDeliveryStructure firstPublicationDelivery = createPublicationDeliveryWithStopPlace(stopPlace);
        PublicationDeliveryStructure firstResponse = publicationDeliveryResource.importPublicationDelivery(firstPublicationDelivery);
        StopPlace firstStopPlaceReturned = findFirstStopPlace(firstResponse);
        // Same ID, but no coordinates
        StopPlace stopPlaceWithoutCoordinates = new StopPlace()
                .withId(chouetteId)
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withName(new MultilingualString().withValue("quay"))));

        PublicationDeliveryStructure secondPublicationDelivery = createPublicationDeliveryWithStopPlace(stopPlaceWithoutCoordinates);
        PublicationDeliveryStructure secondResponse = publicationDeliveryResource.importPublicationDelivery(secondPublicationDelivery);

        StopPlace secondStopPlaceReturned = findFirstStopPlace(secondResponse);
        assertThat(secondStopPlaceReturned.getId()).isEqualTo(firstStopPlaceReturned.getId())
                .as("Expecting IDs to be the same, because the chouette ID is the same");

    }

    @Test
    public void importPublicationDeliveryAndExpectMappedIdInReturn() throws Exception {

        String originalQuayId = "XYZ:Quay:321321";

        StopPlace stopPlace = new StopPlace()
                .withId("XYZ:StopPlace:123123")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId(originalQuayId)
                                .withName(new MultilingualString().withValue("quay"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("9.1"))
                                                .withLongitude(new BigDecimal("71.2"))))));

        PublicationDeliveryStructure publicationDelivery = createPublicationDeliveryWithStopPlace(stopPlace);
        PublicationDeliveryStructure firstResponse = publicationDeliveryResource.importPublicationDelivery(publicationDelivery);

        StopPlace actualStopPlace = findFirstStopPlace(firstResponse);

        hasOriginalId(stopPlace.getId(), actualStopPlace);

        Quay quay = actualStopPlace.getQuays()
                .getQuayRefOrQuay()
                .stream()
                .peek(object -> System.out.println(object))
                .filter(object -> object instanceof Quay)
                .map(object -> ((Quay) object))
                .peek(q-> System.out.println(q))
                .findFirst().get();

        hasOriginalId(originalQuayId, quay);
    }

    @Test
    public void importPublicationDeliveryAndExpectCertainWordsToBeRemovedFromNames() throws Exception {
        StopPlace stopPlace = new StopPlace()
                .withName(new MultilingualString().withValue("Steinerskolen Moss (Buss)"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withName(new MultilingualString().withValue("Steinerskolen Moss [tog]"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("9.1"))
                                                .withLongitude(new BigDecimal("71.2"))))));

        PublicationDeliveryStructure publicationDelivery = createPublicationDeliveryWithStopPlace(stopPlace);
        PublicationDeliveryStructure firstResponse = publicationDeliveryResource.importPublicationDelivery(publicationDelivery);

        StopPlace actualStopPlace = findFirstStopPlace(firstResponse);

        Quay quay = actualStopPlace.getQuays()
                .getQuayRefOrQuay()
                .stream()
                .peek(object -> System.out.println(object))
                .filter(object -> object instanceof Quay)
                .map(object -> ((Quay) object))
                .peek(q-> System.out.println(q))
                .findFirst().get();

        assertThat(actualStopPlace.getName().getValue()).isEqualTo("Steinerskolen Moss");
        assertThat(quay.getName().getValue()).isEqualTo("Steinerskolen Moss");

    }

    private PublicationDeliveryStructure createPublicationDeliveryWithStopPlace(StopPlace... stopPlace) {
        SiteFrame siteFrame = new SiteFrame();
        siteFrame.withStopPlaces(new StopPlacesInFrame_RelStructure()
                .withStopPlace(stopPlace));

        PublicationDeliveryStructure publicationDelivery = new PublicationDeliveryStructure()
                .withDataObjects(new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame)));

        return publicationDelivery;
    }

    private void hasOriginalId(String expectedId, DataManagedObjectStructure object) {
        assertThat(object).isNotNull();
        assertThat(object.getKeyList()).isNotNull();
        List<String> list = object.getKeyList().getKeyValue()
                .stream()
                .peek(keyValueStructure -> System.out.println(keyValueStructure))
                .filter(keyValueStructure -> keyValueStructure.getKey().equals(ORIGINAL_ID_KEY))
                .map(keyValueStructure -> keyValueStructure.getValue())
                .collect(Collectors.toList());
        assertThat(list).hasSize(1);
        String originalIdString = list.get(0);
        assertThat(originalIdString).isNotEmpty();
        assertThat(originalIdString).isEqualTo(expectedId);
    }

    private List<StopPlace> extractStopPlace(PublicationDeliveryStructure publicationDeliveryStructure) {
        return  publicationDeliveryStructure.getDataObjects()
                .getCompositeFrameOrCommonFrame()
                .stream()
                .map(JAXBElement::getValue)
                .filter(commonVersionFrameStructure -> commonVersionFrameStructure instanceof SiteFrame)
                .flatMap(commonVersionFrameStructure -> ((SiteFrame) commonVersionFrameStructure).getStopPlaces().getStopPlace().stream())
                .collect(toList());
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