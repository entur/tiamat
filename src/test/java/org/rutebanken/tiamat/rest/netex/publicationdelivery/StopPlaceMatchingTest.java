package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.junit.Test;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.importer.ImportType;
import org.rutebanken.tiamat.importer.PublicationDeliveryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

public class StopPlaceMatchingTest extends TiamatIntegrationTest {

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    @Test
    public void matchImportedStopOnId() throws Exception {

        StopPlace stopPlaceToBeMatched = new StopPlace()
                .withId("RUT:StopPlace:187187666")
                .withStopPlaceType(StopTypeEnumeration.BUS_STATION)
                .withVersion("1")
                .withName(new MultilingualString().withValue("I don't care"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("10"))
                                .withLongitude(new BigDecimal("74"))));


        PublicationDeliveryParams publicationDeliveryParams = new PublicationDeliveryParams();
        publicationDeliveryParams.importType = ImportType.INITIAL;
        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlaceToBeMatched);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, publicationDeliveryParams);


        PublicationDeliveryStructure publicationDelivery2 = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlaceToBeMatched);
        publicationDeliveryParams.importType = ImportType.ID_MATCH;
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery2, publicationDeliveryParams);

        System.out.println("Got response: \n" + response);

        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response);

        assertThat(result).hasSize(1);
        publicationDeliveryTestHelper.hasOriginalId(stopPlaceToBeMatched.getId(), result.get(0));
    }

    /**
     * See https://rutebanken.atlassian.net/browse/NRP-1601
     *
     * IDs might match incorrectly because of bad data.
     * Make sure if we got a ID match, the distance should be checked.
     * If the existing stop place and the incoming stop place is too far away from each other,
     * fall back to look for nearby stops.
     */
    @Test
    public void matchNearByStopPlaceIfIDMatchIsTooFarAway() throws Exception {

        StopPlace tooFarAwayStopPlace = new StopPlace()
                .withId("RUT:StopPlace:187187666")
                .withStopPlaceType(StopTypeEnumeration.BUS_STATION)
                .withVersion("1")
                .withName(new MultilingualString().withValue("Too far away"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("74"))
                                .withLongitude(new BigDecimal("10"))));

        StopPlace nearbyStopPlace = new StopPlace()
                .withId("CBS:StopPlace:321")
                .withStopPlaceType(StopTypeEnumeration.BUS_STATION)
                .withVersion("1")
                .withName(new MultilingualString().withValue("Some stop place"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("74.2"))
                                .withLongitude(new BigDecimal("10.2"))));

        PublicationDeliveryParams publicationDeliveryParams = new PublicationDeliveryParams();
        publicationDeliveryParams.importType = ImportType.INITIAL;
        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(tooFarAwayStopPlace, nearbyStopPlace);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, publicationDeliveryParams);

        StopPlace stopPlaceToBeMerged = new StopPlace()
                .withId("RUT:StopPlace:187187666") // Same as the ID of the stop place which is too far away
                .withStopPlaceType(StopTypeEnumeration.BUS_STATION)
                .withVersion("1")
                .withName(new MultilingualString().withValue("Some stop place"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("74.2002"))
                                .withLongitude(new BigDecimal("10.20001"))));

        PublicationDeliveryStructure publicationDelivery2 = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlaceToBeMerged);
        publicationDeliveryParams.importType = ImportType.MERGE;
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery2, publicationDeliveryParams);

        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response);

        assertThat(result).hasSize(1);
        StopPlace actualStopPlace = result.get(0);
        publicationDeliveryTestHelper.hasOriginalId(nearbyStopPlace.getId(), actualStopPlace);
        publicationDeliveryTestHelper.hasOriginalId(stopPlaceToBeMerged.getId(), actualStopPlace);

        assertThat(actualStopPlace.getName().getValue()).isNotEqualTo(tooFarAwayStopPlace.getName().getValue());
        assertThat(actualStopPlace.getName().getValue()).isEqualTo(nearbyStopPlace.getName().getValue());

    }

    @Test
    public void matchImportedStopOnNonNumericId() throws Exception {

        StopPlace stopPlaceToBeMatched = new StopPlace()
                .withId("RUT:StopPlace:xxxxxx")
                .withStopPlaceType(StopTypeEnumeration.BUS_STATION)
                .withVersion("1")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("14"))
                                .withLongitude(new BigDecimal("75"))));


        PublicationDeliveryParams publicationDeliveryParams = new PublicationDeliveryParams();
        publicationDeliveryParams.importType = ImportType.INITIAL;
        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlaceToBeMatched);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, publicationDeliveryParams);


        PublicationDeliveryStructure publicationDelivery2 = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlaceToBeMatched);
        publicationDeliveryParams.importType = ImportType.ID_MATCH;
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery2, publicationDeliveryParams);

        System.out.println("Got response: \n" + response);

        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response);

        assertThat(result).hasSize(1);
        publicationDeliveryTestHelper.hasOriginalId(stopPlaceToBeMatched.getId(), result.get(0));
    }

    @Test
    public void doNotmatchStopOnSimilarOriginalId() throws Exception {

        StopPlace stopPlaceNotToBeMatched = new StopPlace()
                .withName(new MultilingualString().withValue("Hest"))
                .withId("RUT:StopPlace:212345678910")
                .withStopPlaceType(StopTypeEnumeration.BUS_STATION)
                .withVersion("1")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("15"))
                                .withLongitude(new BigDecimal("76"))));


        PublicationDeliveryParams publicationDeliveryParams = new PublicationDeliveryParams();
        publicationDeliveryParams.importType = ImportType.INITIAL;
        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlaceNotToBeMatched);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, publicationDeliveryParams);

        // ID is similar, but does not start with 2
        stopPlaceNotToBeMatched.setId("RUT:StopPlace:12345678910");

        PublicationDeliveryStructure publicationDelivery2 = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlaceNotToBeMatched);
        publicationDeliveryParams.importType = ImportType.ID_MATCH;
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery2, publicationDeliveryParams);

        System.out.println("Got response: \n" + response);

        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response, false);

        assertThat(result).as("no match as stop has different ID").hasSize(0);
    }

    @Test
    public void matchImportedStopWithoutLeadingZeroOriginalId() throws Exception {

        StopPlace stopPlaceToBeMatched = new StopPlace()
                .withId("RUT:StopPlace:0111111111")
                .withVersion("1")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("11"))
                                .withLongitude(new BigDecimal("77"))));


        PublicationDeliveryParams publicationDeliveryParams = new PublicationDeliveryParams();
        publicationDeliveryParams.importType = ImportType.INITIAL;
        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlaceToBeMatched);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, publicationDeliveryParams);

        // match even without leading zero and different prefix
        stopPlaceToBeMatched.setId("AKT:StopPlace:111111111");

        PublicationDeliveryStructure publicationDelivery2 = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlaceToBeMatched);
        publicationDeliveryParams.importType = ImportType.ID_MATCH;
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery2, publicationDeliveryParams);

        System.out.println("Got response: \n" + response);

        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response);

        assertThat(result).hasSize(1);
        publicationDeliveryTestHelper.hasOriginalId("RUT:StopPlace:0111111111", result.get(0));
    }

    @Test
    public void matchImportedStopOnLeadingZeroOriginalId() throws Exception {

        StopPlace stopPlaceToBeMatched = new StopPlace()
                .withId("RUT:StopPlace:111111111")
                .withVersion("1")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("11"))
                                .withLongitude(new BigDecimal("77"))));


        PublicationDeliveryParams publicationDeliveryParams = new PublicationDeliveryParams();
        publicationDeliveryParams.importType = ImportType.INITIAL;
        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlaceToBeMatched);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, publicationDeliveryParams);

        // match when stop place does not have leading zero, but the incoming has.
        stopPlaceToBeMatched.setId("AKT:StopPlace:0111111111");

        PublicationDeliveryStructure publicationDelivery2 = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlaceToBeMatched);
        publicationDeliveryParams.importType = ImportType.ID_MATCH;
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery2, publicationDeliveryParams);

        System.out.println("Got response: \n" + response);

        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response);

        assertThat(result).hasSize(1);
        publicationDeliveryTestHelper.hasOriginalId("RUT:StopPlace:111111111", result.get(0));

        // When the import type is ID_MATCH, no original ID is appended
        // publicationDeliveryTestHelper.hasOriginalId("AKT:StopPlace:0111111111", result.get(0));
    }

    @Test
    public void avoidDuplicatesInOutput() throws Exception {


        StopPlace stopPlace1 = new StopPlace()
                .withId("RUT:StopPlace:321")
                .withKeyList(
                        new KeyListStructure()
                                .withKeyValue(
                                        new KeyValueStructure()
                                                .withKey(ORIGINAL_ID_KEY)
                                                .withValue("KOL:StopPlace:123")
                                )
                )
                .withStopPlaceType(StopTypeEnumeration.BUS_STATION)
                .withVersion("1")
                .withName(new MultilingualString().withValue("somewhere"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))));

        StopPlace stopPlace2 = new StopPlace()
                .withId("RUT:StopPlace:325")
                .withKeyList(
                        new KeyListStructure()
                                .withKeyValue(
                                        new KeyValueStructure()
                                                .withKey(ORIGINAL_ID_KEY)
                                                .withValue("KOL:StopPlace:123")
                                )
                )
                .withStopPlaceType(StopTypeEnumeration.BUS_STATION)
                .withVersion("1")
                .withName(new MultilingualString().withValue("somewhere"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))));


        PublicationDeliveryParams publicationDeliveryParams = new PublicationDeliveryParams();
        publicationDeliveryParams.importType = ImportType.INITIAL;
        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace1);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, publicationDeliveryParams);

        // Import the same matching stop twice to verify no duplicates
        PublicationDeliveryStructure publicationDelivery2 = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace1, stopPlace2);
        publicationDeliveryParams.importType = ImportType.ID_MATCH;
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery2, publicationDeliveryParams);

        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response);

        assertThat(result).hasSize(1);
        publicationDeliveryTestHelper.hasOriginalId(stopPlace1.getId(), result.get(0));
    }

    @Test
    public void matchStopsOnQuayImportedId() throws Exception {

        StopPlace stopPlace1 = new StopPlace()
                .withId("RUT:StopPlace:987")
                .withVersion("1")
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("RUT:Quay:0136068001")
                                .withVersion("1")));

        StopPlace stopPlace2 = new StopPlace()
                .withId("RUT:StopPlace:666")
                .withVersion("1")
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("RUT:Quay:0136068001")
                                .withVersion("1")));

        PublicationDeliveryParams publicationDeliveryParams = new PublicationDeliveryParams();
        publicationDeliveryParams.importType = ImportType.INITIAL;
        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace1);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, publicationDeliveryParams);

        PublicationDeliveryStructure publicationDelivery2 = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace2);
        publicationDeliveryParams.importType = ImportType.ID_MATCH;
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery2, publicationDeliveryParams);

        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response);

        assertThat(result).hasSize(1);
        publicationDeliveryTestHelper.hasOriginalId(stopPlace1.getId(), result.get(0));
    }

    @Test
    public void matchStopsOnQuayNonNumericImportedId() throws Exception {

        StopPlace stopPlace1 = new StopPlace()
                .withId("RUT:StopPlace:xyz")
                .withVersion("1")
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("RUT:Quay:zzzz")
                                .withVersion("1")));

        StopPlace stopPlace2 = new StopPlace()
                .withId("RUT:StopPlace:ppp")
                .withVersion("1")
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("RUT:Quay:zzzz")
                                .withVersion("1")));

        PublicationDeliveryParams publicationDeliveryParams = new PublicationDeliveryParams();
        publicationDeliveryParams.importType = ImportType.INITIAL;
        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace1);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, publicationDeliveryParams);

        PublicationDeliveryStructure publicationDelivery2 = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace2);
        publicationDeliveryParams.importType = ImportType.ID_MATCH;
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery2, publicationDeliveryParams);

        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response);

        assertThat(result).hasSize(1);
        publicationDeliveryTestHelper.hasOriginalId(stopPlace1.getId(), result.get(0));
    }

    @Test
    public void matchStopsWithZeroPaddedQuayOriginalId() throws Exception {

        StopPlace stopPlace1 = new StopPlace()
                .withId("RUT:StopPlace:987999")
                .withVersion("1")
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("BRA:Quay:8888888")
                                .withVersion("1")));

        StopPlace stopPlace2 = new StopPlace()
                .withId("BRA:StopPlace:99999")
                .withVersion("1")
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("RUT:Quay:08888888")
                                .withVersion("1")));

        PublicationDeliveryParams publicationDeliveryParams = new PublicationDeliveryParams();
        publicationDeliveryParams.importType = ImportType.INITIAL;
        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace1);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, publicationDeliveryParams);

        PublicationDeliveryStructure publicationDelivery2 = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace2);
        publicationDeliveryParams.importType = ImportType.ID_MATCH;
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery2, publicationDeliveryParams);

        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response);

        assertThat(result).hasSize(1);
        publicationDeliveryTestHelper.hasOriginalId(stopPlace1.getId(), result.get(0));
    }

    @Test
    public void matchStopsOnQuayNetexId() throws Exception {

        StopPlace stopPlace1 = new StopPlace()
                .withId("RUT:StopPlace:666")
                .withVersion("1")
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("NSR:Quay:0136068001")
                                .withVersion("1")));

        StopPlace stopPlace2 = new StopPlace()
                .withId("RUT:StopPlace:187")
                .withVersion("1")
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("NSR:Quay:0136068001")
                                .withVersion("1")));

        PublicationDeliveryParams publicationDeliveryParams = new PublicationDeliveryParams();
        publicationDeliveryParams.importType = ImportType.INITIAL;
        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace1);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, publicationDeliveryParams);

        PublicationDeliveryStructure publicationDelivery2 = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace2);
        publicationDeliveryParams.importType = ImportType.ID_MATCH;
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery2, publicationDeliveryParams);

        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response);

        assertThat(result).hasSize(1);
        publicationDeliveryTestHelper.hasOriginalId(stopPlace1.getId(), result.get(0));
    }

    /**
     * https://rutebanken.atlassian.net/browse/NRP-1558
     */
    @Test
    public void matchOneIncomingStopToMultipleTiamatStops() throws JAXBException, IOException, SAXException {

        String initiallyImportedStops = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<PublicationDelivery xmlns=\"http://www.netex.org.uk/netex\">\n" +
                "  <PublicationTimestamp>2017-05-05T15:12:33.618+02:00</PublicationTimestamp>\n" +
                "  <ParticipantRef>XYZ</ParticipantRef>\n" +
                "  <dataObjects>\n" +
                "    <SiteFrame id=\"initial\" version=\"1\">\n" +
                "      <stopPlaces>\n" +
                "        <StopPlace id=\"SOF:StopPlace:14162447Bus\" version=\"1\">\n" +
                "          <keyList>\n" +
                "            <KeyValue>\n" +
                "              <Key>imported-id</Key>\n" +
                "              <Value>SOF:StopPlace:14162447,NRI:StopPlace:761115465</Value>\n" +
                "            </KeyValue>\n" +
                "          </keyList>\n" +
                "          <Name lang=\"no\">Nordeide kai</Name>\n" +
                "          <Centroid>\n" +
                "            <Location srsName=\"EPSG:4326\">\n" +
                "              <Longitude>5.98568</Longitude>\n" +
                "              <Latitude>61.172894</Latitude>\n" +
                "            </Location>\n" +
                "          </Centroid>\n" +
                "          <AccessibilityAssessment id=\"SOF:AccessibilityAssessment:14162447Bus\" version=\"1\">\n" +
                "            <MobilityImpairedAccess>unknown</MobilityImpairedAccess>\n" +
                "            <limitations>\n" +
                "              <AccessibilityLimitation>\n" +
                "                <WheelchairAccess>unknown</WheelchairAccess>\n" +
                "                <StepFreeAccess>unknown</StepFreeAccess>\n" +
                "              </AccessibilityLimitation>\n" +
                "            </limitations>\n" +
                "          </AccessibilityAssessment>\n" +
                "          <StopPlaceType>onstreetBus</StopPlaceType>\n" +
                "          <Weighting>interchangeAllowed</Weighting>\n" +
                "          <quays>\n" +
                "            <n:Quay xmlns:n=\"http://www.netex.org.uk/netex\" id=\"SOF:Quay:1416244702\" version=\"1\">\n" +
                "              <keyList>\n" +
                "                <KeyValue>\n" +
                "                  <Key>imported-id</Key>\n" +
                "                  <Value>NRI:Quay:762132224</Value>\n" +
                "                </KeyValue>\n" +
                "              </keyList>\n" +
                "              <Centroid>\n" +
                "                <Location srsName=\"EPSG:4326\">\n" +
                "                  <Longitude>5.9861436</Longitude>\n" +
                "                  <Latitude>61.173157</Latitude>\n" +
                "                </Location>\n" +
                "              </Centroid>\n" +
                "              <Lighting>unknown</Lighting>\n" +
                "              <PublicCode>2</PublicCode>\n" +
                "              <!--Enriched CompassBearing-->\n" +
                "              <n:CompassBearing>311.0</n:CompassBearing>\n" +
                "            </n:Quay>\n" +
                "            <n:Quay xmlns:n=\"http://www.netex.org.uk/netex\" id=\"SOF:Quay:1416244701\" version=\"1\">\n" +
                "              <keyList>\n" +
                "                <KeyValue>\n" +
                "                  <Key>imported-id</Key>\n" +
                "                  <Value>NRI:Quay:762054659</Value>\n" +
                "                </KeyValue>\n" +
                "              </keyList>\n" +
                "              <Centroid>\n" +
                "                <Location srsName=\"EPSG:4326\">\n" +
                "                  <Longitude>5.9847283</Longitude>\n" +
                "                  <Latitude>61.172707</Latitude>\n" +
                "                </Location>\n" +
                "              </Centroid>\n" +
                "              <Lighting>unknown</Lighting>\n" +
                "              <PublicCode>1</PublicCode>\n" +
                "              <!--Enriched CompassBearing-->\n" +
                "              <n:CompassBearing>233.0</n:CompassBearing>\n" +
                "            </n:Quay>\n" +
                "          </quays>\n" +
                "        </StopPlace>\n" +
                "        <StopPlace id=\"SOF:StopPlace:14162447Ferje\" version=\"1\">\n" +
                "          <keyList>\n" +
                "            <KeyValue>\n" +
                "              <Key>imported-id</Key>\n" +
                "              <Value>SOF:StopPlace:14162447,NRI:StopPlace:761115465</Value>\n" +
                "            </KeyValue>\n" +
                "          </keyList>\n" +
                "          <Name lang=\"no\">Nordeide kai</Name>\n" +
                "          <Centroid>\n" +
                "            <Location srsName=\"EPSG:4326\">\n" +
                "              <Longitude>5.98568</Longitude>\n" +
                "              <Latitude>61.172894</Latitude>\n" +
                "            </Location>\n" +
                "          </Centroid>\n" +
                "          <AccessibilityAssessment id=\"SOF:AccessibilityAssessment:14162447Ferje\" version=\"1\">\n" +
                "            <MobilityImpairedAccess>unknown</MobilityImpairedAccess>\n" +
                "            <limitations>\n" +
                "              <AccessibilityLimitation>\n" +
                "                <WheelchairAccess>unknown</WheelchairAccess>\n" +
                "                <StepFreeAccess>unknown</StepFreeAccess>\n" +
                "              </AccessibilityLimitation>\n" +
                "            </limitations>\n" +
                "          </AccessibilityAssessment>\n" +
                "          <StopPlaceType>harbourPort</StopPlaceType>\n" +
                "          <Weighting>interchangeAllowed</Weighting>\n" +
                "          <quays>\n" +
                "            <n:Quay xmlns:n=\"http://www.netex.org.uk/netex\" id=\"SOF:Quay:1416244703\" version=\"1\">\n" +
                "              <keyList>\n" +
                "                <KeyValue>\n" +
                "                  <Key>imported-id</Key>\n" +
                "                  <Value>NRI:Quay:762054660</Value>\n" +
                "                </KeyValue>\n" +
                "              </keyList>\n" +
                "              <Centroid>\n" +
                "                <Location srsName=\"EPSG:4326\">\n" +
                "                  <Longitude>5.986519</Longitude>\n" +
                "                  <Latitude>61.173313</Latitude>\n" +
                "                </Location>\n" +
                "              </Centroid>\n" +
                "              <Lighting>unknown</Lighting>\n" +
                "              <PublicCode>3</PublicCode>\n" +
                "              <!--Enriched CompassBearing-->\n" +
                "              <n:CompassBearing>132.0</n:CompassBearing>\n" +
                "            </n:Quay>\n" +
                "          </quays>\n" +
                "        </StopPlace>\n" +
                "      </stopPlaces>\n" +
                "    </SiteFrame>\n" +
                "  </dataObjects>\n" +
                "</PublicationDelivery>\n";

        String idMatch = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<PublicationDelivery xmlns=\"http://www.netex.org.uk/netex\">\n" +
                "  <PublicationTimestamp>2017-05-05T15:12:33.618+02:00</PublicationTimestamp>\n" +
                "  <ParticipantRef>ZZZ</ParticipantRef>\n" +
                "  <dataObjects>\n" +
                "    <SiteFrame id=\"idmatch\" version=\"1\">\n" +
                "      <stopPlaces>\n" +
                "        <StopPlace version=\"1\" id=\"SOF:StopPlace:14162447\">\n" +
                "          <Name lang=\"no\" textIdType=\"\">Nordeide kai</Name>\n" +
                "          <Centroid>\n" +
                "            <Location>\n" +
                "              <Longitude>5.985714134603235</Longitude>\n" +
                "              <Latitude>61.1729031032732</Latitude>\n" +
                "            </Location>\n" +
                "          </Centroid>\n" +
                "          <StopPlaceType>onstreetBus</StopPlaceType>\n" +
                "          <quays>\n" +
                "            <Quay version=\"1\" id=\"SOF:Quay:1416244704\">\n" +
                "              <Name lang=\"no\" textIdType=\"\">Nordeide kai</Name>\n" +
                "              <Centroid>\n" +
                "                <Location>\n" +
                "                  <Longitude>5.98541250888936193774725325056351721286773681640625</Longitude>\n" +
                "                  <Latitude>61.17240898514889835269059403799474239349365234375</Latitude>\n" +
                "                </Location>\n" +
                "              </Centroid>\n" +
                "            </Quay>\n" +
                "            <Quay version=\"1\" id=\"SOF:Quay:1416244703\">\n" +
                "              <Name lang=\"no\" textIdType=\"\">Nordeide kai</Name>\n" +
                "              <Centroid>\n" +
                "                <Location>\n" +
                "                  <Longitude>5.98656984073603748441882999031804502010345458984375</Longitude>\n" +
                "                  <Latitude>61.17334218350138286268702358938753604888916015625</Latitude>\n" +
                "                </Location>\n" +
                "              </Centroid>\n" +
                "            </Quay>\n" +
                "            <Quay version=\"1\" id=\"SOF:Quay:1416244702\">\n" +
                "              <Name lang=\"no\" textIdType=\"\">Nordeide kai</Name>\n" +
                "              <Centroid>\n" +
                "                <Location>\n" +
                "                  <Longitude>5.98614141196775673137153717107139527797698974609375</Longitude>\n" +
                "                  <Latitude>61.17315295435830790893305675126612186431884765625</Latitude>\n" +
                "                </Location>\n" +
                "              </Centroid>\n" +
                "              <CompassBearing>132.0</CompassBearing>\n" +
                "            </Quay>\n" +
                "            <Quay version=\"1\" id=\"SOF:Quay:1416244701\">\n" +
                "              <Name lang=\"no\" textIdType=\"\">Nordeide kai</Name>\n" +
                "              <Centroid>\n" +
                "                <Location>\n" +
                "                  <Longitude>5.98473277681978288455866277217864990234375</Longitude>\n" +
                "                  <Latitude>61.1727082900842020762866013683378696441650390625</Latitude>\n" +
                "                </Location>\n" +
                "              </Centroid>\n" +
                "            </Quay>\n" +
                "          </quays>\n" +
                "        </StopPlace>\n" +
                "      </stopPlaces>\n" +
                "    </SiteFrame>\n" +
                "  </dataObjects>\n" +
                "</PublicationDelivery>\n";

        PublicationDeliveryParams publicationDeliveryParams = new PublicationDeliveryParams();
        publicationDeliveryParams.importType = ImportType.INITIAL;
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(initiallyImportedStops, publicationDeliveryParams);

        publicationDeliveryParams.importType = ImportType.ID_MATCH;
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(idMatch, publicationDeliveryParams);

        List<StopPlace> stops = publicationDeliveryTestHelper.extractStopPlaces(response);

        assertThat(stops).hasSize(2);
    }
}
