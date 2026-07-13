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

import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.xml.bind.JAXBException;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.mockito.Mockito;
import org.rutebanken.netex.model.GroupOfStopPlaces;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlaceRefStructure;
import org.rutebanken.netex.model.ValidBetween;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.dtoassembling.dto.ChangedStopPlaceSearchDto;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.PurposeOfGrouping;
import org.rutebanken.tiamat.model.StopPlaceReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.exporter.params.ExportParams.newExportParamsBuilder;
import static org.rutebanken.tiamat.exporter.params.StopPlaceSearch.newStopPlaceSearchBuilder;

/**
 * Tests synchronous export
 */
public class ExportResourceTest extends TiamatIntegrationTest {

    @Autowired
    private ExportResource exportResource;

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    @Test
    public void exportStopPlacesWithoutTopographicPlaces() throws JAXBException, IOException, SAXException {
        exportStopPlacesAndVerify(ExportParams.ExportMode.NONE);
    }

    private void exportStopPlacesAndVerify(ExportParams.ExportMode includeTopographicPlaces) throws JAXBException, IOException, SAXException {
        // Import stop to make sure we have something to export, although other tests might have populated the test database.
        // Make ids and search string unique

        insertTestStopsWithTopographicPlace();

        StopPlaceSearch stopPlaceSearch = newStopPlaceSearchBuilder()
                .setQuery("Østre gravlund")
                .build();
        ExportParams exportParams = newExportParamsBuilder()
                .setStopPlaceSearch(stopPlaceSearch)
                .setTopographicPlaceExportMode(includeTopographicPlaces)
                .build();

        Response response = exportResource.exportStopPlaces(exportParams);
        assertThat(response.getStatus()).isEqualTo(200);

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryTestHelper.fromResponse(response);
        List<StopPlace> stopPlaces = publicationDeliveryTestHelper.extractStopPlaces(publicationDeliveryStructure);
        assertThat(stopPlaces).hasSize(1);
    }

    @Test
    public void verifyPaging() throws Exception {
        org.rutebanken.tiamat.model.StopPlace stopPlace = new org.rutebanken.tiamat.model.StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("stopPlace"));
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        org.rutebanken.tiamat.model.StopPlace stopPlace2 = new org.rutebanken.tiamat.model.StopPlace();
        stopPlace2.setName(new EmbeddableMultilingualString("stopPlace 2"));
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace2);
        stopPlaceRepository.flush();

        final int size = 1;

        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(
                        StopPlaceSearch.newStopPlaceSearchBuilder()
                                .setSize(size)
                                .build())
                .build();

        Response response = exportResource.exportStopPlaces(exportParams);

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryTestHelper.fromResponse(response);
        List<StopPlace> stopPlaces = publicationDeliveryTestHelper.extractStopPlaces(publicationDeliveryStructure);
        assertThat(stopPlaces).as("stop places returned").hasSize(size);
    }

    @Test
    public void exportGroupOfStopPlacesToNetex() throws Exception {

        org.rutebanken.tiamat.model.StopPlace stopPlace = new org.rutebanken.tiamat.model.StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("stopPlace"));
        stopPlace.setCentroid(point(60.000, 10.78));
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        org.rutebanken.tiamat.model.StopPlace stopPlace2 = new org.rutebanken.tiamat.model.StopPlace();
        stopPlace2.setName(new EmbeddableMultilingualString("stopPlace 2"));
        stopPlace2.setCentroid(point(61.000, 11.78));
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace2);

        PurposeOfGrouping purposeOfGrouping = new PurposeOfGrouping();
        purposeOfGrouping.setName(new EmbeddableMultilingualString("generalization"));
        purposeOfGroupingSaverService.saveNewVersion(purposeOfGrouping);

        org.rutebanken.tiamat.model.GroupOfStopPlaces groupOfStopPlaces = new org.rutebanken.tiamat.model.GroupOfStopPlaces();
        groupOfStopPlaces.setPurposeOfGrouping(purposeOfGrouping);
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace2.getNetexId()));
        groupOfStopPlaces.setChangedBy("mr. Solem");
        groupOfStopPlaces.setCreated(Instant.now());
        groupOfStopPlaces.setChanged(Instant.now());
        groupOfStopPlaces.setName(new EmbeddableMultilingualString("oh my gosp"));


        org.rutebanken.tiamat.model.AlternativeName alternativeName = new org.rutebanken.tiamat.model.AlternativeName();
        alternativeName.setName(new EmbeddableMultilingualString("alternative name alias"));
        alternativeName.setNameType(org.rutebanken.tiamat.model.NameTypeEnumeration.ALIAS);
        groupOfStopPlaces.getAlternativeNames().add(alternativeName);

        groupOfStopPlacesSaverService.saveNewVersion(groupOfStopPlaces);

        stopPlaceRepository.flush();
        groupOfStopPlacesRepository.flush();


        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(StopPlaceSearch.newStopPlaceSearchBuilder().build())
                .setGroupOfStopPlacesExportMode(ExportParams.ExportMode.RELEVANT)
                .build();

        Response response = exportResource.exportStopPlaces(exportParams);

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryTestHelper.fromResponse(response);
        SiteFrame siteFrame = publicationDeliveryTestHelper.findSiteFrame(publicationDeliveryStructure);

        List<StopPlace> stopPlaces = publicationDeliveryTestHelper.extractStopPlaces(siteFrame);
        Assert.assertEquals(2, stopPlaces.size());

        GroupOfStopPlaces netexGroupOfStopPlaces = publicationDeliveryTestHelper.extractGroupOfStopPlaces(siteFrame).getFirst();

        assertThat(netexGroupOfStopPlaces).isNotNull();

        assertThat(netexGroupOfStopPlaces.getName().getValue())
                .as("name.value")
                .isEqualTo(groupOfStopPlaces.getName().getValue());

        assertThat(netexGroupOfStopPlaces.getAlternativeNames())
                .as("alternativeNames")
                .isNotNull();

        assertThat(netexGroupOfStopPlaces.getMembers())
                .as("members")
                .isNotNull();


        assertThat(netexGroupOfStopPlaces.getMembers().getStopPlaceRef())
                .as("stop place ref list")
                .isNotNull()
                .isNotEmpty()
                .extracting(sp -> sp.getValue().getRef())
                .as("reference to stop place id")
                .containsOnly(stopPlace.getNetexId(), stopPlace2.getNetexId());

        assertThat(netexGroupOfStopPlaces.getCentroid()).as("centroid").isNotNull();

        assertThat(netexGroupOfStopPlaces.getChanged()).as("changed").isNotNull();
        assertThat(netexGroupOfStopPlaces.getVersion()).as("version").isEqualTo(String.valueOf(groupOfStopPlaces.getVersion()));
    }

    @Test
    public void exportStopPlacesWithEffectiveChangedInPeriod() throws Exception {
        LocalDateTime validFrom = LocalDateTime.now().minusDays(3);
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
        Assert.assertEquals(stopPlace1.getName().getValue(), changedStopPlaces.getFirst().getName().getValue());

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

    private void insertTestStopsWithTopographicPlace() throws JAXBException, IOException, SAXException {
        if (testStopInserted) {
            return;
        }
        testStopInserted = true;

        StopPlace stopPlace = new StopPlace()
                .withId("XYZ:Stopplace:1")
                .withVersion("1")
                .withName(new MultilingualString().withValue("Østre gravlund"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("59.914353"))
                                .withLongitude(new BigDecimal("10.806387"))));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);
    }

    private Point point(double longitude, double latitude) {
        return
                geometryFactory.createPoint(
                        new Coordinate(longitude, latitude));
    }
}