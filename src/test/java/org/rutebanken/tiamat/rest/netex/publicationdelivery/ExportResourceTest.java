package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.dtoassembling.dto.ChangedStopPlaceSearchDto;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static javax.xml.bind.JAXBContext.newInstance;
import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.exporter.params.ExportParams.newExportParamsBuilder;
import static org.rutebanken.tiamat.exporter.params.StopPlaceSearch.newStopPlaceSearchBuilder;

public class ExportResourceTest extends TiamatIntegrationTest {

    @Autowired
    private ExportResource exportResource;

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    /**
     * Make stop places exported in publication deliveries are valid according to the xsd.
     * It should be validated when streaming out.
     */
    @Test
    public void exportStopPlacesWithRelevantTopographicPlaces() throws JAXBException, IOException, SAXException {
        exportStopPlacesAndVerify(ExportParams.ExportMode.RELEVANT);
    }

    @Test
    public void exportStopPlacesWithoutTopographicPlaces() throws JAXBException, IOException, SAXException {
        exportStopPlacesAndVerify(ExportParams.ExportMode.NONE);
    }


    private void exportStopPlacesAndVerify(ExportParams.ExportMode includeTopographicPlaces) throws JAXBException, IOException, SAXException {
        // Import stop to make sure we have something to export, although other tests might have populated the test database.
        // Make ids and search string unique

        insertTestStopWithTopographicPlace();

        StopPlaceSearch stopPlaceSearch = newStopPlaceSearchBuilder()
                .setQuery("Østre gravlund")
                .build();
        ExportParams exportParams = newExportParamsBuilder()
                .setStopPlaceSearch(stopPlaceSearch)
                .setTopographicPlaceExportMode(includeTopographicPlaces)
                .build();

        Response response = exportResource.exportStopPlaces(exportParams);
        assertThat(response.getStatus()).isEqualTo(200);
        // TODO Response is empty. Inserted stop place is somehow not found
        StreamingOutput streamingOutput = (StreamingOutput) response.getEntity();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        streamingOutput.write(byteArrayOutputStream);
        System.out.println(byteArrayOutputStream.toString());
    }


    @Test
    public void importStopPlaceWithMultipleValidBetweenPeriodsIgnoresAllButFirst() throws Exception {
        OffsetDateTime firstValidFrom = OffsetDateTime.now().minusDays(5);
        OffsetDateTime secondValidFrom = OffsetDateTime.now().minusDays(3);
        StopPlace stopPlace1 = new StopPlace()
                                       .withId("XYZ:Stopplace:1")
                                       .withVersion("1")
                                       .withName(new MultilingualString().withValue("New stop1"))
                                       .withValidBetween(new ValidBetween().withFromDate(firstValidFrom).withToDate(secondValidFrom), new ValidBetween().withFromDate(secondValidFrom))
                                       .withCentroid(new SimplePoint_VersionStructure()
                                                             .withLocation(new LocationStructure()
                                                                                   .withLatitude(new BigDecimal("59.914353"))
                                                                                   .withLongitude(new BigDecimal("10.806387"))));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace1);
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        List<StopPlace> changedStopPlaces = publicationDeliveryTestHelper.extractStopPlaces(response);
        Assert.assertEquals(1, changedStopPlaces.size());
        StopPlace stopPlace = changedStopPlaces.get(0);

        List<ValidBetween> actualValidBetween = stopPlace.getValidBetween();

        assertThat(actualValidBetween)
                .as("Stop Place should have actualValidBetween set")
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        assertThat(actualValidBetween.get(0).getFromDate()).isEqualTo(firstValidFrom);
    }


    @Test
    public void exportStopPlacesWithEffectiveChangedInPeriod() throws Exception {
        OffsetDateTime validFrom = OffsetDateTime.now().minusDays(3);
        StopPlace stopPlace1 = new StopPlace()
                                       .withId("XYZ:Stopplace:1")
                                       .withVersion("1")
                                       .withName(new MultilingualString().withValue("Changed stop1"))
                                       .withValidBetween(new ValidBetween().withFromDate(validFrom))
                                       .withCentroid(new SimplePoint_VersionStructure()
                                                             .withLocation(new LocationStructure()
                                                                                   .withLatitude(new BigDecimal("59.914353"))
                                                                                   .withLongitude(new BigDecimal("10.806387"))));

        StopPlace stopPlace2 = new StopPlace()
                                       .withId("XYZ:Stopplace:2")
                                       .withVersion("1")
                                       .withName(new MultilingualString().withValue("Changed stop2"))
                                       .withValidBetween(new ValidBetween().withFromDate(validFrom.plusDays(1)))
                                       .withCentroid(new SimplePoint_VersionStructure()
                                                             .withLocation(new LocationStructure()
                                                                                   .withLatitude(new BigDecimal("22.914353"))
                                                                                   .withLongitude(new BigDecimal("11.806387"))));


        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace1, stopPlace2);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        UriInfo uriInfoMock = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(JerseyUriBuilder.fromPath("http://test"));
        ChangedStopPlaceSearchDto search = new ChangedStopPlaceSearchDto(null, null, 0, 1);

        Response response = exportResource.exportStopPlacesWithEffectiveChangedInPeriod(search, newExportParamsBuilder().build(), uriInfoMock);
        List<StopPlace> changedStopPlaces = publicationDeliveryTestHelper.extractStopPlaces(response);
        Assert.assertEquals(1, changedStopPlaces.size());
        Assert.assertEquals(stopPlace1.getName().getValue(), changedStopPlaces.get(0).getName().getValue());

        Link link = response.getLink("next");
        Assert.assertNotNull(link);
    }

    @Test
    public void exportStopPlacesWithEffectiveChangedInPeriodNoContent() throws Exception {
        String historicTime = "2012-04-23T18:25:43.511+0100";

        UriInfo uriInfoMock = Mockito.mock(UriInfo.class);
        ChangedStopPlaceSearchDto search = new ChangedStopPlaceSearchDto(historicTime, historicTime, 0, 1);

        Response response = exportResource.exportStopPlacesWithEffectiveChangedInPeriod(search, newExportParamsBuilder().build(), uriInfoMock);
        Assert.assertEquals(response.getStatus(), HttpStatus.NO_CONTENT.value());
    }

    private boolean testStopInserted = false;

    private void insertTestStopWithTopographicPlace() throws JAXBException, IOException, SAXException {
        if (testStopInserted) {
            return;
        }
        testStopInserted = true;
        TopographicPlace topographicParent = new TopographicPlace()
                .withId("KVE:TopographicPlace:1")
                .withVersion("1")
                .withDescriptor(new TopographicPlaceDescriptor_VersionedChildStructure().withName(new MultilingualString().withValue("Fylke")));

        TopographicPlace topographicPlace = new TopographicPlace()
                .withId("KVE:TopographicPlace:3")
                .withVersion("1")
                .withDescriptor(new TopographicPlaceDescriptor_VersionedChildStructure().withName(new MultilingualString().withValue("Kommune")))
                .withParentTopographicPlaceRef(new TopographicPlaceRefStructure()
                        .withRef(topographicParent.getId()));
        PublicationDeliveryStructure topographicPlacesForImport = publicationDeliveryTestHelper.createPublicationDeliveryTopographicPlace(topographicParent, topographicPlace);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(topographicPlacesForImport);


        StopPlace stopPlace = new StopPlace()
                .withId("XYZ:Stopplace:1")
                .withVersion("1")
                .withName(new MultilingualString().withValue("Østre gravlund"))
                .withTopographicPlaceRef(new TopographicPlaceRefStructure()
                        .withRef(topographicPlace.getId()))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("59.914353"))
                                .withLongitude(new BigDecimal("10.806387"))));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);
    }


}