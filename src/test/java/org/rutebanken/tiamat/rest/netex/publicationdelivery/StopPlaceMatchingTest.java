package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.junit.Test;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.importer.ImportType;
import org.rutebanken.tiamat.importer.PublicationDeliveryParams;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
}
