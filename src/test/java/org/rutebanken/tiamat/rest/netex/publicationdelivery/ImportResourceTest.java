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

import com.google.common.collect.Sets;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.xml.bind.JAXBException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.PrivateCodeStructure;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopTypeEnumeration;
import org.rutebanken.netex.model.ValidBetween;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.ImportType;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ImportResourceTest extends TiamatIntegrationTest {
    //todo: refactor this test and move xml files in test resources

    @Autowired
    private ImportResource importResource;

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    @Autowired
    private PublicationDeliveryHelper publicationDeliveryHelper;

    private LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    /**
     * When sending a stop place with the same ID twice, the same stop place must be returned.
     * When importing multiple stop places and those exists, make sure no Lazy Initialization Exception is thrown.
     */

    @Before
    public void setUp() {
        setUpSecurityContext();
    }
    @Test
    public void publicationDeliveriesWithDuplicateStopPlace() throws Exception {

        StopPlace stopPlace = new StopPlace()
                .withId("RUT:StopPlace:123123")
                .withVersion("1")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))));

        StopPlace stopPlace2 = new StopPlace()
                .withId("RUT:StopPlace:123123")
                .withVersion("1")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("10"))
                                .withLongitude(new BigDecimal("72"))));


        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        PublicationDeliveryStructure publicationDelivery2 = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace2);
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery2);


        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response);

        assertThat(result).as("Expecting one stop place in return, as there is no need to return duplicates").hasSize(1);
    }

    @Test
    public void publicationDeliveriesWithBusStationStopAndOnStreetBus() throws Exception {

        StopPlace stopPlace = new StopPlace()
                .withId("RUT:StopPlace:123123")
                .withStopPlaceType(StopTypeEnumeration.BUS_STATION)
                .withVersion("1")
                .withName(new MultilingualString().withValue("somewhere"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))));

        StopPlace stopPlace2 = new StopPlace()
                .withId("RUT:StopPlace:987654321")
                .withVersion("1")
                .withStopPlaceType(StopTypeEnumeration.ONSTREET_BUS)
                .withName(new MultilingualString().withValue("somewhere"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))));


        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        PublicationDeliveryStructure publicationDelivery2 = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace2);
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery2);


        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response);

        assertThat(result).as("Expecting one stop place in return, as stops imported has onstreet bus and bus station as type").hasSize(1);
        publicationDeliveryTestHelper.hasOriginalId("RUT:StopPlace:123123", result.getFirst());
        publicationDeliveryTestHelper.hasOriginalId("RUT:StopPlace:987654321", result.getFirst());
    }

    @Test
    public void ignoreStopPlaceTypes() throws Exception {

        StopPlace stopPlace = new StopPlace()
                .withId("XYZ:StopPlace:321")
                .withVersion("3")
                .withStopPlaceType(StopTypeEnumeration.BUS_STATION);


        ImportParams importParams = new ImportParams();
        importParams.ignoreStopTypes = Sets.newHashSet(org.rutebanken.tiamat.model.StopTypeEnumeration.BUS_STATION);
        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, importParams);
        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response, false);

        assertThat(result).isEmpty();
    }

    @Test
    public void allowOnlyStopPlaceTypes() throws Exception {

        StopPlace stopPlace = new StopPlace()
                .withId("XYZ:StopPlace:3231")
                .withVersion("2")
                .withStopPlaceType(StopTypeEnumeration.METRO_STATION);

        StopPlace other = new StopPlace()
                .withId("XYZ:StopPlace:9988")
                .withVersion("2")
                .withStopPlaceType(StopTypeEnumeration.AIRPORT);

        ImportParams importParams = new ImportParams();
        importParams.allowOnlyStopTypes = Sets.newHashSet(org.rutebanken.tiamat.model.StopTypeEnumeration.METRO_STATION);
        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace, other);
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, importParams);
        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStopPlaceType()).isEqualTo(StopTypeEnumeration.METRO_STATION);
    }

    @Test
    public void forceStopPlaceType() throws Exception {

        StopPlace stopPlace = new StopPlace()
                .withId("XYZ:StopPlace:3231")
                .withVersion("2");

        ImportParams importParams = new ImportParams();
        importParams.forceStopType = org.rutebanken.tiamat.model.StopTypeEnumeration.BUS_STATION;
        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, importParams);
        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStopPlaceType()).isEqualTo(StopTypeEnumeration.BUS_STATION);
    }

    @Test
    public void allowOtherWhenMatchingExistingStopPlacesWithImportTypeMATCH() throws Exception {

        StopPlace stopPlaceToBeMatched = new StopPlace()
                .withId("RUT:StopPlace:987978")
                .withStopPlaceType(StopTypeEnumeration.BUS_STATION)
                .withVersion("1")
                .withName(new MultilingualString().withValue("somewhere"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))));

        StopPlace incomingStopPlace = new StopPlace()
                .withId("RUT:StopPlace:123546789")
                .withVersion("1")
                .withStopPlaceType(StopTypeEnumeration.OTHER)
                .withName(new MultilingualString().withValue("somewhere"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))));


        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlaceToBeMatched);
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        PublicationDeliveryStructure publicationDelivery2 = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(incomingStopPlace);
        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.MATCH;
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery2, importParams);


        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response);

        assertThat(result).as("Expecting one stop place in return, as stops imported has onstreet bus and bus station as type").hasSize(1);
        publicationDeliveryTestHelper.hasOriginalId(stopPlaceToBeMatched.getId(), result.getFirst());
        publicationDeliveryTestHelper.hasOriginalId(incomingStopPlace.getId(), result.getFirst());
    }

    /**
     * When sending a stop place multiple times with separate 'imported ids' - all 'imported ids' should be kept
     */
    @Test
    public void publicationDeliveryWithImportedIdUpdates() throws Exception {

        StopPlace stopPlace = new StopPlace()
                .withId("RUT:StopPlace:123")
                .withVersion("1")
                .withStopPlaceType(StopTypeEnumeration.BUS_STATION)
                .withName(new MultilingualString().withValue("Test"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("10"))
                                .withLongitude(new BigDecimal("72"))));

        StopPlace stopPlace2 = new StopPlace()
                .withId("RUT:StopPlace:1234")
                .withVersion("2")
                .withStopPlaceType(StopTypeEnumeration.BUS_STATION)
                .withName(new MultilingualString().withValue("Test"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("10"))
                                .withLongitude(new BigDecimal("72"))));

        StopPlace stopPlace3 = new StopPlace()
                .withId("RUT:StopPlace:12345")
                .withVersion("3")
                .withStopPlaceType(StopTypeEnumeration.BUS_STATION)
                .withName(new MultilingualString().withValue("Test"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("10"))
                                .withLongitude(new BigDecimal("72"))));


        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        PublicationDeliveryStructure publicationDelivery2 = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace2);
        PublicationDeliveryStructure response2 = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery2);

        PublicationDeliveryStructure publicationDelivery3 = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace3);
        PublicationDeliveryStructure response3 = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery3);


        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response);
        List<StopPlace> result2 = publicationDeliveryTestHelper.extractStopPlaces(response2);
        List<StopPlace> result3 = publicationDeliveryTestHelper.extractStopPlaces(response3);

        assertThat(result).as("Expecting one stop place in return, as there is no need to return duplicates").hasSize(1);
        assertThat(result2).as("Expecting one stop place in return, as there is no need to return duplicates").hasSize(1);
        assertThat(result3).as("Expecting one stop place in return, as there is no need to return duplicates").hasSize(1);
        assertThat(result3.getFirst().getVersion()).isEqualTo("3");
    }


    /**
     * Real life example: Two stops with different IDs should be merged into one, and their quays should be added.
     *
     * @throws Exception
     */
    @Test
    public void publicationDeliveryWithDuplicateStopPlaceWithDifferentId() throws Exception {

        String name = "Varnaveien bensin";

        StopPlace stopPlace = new StopPlace()
                .withName(new MultilingualString().withValue(name))
                .withId("OST:StopArea:01360680")
                .withVersion("1")
                .withStopPlaceType(StopTypeEnumeration.ONSTREET_BUS)
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("59.4172358106178"))
                                .withLongitude(new BigDecimal("10.66847409589632"))))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("OST:StopArea:0136068001")
                                .withVersion("1")
                                .withName(new MultilingualString().withValue(name))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLongitude(new BigDecimal("10.6684740958963200085918288095854222774505615234375"))
                                                .withLatitude(new BigDecimal("59.41723581061779668743838556110858917236328125"))))));


        StopPlace stopPlace2 = new StopPlace()
                .withName(new MultilingualString().withValue(name))
                .withId("OST:StopArea:01040720")
                .withVersion("1")
                .withStopPlaceType(StopTypeEnumeration.ONSTREET_BUS)
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("59.41727956639375"))
                                .withLongitude(new BigDecimal("10.66866436373097"))))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("OST:StopArea:0104072001")
                                .withVersion("1")
                                .withName(new MultilingualString().withValue(name))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLongitude(new BigDecimal("10.6686643637309706122096031322143971920013427734375"))
                                                .withLatitude(new BigDecimal("59.41727956639375207714692805893719196319580078125"))))));


        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace, stopPlace2);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        List<StopPlace> result = publicationDeliveryTestHelper.extractStopPlaces(response);

        assertThat(result).as("Expecting one stop place in return, as there is no need to return the same matching stop place twice").hasSize(1);
        String importedIds = result.getFirst().getKeyList().getKeyValue()
                .stream()
                .filter(kv -> "imported-id".equals(kv.getKey()))
                .map(KeyValueStructure::getValue)
                .findFirst()
                .get();
        assertThat(importedIds).contains(stopPlace.getId());
        assertThat(importedIds).contains(stopPlace2.getId());
        assertThat(result.getFirst().getQuays().getQuayRefOrQuay()).hasSize(2);
    }

    @Test
    public void publicationDeliveryWithStopPlaceAndQuay() throws Exception {

        StopPlace stopPlace = new StopPlace()
                .withId("NSR:StopPlace:123123")
                .withVersion("1")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("XYZ:Quay:4")
                                .withVersion("1")
                                .withPrivateCode(new PrivateCodeStructure().withValue("B02").withType("type"))
                                .withName(new MultilingualString().withValue("quay"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("9.1"))
                                                .withLongitude(new BigDecimal("71.2"))))));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);

        PublicationDeliveryStructure firstResponse = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(firstResponse);

        assertThat(actualStopPlace.getQuays()).as("quays should not be null").isNotNull();

        Quay quay = actualStopPlace.getQuays()
                .getQuayRefOrQuay().stream()
                .filter(object -> object instanceof Quay)
                .map(object -> ((Quay) object))
                .findFirst()
                .get();


        assertThat(quay.getName().getValue()).isEqualTo("quay");
        assertThat(quay.getId()).isNotNull();
        assertThat(quay.getPrivateCode().getValue()).isEqualTo("B02");
        assertThat(quay.getPrivateCode().getType()).isEqualTo("type");

    }

    /**
     * When importing a stop place witch is a direct match with import type MERGE. No changes should be made to the stop place.
     * <p>
     * https://rutebanken.atlassian.net/browse/NRP-1587
     */
    @Test
    public void initialImportThenMergeShouldNotMergeNearbyQuays() throws Exception {

        // Quays with different original ID
        StopPlace stopPlace = new StopPlace()
                .withId("XYZ:StopPlace:123123")
                .withVersion("1")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("XYZ:Quay:4")
                                .withVersion("1")
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("9.1"))
                                                .withLongitude(new BigDecimal("71.2")))))
                        .withQuayRefOrQuay(new Quay()
                                .withId("XYZ:Quay:5")
                                .withVersion("1")
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("9.1"))
                                                .withLongitude(new BigDecimal("71.2"))))));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);

        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.INITIAL;
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, importParams);

        importParams.importType = ImportType.MERGE;
        PublicationDeliveryStructure mergeResponse = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, importParams);

        StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(mergeResponse);

        assertThat(actualStopPlace.getQuays()).as("quays should not be null").isNotNull();

        List<Quay> quays = publicationDeliveryTestHelper.extractQuays(actualStopPlace);

        assertThat(quays).hasSize(2);

        quays.forEach(quay -> {
            Set<String> importedIds = publicationDeliveryHelper.getImportedIds(quay);
            assertThat(importedIds).hasSize(1);
        });

    }

    /**
     * https://rutebanken.atlassian.net/browse/NRP-830
     */
    @Test
    public void handleChangesToQuaysWithoutSavingDuplicates() throws Exception {

        /**
         * StopPlace{name=Fredheimveien (no),
         *      quays=[Quay{name=Fredheimveien (no), centroid=POINT (11.142676854561447 59.83314448493502), keyValues={imported-id=Value{id=0, items=[RUT:StopArea:0229012201]}}},
         *        Quay{name=Fredheimveien (no), centroid=POINT (11.142897636770531 59.83297022041692), keyValues={imported-id=Value{id=0, items=[RUT:StopArea:0229012202]}}}],
         *    centroid=POINT (11.142676854561447 59.83314448493502),
         *    keyValues={imported-id=Value{id=0, items=[RUT:StopArea:02290122]}}}
         */
        MultilingualString name = new MultilingualString().withValue("Fredheimveien").withLang("no");


        StopPlace stopPlace1 = new StopPlace()
                .withId("RUT:StopArea:02290122")
                .withVersion("1")
                .withName(name)
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("59.83314448493502"))
                                .withLongitude(new BigDecimal("11.142676854561447"))))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                        .withId("RUT:StopArea:0229012201")
                                        .withVersion("1")
                                        .withName(name)
                                        .withCentroid(new SimplePoint_VersionStructure()
                                                .withLocation(new LocationStructure()
                                                        .withLatitude(new BigDecimal("59.83314448493502"))
                                                        .withLongitude(new BigDecimal("11.142676854561447")))),
                                new Quay()
                                        .withId("RUT:StopArea:0229012202")
                                        .withVersion("1")
                                        .withName(name)
                                        .withCentroid(new SimplePoint_VersionStructure()
                                                .withLocation(new LocationStructure()
                                                        .withLatitude(new BigDecimal("59.83297022041692"))
                                                        .withLongitude(new BigDecimal("11.142897636770531"))))
                        ));

        /**
         * StopPlace{name=Fredheimveien (no),
         *      quays=[Quay{name=Fredheimveien (no), centroid=POINT (11.142902250197631 59.83304200609072), keyValues={imported-id=Value{id=0, items=[RUT:StopArea:0229012201]}}},
         *          Quay{name=Fredheimveien (no), centroid=POINT (11.14317535486387 59.832848923825956), keyValues={imported-id=Value{id=0, items=[RUT:StopArea:0229012202]}}}],
         *
         *  centroid=POINT (11.142902250197631 59.83304200609072),
         *  keyValues={imported-id=Value{id=0, items=[RUT:StopArea:02290122]}}}
         *
         */
        StopPlace stopPlace2 = new StopPlace()
                .withId("RUT:StopArea:02290122")
                .withVersion("1")
                .withName(name)
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("59.83304200609072"))
                                .withLongitude(new BigDecimal("11.142902250197631"))))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(
                                new Quay()
                                        .withId("BRA:StopArea:0229012201")
                                        .withVersion("1")
                                        .withName(name)
                                        .withCentroid(new SimplePoint_VersionStructure()
                                                .withLocation(new LocationStructure()
                                                        .withLatitude(new BigDecimal("59.83304200609072"))
                                                        .withLongitude(new BigDecimal("11.142902250197631")))),
                                new Quay()
                                        .withId("BRA:StopArea:0229012202")
                                        .withVersion("1")
                                        .withName(name)
                                        .withCentroid(new SimplePoint_VersionStructure()
                                                .withLocation(new LocationStructure()
                                                        .withLatitude(new BigDecimal("59.832848923825956"))
                                                        .withLongitude(new BigDecimal("11.14317535486387"))))
                        ));

        List<PublicationDeliveryStructure> publicationDeliveryStructures = new ArrayList<>();

        publicationDeliveryStructures.add(publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace1));
        publicationDeliveryStructures.add(publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace2));

        for (PublicationDeliveryStructure pubde : publicationDeliveryStructures) {
            PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(pubde);
            StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(response);
            assertThat(actualStopPlace.getQuays().getQuayRefOrQuay()).hasSize(2);
            List<Quay> quays = publicationDeliveryTestHelper.extractQuays(actualStopPlace);

            long matches = quays
                    .stream()
                    .map(quay -> quay.getKeyList())
                    .flatMap(keyList -> keyList.getKeyValue().stream())
                    .map(keyValue -> keyValue.getValue())
                    .map(value -> value.split(","))
                    .flatMap(values -> Stream.of(values))
                    .filter(value -> value.equals("RUT:StopArea:0229012202") || value.equals("RUT:StopArea:0229012201"))
                    .count();
            assertThat(matches)
                    .as("Expecting quay to contain two matching orignal IDs in key val")
                    .isEqualTo(2);
        }
    }

    /**
     * Import stop place StopPlace{name=Skaret (no), quays=
     * [Quay{name=Skaret (no), centroid=POINT (7.328336965528884 62.799557598196465), keyValues={imported-id=Value{id=0, items=[MOR:StopArea:1548612801]}}},
     * Quay{name=Skaret (no), keyValues={imported-id=Value{id=0, items=[MOR:StopArea:1548575301]}}}],
     * keyValues={imported-id=Value{id=0, items=[MOR:StopArea:15485753]}}}
     */
    @Test
    public void importStopWithoutCoordinatesWithQuays1() throws Exception {

        StopPlace stopPlace = new StopPlace()
                .withId("MOR:StopArea:15485753")
                .withVersion("1")
                .withName(new MultilingualString().withValue("Skaret").withLang("no"))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                        .withVersion("1")
                                        .withId("MOR:StopArea:1548612801")
                                        .withName(new MultilingualString().withValue("Skaret").withLang("no"))
                                        .withCentroid(new SimplePoint_VersionStructure().withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("62.799557598196465"))
                                                .withLongitude(new BigDecimal("7.328336965528884")))),
                                new Quay()
                                        .withId("MOR:StopArea:1548575301")
                                        .withVersion("1")
                                        .withName(new MultilingualString().withValue("Skaret").withLang("no"))));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        // Exception should not have been thrown
        StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(response);

        List<Quay> actualQuays = publicationDeliveryTestHelper.extractQuays(actualStopPlace);
        assertThat(actualQuays).as("quays should not be null").isNotNull();
    }

    @Test
    public void createdAndChangedTimestampsMustBeSetOnStopPlaceAndQuays() throws Exception {

        StopPlace stopPlace = new StopPlace()
                .withId("XYZ:StopPlace:4")
                .withVersion("1")
                .withName(new MultilingualString().withValue("new"))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withVersion("1")
                                .withId("XYZ:Quay:5")
                                .withName(new MultilingualString().withValue("new quay"))
                                .withCentroid(new SimplePoint_VersionStructure().withLocation(new LocationStructure()
                                        .withLatitude(new BigDecimal("62.799557598196465"))
                                        .withLongitude(new BigDecimal("7.328336965528884"))))));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(response);
        assertThat(actualStopPlace.getCreated()).as("The imported stop place's created date must not be null").isNotNull();

        List<Quay> actualQuays = publicationDeliveryTestHelper.extractQuays(actualStopPlace);

        assertThat(actualQuays.getFirst().getCreated()).as("The imported quay's created date must not be null").isNotNull();
    }

    @Test
    public void validityMustBeSetOnImportedStop() throws Exception {

        StopPlace stopPlace = new StopPlace()
                .withId("XYZ:StopPlace:123")
                .withVersion("1")
                .withName(new MultilingualString().withValue("new"));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(response);

        List<ValidBetween> actualValidBetween = actualStopPlace.getValidBetween();

        assertThat(actualValidBetween)
                .as("Stop Place should have actualValidBetween set")
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        ValidBetween validBetween = actualValidBetween.getFirst();
        assertThat(validBetween.getFromDate())
                .as("From date should be set")
                .isNotNull();

    }

    @Test
    public void updateStopPlaceShouldHaveItsDateChanged() throws Exception {

        StopPlace stopPlace = new StopPlace()
                .withId("XYZ:StopPlace:123")
                .withVersion("1")
                .withName(new MultilingualString().withValue("new"));

        PublicationDeliveryStructure firstPublicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);
        PublicationDeliveryStructure firstResponse = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(firstPublicationDelivery);

        StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(firstResponse);
        LocalDateTime changedDate = actualStopPlace.getChanged();

        // Add a Quay to the stop place so that it will be updated.
        stopPlace.withQuays(
                new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withVersion("1")
                                .withId("XYZ:Quay:321")
                                .withName(new MultilingualString().withValue("new quay"))
                                .withCentroid(new SimplePoint_VersionStructure().withLocation(new LocationStructure()
                                        .withLatitude(new BigDecimal("62.799557598196465"))
                                        .withLongitude(new BigDecimal("7.328336965528884"))))));

        PublicationDeliveryStructure secondPublicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);
        PublicationDeliveryStructure secondResponse = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(secondPublicationDelivery);

        StopPlace changedStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(secondResponse);
        assertThat(changedDate).as("The changed date for stop should not be the same as the first time it was imported")
                .isNotEqualTo(changedStopPlace.getChanged());
    }


    @Test
    public void importStopPlaceWithoutCoordinates() throws Exception {

        String chouetteId = "OPP:StopArea:123";

        StopPlace stopPlace = new StopPlace()
                .withId(chouetteId)
                .withVersion("1")
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withName(new MultilingualString().withValue("quay"))
                                .withId("XYZ:Quay:1")
                                .withVersion("1")));

        PublicationDeliveryStructure firstPublicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(firstPublicationDelivery);

        StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(response);

        assertThat(actualStopPlace).isNotNull();

    }

    @Test
    public void matchStopPlaceWithoutCoordinates() throws Exception {

        String chouetteId = "HED:StopArea:321321";

        StopPlace stopPlace = new StopPlace()
                .withId(chouetteId)
                .withVersion("1")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withVersion("1")
                                .withId(chouetteId + 1)
                                .withName(new MultilingualString().withValue("quay"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("9.1"))
                                                .withLongitude(new BigDecimal("71.2"))))));

        PublicationDeliveryStructure firstPublicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);
        PublicationDeliveryStructure firstResponse = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(firstPublicationDelivery);
        StopPlace firstStopPlaceReturned = publicationDeliveryTestHelper.findFirstStopPlace(firstResponse);
        // Same ID, but no coordinates
        StopPlace stopPlaceWithoutCoordinates = new StopPlace()
                .withId(chouetteId)
                .withVersion("1")
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("XYZ:Quay:1")
                                .withVersion("1")
                                .withName(new MultilingualString().withValue("quay"))));

        PublicationDeliveryStructure secondPublicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlaceWithoutCoordinates);
        PublicationDeliveryStructure secondResponse = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(secondPublicationDelivery);

        StopPlace secondStopPlaceReturned = publicationDeliveryTestHelper.findFirstStopPlace(secondResponse);
        assertThat(secondStopPlaceReturned.getId())
                .as("Expecting IDs to be the same, because the chouette ID is the same")
                .isEqualTo(firstStopPlaceReturned.getId());

    }

    @Test
    public void importPublicationDeliveryAndExpectMappedIdInReturn() throws Exception {

        String originalQuayId = "XYZ:Quay:321321";

        StopPlace stopPlace = new StopPlace()
                .withId("XYZ:StopPlace:123123")
                .withVersion("1")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId(originalQuayId)
                                .withVersion("1")
                                .withName(new MultilingualString().withValue("quay"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("9.1"))
                                                .withLongitude(new BigDecimal("71.2"))))));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);
        PublicationDeliveryStructure firstResponse = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(firstResponse);

        publicationDeliveryTestHelper.hasOriginalId(stopPlace.getId(), actualStopPlace);

        Quay quay = actualStopPlace.getQuays()
                .getQuayRefOrQuay()
                .stream()
                .peek(System.out::println)
                .filter(object -> object instanceof Quay)
                .map(object -> ((Quay) object))
                .peek(System.out::println)
                .findFirst().get();

        publicationDeliveryTestHelper.hasOriginalId(originalQuayId, quay);
    }

    @Test
    public void importPublicationDeliveryAndExpectCertainWordsToBeRemovedFromNames() throws Exception {
        StopPlace stopPlace = new StopPlace()
                .withId("XYZ:stoparea:1")
                .withVersion("1")
                .withName(new MultilingualString().withValue("Steinerskolen Moss (Buss)"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("XYZ:boardingpos:2")
                                .withVersion("1")
                                .withName(new MultilingualString().withValue("Steinerskolen [tog]"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("9.1"))
                                                .withLongitude(new BigDecimal("71.2"))))));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);
        PublicationDeliveryStructure firstResponse = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(firstResponse);

        Quay quay = actualStopPlace.getQuays()
                .getQuayRefOrQuay()
                .stream()
                .peek(object -> System.out.println(object))
                .filter(object -> object instanceof Quay)
                .map(object -> ((Quay) object))
                .peek(q -> System.out.println(q))
                .findFirst().get();

        assertThat(actualStopPlace.getName().getValue()).isEqualTo("Steinerskolen Moss");
        assertThat(quay.getName().getValue()).isEqualTo("Steinerskolen");

    }

    @Test
    public void expectQuayNameToBeRemovedIfSameAsParentStopPlaceName() throws Exception {
        StopPlace stopPlace = new StopPlace()
                .withId("XYZ:stoparea:2")
                .withVersion("1")
                .withName(new MultilingualString().withValue("Fleskeby sentrum"))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("XYZ:boardingpos:2")
                                .withVersion("1")
                                .withName(new MultilingualString().withValue("Fleskeby sentrum"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("9.1"))
                                                .withLongitude(new BigDecimal("71.2"))))));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);
        PublicationDeliveryStructure firstResponse = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(firstResponse);

        Quay quay = actualStopPlace.getQuays()
                .getQuayRefOrQuay()
                .stream()
                .peek(object -> System.out.println(object))
                .filter(object -> object instanceof Quay)
                .map(object -> ((Quay) object))
                .peek(q -> System.out.println(q))
                .findFirst().get();

        assertThat(actualStopPlace.getName().getValue()).isEqualTo("Fleskeby sentrum");
        assertThat(quay.getName()).isNull();
    }

    @Test
    public void computeStopPlaceCentroid() throws Exception {

        StopPlace stopPlace = new StopPlace()
                .withId("XYZ:StopPlace:9")
                .withVersion("1")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("1"))
                                .withLongitude(new BigDecimal("2"))))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                        .withId("XYZ:Quay:9")
                                        .withVersion("1")
                                        .withName(new MultilingualString().withValue("quay number one"))
                                        .withCentroid(new SimplePoint_VersionStructure()
                                                .withId("12")
                                                .withVersion("1")
                                                .withLocation(new LocationStructure()
                                                        .withLatitude(new BigDecimal("10"))
                                                        .withLongitude(new BigDecimal("20")))),
                                new Quay()
                                        .withId("XYZ:Quay:133")
                                        .withVersion("1")
                                        .withName(new MultilingualString().withValue("quay number two"))
                                        .withCentroid(new SimplePoint_VersionStructure()
                                                .withId("30")
                                                .withVersion("1")
                                                .withLocation(new LocationStructure()
                                                        .withLatitude(new BigDecimal("10.0002"))
                                                        .withLongitude(new BigDecimal("20.0002"))))));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);

        PublicationDeliveryStructure firstResponse = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(firstResponse);

        assertThat(actualStopPlace.getCentroid().getLocation().getLongitude().doubleValue()).isEqualTo(20.0001);
        assertThat(actualStopPlace.getCentroid().getLocation().getLatitude().doubleValue()).isEqualTo(10.0001);
    }

    @Test
    public void maxNumberOfDigitsInCoordinatesShouldBeSix() throws Exception {

        final int maxdigits = 6;

        StopPlace stopPlace = new StopPlace()
                .withId("XYZ:StopPlace:91")
                .withVersion("1")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("10.123456789123456789123456789"))
                                .withLongitude(new BigDecimal("20.123456789123456789123456789"))))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withId("XYZ:Quay:91")
                                .withVersion("1")
                                .withName(new MultilingualString().withValue("quay number one"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withVersion("1")
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("10.123456789123456789123456789"))
                                                .withLongitude(new BigDecimal("20.123456789123456789123456789"))))));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);

        PublicationDeliveryStructure firstResponse = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(firstResponse);

        BigDecimal longitude = actualStopPlace.getCentroid().getLocation().getLongitude();
        BigDecimal latitude = actualStopPlace.getCentroid().getLocation().getLatitude();

        assertThat(String.valueOf(longitude).split("\\.")[1].length()).as("longitude decimals length").isLessThanOrEqualTo(maxdigits);
        assertThat(String.valueOf(latitude).split("\\.")[1].length()).as("latitude decimals length").isLessThanOrEqualTo(maxdigits);

    }

    @Test
    public void importPublicationDeliveryAndVerifyStatusCode200() throws Exception {

        String xml = """
                <?xml version="1.0" encoding="utf-8"?>
                <PublicationDelivery version="1.0" xmlns="http://www.netex.org.uk/netex"
                                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                                     xsi:schemaLocation="http://www.netex.org.uk/netex ../../xsd/NeTEx_publication.xsd">
                    <PublicationTimestamp>2016-05-18T15:00:00.0Z</PublicationTimestamp>
                    <ParticipantRef>NHR</ParticipantRef>
                    <dataObjects>
                        <SiteFrame version="01" id="nhr:sf:1">
                            <FrameDefaults>
                               <DefaultLocale>
                                   <TimeZone>Europe/Oslo</TimeZone>
                                   <DefaultLanguage>no</DefaultLanguage>
                               </DefaultLocale>
                            </FrameDefaults>
                            <stopPlaces>
                                <StopPlace version="01" created="2016-04-21T09:00:00.0Z" id="nhr:sp:1">
                                    <ValidBetween>
                                        <FromDate>2017-05-11T10:20:27.394+02:00</FromDate>
                                    </ValidBetween>\
                                    <Name lang="no-NO">Krokstien</Name>
                                    <Centroid>
                                        <Location srsName="WGS84">
                                            <Longitude>10.8577903</Longitude>
                                            <Latitude>59.910579</Latitude>
                                        </Location>
                                    </Centroid>
                                    <TransportMode>bus</TransportMode>
                                    <StopPlaceType>onstreetBus</StopPlaceType>
                                    <quays>
                                        <Quay version="01" created="2016-04-21T09:01:00.0Z" id="nhr:Quay:1">
                                            <Centroid>
                                                <Location srsName="WGS84">
                                                    <Longitude>10.8577903</Longitude>
                                                    <Latitude>59.910579</Latitude>
                                                </Location>
                                            </Centroid>
                                            <Covered>outdoors</Covered>
                                            <Lighting>wellLit</Lighting>
                                            <QuayType>busStop</QuayType>
                                        </Quay>
                                    </quays>
                                </StopPlace>
                            </stopPlaces>
                        </SiteFrame>
                    </dataObjects>
                </PublicationDelivery>""";


        InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));


        Response response = importResource.importPublicationDelivery(stream);

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    public void importStopPlaceWithMultipleValidBetweenPeriodsIgnoresAllButFirst() throws Exception {
        LocalDateTime firstValidFrom = now.plusSeconds(1);
        LocalDateTime secondValidFrom = now.plusSeconds(2);
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
        StopPlace stopPlace = changedStopPlaces.getFirst();

        List<ValidBetween> actualValidBetween = stopPlace.getValidBetween();

        assertThat(actualValidBetween)
                .as("Stop Place should have actualValidBetween set")
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        assertThat(actualValidBetween.getFirst().getFromDate()).isEqualTo(firstValidFrom);
    }

    /**
     * Partially copied from https://github.com/rutebanken/netex-norway-examples/blob/master/examples/stops/BasicStopPlace_example.xml
     */
    @Test
    public void importBasicStopPlace() throws JAXBException, IOException, SAXException {

        String xml = """
                <PublicationDelivery
                 version="any"
                 xmlns="http://www.netex.org.uk/netex"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://www.netex.org.uk/netex ../../xsd/NeTEx_publication.xsd">
                 <!-- Når denne dataleveransen ble generert -->
                 <PublicationTimestamp>2016-05-18T15:00:00.0Z</PublicationTimestamp>
                 <ParticipantRef>NHR</ParticipantRef>
                 <dataObjects>
                  <SiteFrame version="any" id="nhr:sf:1">
                   <FrameDefaults>
                     <DefaultLocale>
                       <TimeZone>Europe/Oslo</TimeZone>
                       <DefaultLanguage>no</DefaultLanguage>
                     </DefaultLocale>
                   </FrameDefaults>
                   <stopPlaces>
                    <!--===Stop=== -->
                    <!-- Merk: Holdeplass-ID vil komme fra Holdeplassregisteret -->
                    <StopPlace version="1" created="2016-04-21T09:00:00.0Z" id="nhr:sp:2">
                     <Name lang="no-NO">Krokstien</Name>
                    </StopPlace>
                   </stopPlaces>
                  </SiteFrame>
                 </dataObjects>
                </PublicationDelivery>

                """;

        InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));


        Response response = importResource.importPublicationDelivery(stream);
        assertThat(response.getStatus()).isEqualTo(200);

        StreamingOutput streamingOutput = (StreamingOutput) response.getEntity();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        streamingOutput.write(byteArrayOutputStream);
        System.out.println(byteArrayOutputStream.toString());
    }

    @Test
    public void importNSBStopPlace() throws JAXBException, IOException, SAXException {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <PublicationDelivery xmlns="http://www.netex.org.uk/netex">
                   <PublicationTimestamp>2017-04-18T12:57:27.796+02:00</PublicationTimestamp>
                   <ParticipantRef>NSB</ParticipantRef>
                   <Description>NSB Grails stasjoner til NeTex</Description>
                   <dataObjects>
                      <SiteFrame id="NSB:SiteFrame:1" version="1">
                         <codespaces>
                            <Codespace id="nsb">
                               <Xmlns>NSB</Xmlns>
                               <XmlnsUrl>http://www.rutebanken.org/ns/nsb</XmlnsUrl>
                            </Codespace>
                         </codespaces>
                         <FrameDefaults>
                           <DefaultLocale>
                               <TimeZone>Europe/Oslo</TimeZone>
                               <DefaultLanguage>no</DefaultLanguage>
                           </DefaultLocale>
                         </FrameDefaults>
                         <stopPlaces>
                  \s
                  \s
                            <StopPlace id="NSB:StopPlace:007602146" version="1">
                               <keyList>
                                  <KeyValue>
                                     <Key>grailsId</Key>
                                     <Value>3</Value>
                                  </KeyValue>
                                  <KeyValue>
                                     <Key>lisaId</Key>
                                     <Value>2146</Value>
                                  </KeyValue>
                                  <KeyValue>
                                     <Key>jbvCode</Key>
                                     <Value>ADL</Value>
                                  </KeyValue>
                                  <KeyValue>
                                     <Key>iffCode</Key>
                                     <Value>7602146</Value>
                                  </KeyValue>
                                  <KeyValue>
                                     <Key>uicCode</Key>
                                     <Value>7602146</Value>
                                  </KeyValue>
                                  <KeyValue>
                                     <Key>imported-id</Key>
                                     <Value>NRI:StopPlace:761037602</Value>
                                  </KeyValue>
                               </keyList>
                               <Name lang="no">Arendal</Name>
                               <Centroid>
                                  <Location srsName="WGS84"><!--Match on NRI quays--><Longitude>8.769146</Longitude>
                                     <Latitude>58.465256</Latitude>
                                  </Location>
                               </Centroid>
                               <Url>http://www.jernbaneverket.no/no/Jernbanen/Stasjonssok/-A-/Arendal/</Url>
                               <PostalAddress id="NSB:PostalAddress:3" version="1">
                                  <AddressLine1>Møllebakken 15</AddressLine1>
                                  <AddressLine2> 4841 Arendal</AddressLine2>
                               </PostalAddress>
                               <AccessibilityAssessment id="NSB:AccessibilityAssessment:3" version="1">
                                  <MobilityImpairedAccess>true</MobilityImpairedAccess>
                                  <limitations>
                                     <AccessibilityLimitation>
                                        <WheelchairAccess>true</WheelchairAccess>
                                        <StepFreeAccess>true</StepFreeAccess>
                                     </AccessibilityLimitation>
                                  </limitations>
                               </AccessibilityAssessment>
                               <placeEquipments>
                                  <WaitingRoomEquipment id="NSB:WaitingRoomEquipment:3" version="1"/>
                                  <SanitaryEquipment id="NSB:SanitaryEquipment:3" version="1">
                                     <Gender>both</Gender>
                                     <SanitaryFacilityList>toilet wheelChairAccessToilet</SanitaryFacilityList>
                                  </SanitaryEquipment>
                                  <TicketingEquipment id="NSB:TicketingEquipment:3" version="1">
                                     <NumberOfMachines>1</NumberOfMachines>
                                  </TicketingEquipment>
                               </placeEquipments>
                               <localServices>
                                  <LeftLuggageService id="NSB:LeftLuggageService:3" version="1">
                                     <SelfServiceLockers>true</SelfServiceLockers>
                                  </LeftLuggageService>
                                  <TicketingService id="NSB:TicketingService:3" version="1">
                                     <TicketCounterService>true</TicketCounterService>
                                  </TicketingService>
                               </localServices>
                               <StopPlaceType>railStation</StopPlaceType>
                               <Weighting>interchangeAllowed</Weighting>
                               <quays>
                                  <Quay id="NSB:Quay:0076021461" version="1">
                                     <keyList>
                                        <KeyValue>
                                           <Key>grails-platformId</Key>
                                           <Value>825930</Value>
                                        </KeyValue>
                                        <KeyValue>
                                           <Key>uicCode</Key>
                                           <Value>7602146</Value>
                                        </KeyValue>
                                     </keyList>
                                     <Centroid>
                                        <Location srsName="WGS84"><!--Match on NRI quays--><Longitude>8.769146</Longitude>
                                           <Latitude>58.465256</Latitude>
                                        </Location>
                                     </Centroid>
                                     <PublicCode>1</PublicCode>
                                  </Quay>
                               </quays>
                            </StopPlace>
                           </stopPlaces>\
                       </SiteFrame>\
                   </dataObjects>\
                </PublicationDelivery>""";


        InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));


        Response response = importResource.importPublicationDelivery(stream);
        assertThat(response.getStatus()).isEqualTo(200);

        StreamingOutput streamingOutput = (StreamingOutput) response.getEntity();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        streamingOutput.write(byteArrayOutputStream);
        System.out.println(byteArrayOutputStream.toString());
    }

    @Test
    public void importBrakarStopPlaceWithGeneralSignEquipment() throws JAXBException, IOException, SAXException {

        final FileInputStream fileInputStream = new FileInputStream("src/test/resources/org/rutebanken/tiamat/rest/netex/publicationdelivery/brakar.xml");

        Response response = importResource.importPublicationDelivery(fileInputStream);
        assertThat(response.getStatus()).isEqualTo(200);

        StreamingOutput streamingOutput = (StreamingOutput) response.getEntity();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        streamingOutput.write(byteArrayOutputStream);
        System.out.println(byteArrayOutputStream.toString());
    }


    @Test
    public void importParking() throws JAXBException, IOException, SAXException {
        final org.rutebanken.tiamat.model.StopPlace stopPlace = new org.rutebanken.tiamat.model.StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:408");
        stopPlace.setVersion(1);
        stopPlaceRepository.save(stopPlace);

        final FileInputStream fileInputStream = new FileInputStream("src/test/resources/org/rutebanken/tiamat/rest/netex/publicationdelivery/parking.xml");

        Response response = importResource.importPublicationDelivery(fileInputStream);
        assertThat(response.getStatus()).isEqualTo(200);

        StreamingOutput streamingOutput = (StreamingOutput) response.getEntity();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        streamingOutput.write(byteArrayOutputStream);
        System.out.println(byteArrayOutputStream.toString());
    }
    private void setUpSecurityContext() {
        // Create a Jwt with claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "testuser");
        claims.put("scope", "ROLE_USER");  // Or other relevant scopes/roles

        // Create a Jwt instance
        Jwt jwt = new Jwt(
                "tokenValue",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                claims
        );

        final AbstractAuthenticationToken authToken = new JwtAuthenticationToken(jwt, Collections.singleton(new SimpleGrantedAuthority("ROLE_EDIT_STOPS")));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}