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

package org.rutebanken.tiamat.netex.mapping;

import jakarta.xml.bind.JAXBElement;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.rutebanken.netex.model.AccessibilityLimitations_RelStructure;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.SiteRefs_RelStructure;
import org.rutebanken.netex.model.TopographicPlacesInFrame_RelStructure;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.AddressablePlaceRefStructure;
import org.rutebanken.tiamat.model.AlternativeName;
import org.rutebanken.tiamat.model.CountryRef;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.IanaCountryTldEnumeration;
import org.rutebanken.tiamat.model.EntranceEnumeration;
import org.rutebanken.tiamat.model.LightingEnumeration;
import org.rutebanken.tiamat.model.LimitationStatusEnumeration;
import org.rutebanken.tiamat.model.NameTypeEnumeration;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.ScopingMethodEnumeration;
import org.rutebanken.tiamat.model.SiteFrame;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopPlaceReference;
import org.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceRefStructure;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.model.Value;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

public class NetexMapperTest extends TiamatIntegrationTest {

    @Autowired
    private NetexMapper netexMapper;

    @Test
    public void mapKeyValuesToInternalList() throws Exception {


        org.rutebanken.netex.model.StopPlace stopPlace = new org.rutebanken.netex.model.StopPlace()
                .withKeyList(
                        new KeyListStructure()
                                .withKeyValue(
                                        new KeyValueStructure()
                                                .withKey(ORIGINAL_ID_KEY)
                                                .withValue("KOL:StopPlace:123,BRA:StopPlace:123,RUT:StopPlace:123")
                                )
                );

        StopPlace tiamatStopPlace = netexMapper.mapToTiamatModel(stopPlace);


        assertThat(tiamatStopPlace.getOriginalIds())
                .hasSize(3);

    }


    @Test
    public void mapSiteFrameToNetexModel() throws Exception {
        org.rutebanken.tiamat.model.SiteFrame sourceSiteFrame = new org.rutebanken.tiamat.model.SiteFrame();

        StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure = new StopPlacesInFrame_RelStructure();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("name", "en"));

        stopPlacesInFrame_relStructure.getStopPlace().add(stopPlace);

        sourceSiteFrame.setStopPlaces(stopPlacesInFrame_relStructure);

        org.rutebanken.netex.model.SiteFrame netexSiteFrame = netexMapper.mapToNetexModel(sourceSiteFrame);

        assertThat(netexSiteFrame).isNotNull();
        assertThat(netexSiteFrame.getStopPlaces().getStopPlace_().getFirst().getValue().getName().getValue()).isEqualTo(stopPlace.getName().getValue());
    }

    @Test
    public void mapSiteFrameToInternalModel() throws Exception {
        org.rutebanken.netex.model.SiteFrame netexSiteFrame = new org.rutebanken.netex.model.SiteFrame();

        org.rutebanken.netex.model.StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure = new org.rutebanken.netex.model.StopPlacesInFrame_RelStructure();

        org.rutebanken.netex.model.StopPlace stopPlace = new org.rutebanken.netex.model.StopPlace();
        stopPlace.setName(new org.rutebanken.netex.model.MultilingualString()
                .withValue("stop place")
                .withLang("no"));

        String stopPlaceId = "1337";
        stopPlace.setId("AVI:StopPlace:" + stopPlaceId);

        stopPlacesInFrame_relStructure.getStopPlace_().add(new ObjectFactory().createStopPlace(stopPlace));
        netexSiteFrame.setStopPlaces(stopPlacesInFrame_relStructure);

        org.rutebanken.tiamat.model.SiteFrame actualSiteFrame = netexMapper.mapToTiamatModel(netexSiteFrame);

        assertThat(actualSiteFrame).isNotNull();
        assertThat(actualSiteFrame.getStopPlaces().getStopPlace().getFirst().getName().getValue()).isEqualTo(stopPlace.getName().getValue());
    }

    @Test
    public void mapGroupOfStopPlacesToNetex() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:1");
        stopPlace.setName(new EmbeddableMultilingualString("stopPlace"));

        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces();
        groupOfStopPlaces.setNetexId("NSR:GroupOfStopPlaces:1");
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));
        groupOfStopPlaces.setChangedBy("Solem");
        groupOfStopPlaces.setVersion(2L);
        groupOfStopPlaces.setCreated(Instant.now());
        groupOfStopPlaces.setChanged(Instant.now());
        groupOfStopPlaces.setName(new EmbeddableMultilingualString("oh my gosp"));
        groupOfStopPlaces.setCentroid(geometryFactory.createPoint(new Coordinate(16,17)));

        AlternativeName alternativeName = new AlternativeName();
        alternativeName.setName(new EmbeddableMultilingualString("alternative name alias"));
        alternativeName.setNameType(NameTypeEnumeration.ALIAS);
        groupOfStopPlaces.getAlternativeNames().add(alternativeName);




        org.rutebanken.netex.model.GroupOfStopPlaces netexGroupOfStopPlaces = netexMapper.getFacade().map(groupOfStopPlaces, org.rutebanken.netex.model.GroupOfStopPlaces.class);

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
                    .containsOnly(stopPlace.getNetexId());

        assertThat(netexGroupOfStopPlaces.getChanged()).as("changed").isNotNull();
        assertThat(netexGroupOfStopPlaces.getVersion()).as("version").isEqualTo(String.valueOf(groupOfStopPlaces.getVersion()));
    }


    @Test
    public void mapStopPlaceWithNameAndTopographicPlaceRefToNetex() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("stopplacenetexid");
        stopPlace.setName(new EmbeddableMultilingualString("name", "en"));
        stopPlace.setVersion(1L);

        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("Asker"));
        topographicPlace.setVersion(1L);
        topographicPlace.setNetexId("netexidfortopoplace");
        stopPlace.setTopographicPlace(topographicPlace);

        org.rutebanken.netex.model.StopPlace netexStopPlace = netexMapper.mapToNetexModel(stopPlace);

        assertThat(netexStopPlace).isNotNull();
        assertThat(netexStopPlace.getName()).isNotNull();
        assertThat(netexStopPlace.getName().getValue()).isEqualTo(stopPlace.getName().getValue());
        assertThat(netexStopPlace.getTopographicPlaceRef()).isNotNull();
        assertThat(netexStopPlace.getTopographicPlaceRef().getRef()).isEqualTo(topographicPlace.getNetexId());
        assertThat(netexStopPlace.getTopographicPlaceRef().getVersion()).isEqualTo(String.valueOf(topographicPlace.getVersion()));
    }

    @Test
    public void mapStopPlaceToInternalWithId() throws Exception {
        org.rutebanken.netex.model.StopPlace netexStopPlace = new org.rutebanken.netex.model.StopPlace();
        netexStopPlace.setId("NSR:StopPlace:1339");

        org.rutebanken.tiamat.model.StopPlace tiamatStopPlace = netexMapper.mapToTiamatModel(netexStopPlace);

        assertThat(tiamatStopPlace).isNotNull();
        assertThat(tiamatStopPlace.getNetexId()).isEqualTo(netexStopPlace.getId());
    }

    @Test
    public void mapStopPlaceWithKeyValuesToNetex() throws Exception {

        StopPlace stopPlace = new StopPlace();

        String originalId = "OPP:StopArea:123";


        stopPlace.getKeyValues().put(ORIGINAL_ID_KEY, new Value(originalId));

        org.rutebanken.netex.model.StopPlace netexStopPlace = netexMapper.mapToNetexModel(stopPlace);
        assertThat(netexStopPlace.getKeyList()).isNotNull();
        assertThat(netexStopPlace.getKeyList().getKeyValue()).isNotNull();
        assertThat(netexStopPlace.getKeyList().getKeyValue()).isNotEmpty();
        assertThat(netexStopPlace.getKeyList().getKeyValue()).extracting("key").contains(ORIGINAL_ID_KEY);
        assertThat(netexStopPlace.getKeyList().getKeyValue()).extracting("value").contains(originalId);
    }

    /**
     * Usually, a stop place's ID field will be moved to key value.
     * But when the stop place already has key values, we should map them to tiamat's keyValues.
     */
    @Test
    public void mapStopPlaceWithKeyValuesToTiamat() throws Exception {
        String originalId = "OPP:StopArea:123";
        org.rutebanken.netex.model.StopPlace netexStopPlace = new org.rutebanken.netex.model.StopPlace()
                .withKeyList(
                        new KeyListStructure().withKeyValue(
                                new KeyValueStructure()
                                        .withKey(ORIGINAL_ID_KEY)
                                        .withValue(originalId)));

        StopPlace tiamatStopPlace = netexMapper.mapToTiamatModel(netexStopPlace);
        assertThat(tiamatStopPlace.getKeyValues()).isNotNull();
        assertThat(tiamatStopPlace.getKeyValues()).containsKey(ORIGINAL_ID_KEY);
        assertThat(tiamatStopPlace.getKeyValues().get(ORIGINAL_ID_KEY).getItems().contains(originalId));
    }

    @Test
    public void mapStopPlaceToInternalWithName() throws Exception {
        org.rutebanken.netex.model.StopPlace netexStopPlace = new org.rutebanken.netex.model.StopPlace();
        org.rutebanken.netex.model.MultilingualString name = new org.rutebanken.netex.model.MultilingualString();
        name.setValue("stop place ");
        name.setLang("no");
        name.setTextIdType("");
        netexStopPlace.setName(name);

        org.rutebanken.tiamat.model.StopPlace tiamatStopPlace = netexMapper.mapToTiamatModel(netexStopPlace);

        assertThat(tiamatStopPlace).isNotNull();
        assertThat(tiamatStopPlace.getName().getValue()).isEqualTo(netexStopPlace.getName().getValue());

    }

    @Test
    public void mapStopPlaceInternalIdToNetexId() {
        StopPlace tiamatStopPlace = new StopPlace();
        tiamatStopPlace.setNetexId("NSR:StopPlace:123456");

        org.rutebanken.netex.model.StopPlace netexStopPlace = netexMapper.mapToNetexModel(tiamatStopPlace);

        assertThat(netexStopPlace.getId()).isEqualTo("NSR:StopPlace:123456");
    }

    @Test
    public void mapNetexQuayIdToInternal() {
        org.rutebanken.netex.model.Quay netexQuay = new org.rutebanken.netex.model.Quay();

        String netexId = "NSR:Quay:12345";
        netexQuay.setId(netexId);
        netexQuay.setCompassBearing(312f);
        netexQuay.setPublicCode("B");
        netexQuay.setLighting(org.rutebanken.netex.model.LightingEnumeration.UNLIT);

        org.rutebanken.tiamat.model.Quay tiamatQuay = netexMapper.mapToTiamatModel(netexQuay);

        assertThat(tiamatQuay.getNetexId()).isEqualTo("NSR:Quay:12345");
        assertThat(tiamatQuay.getCompassBearing()).isEqualTo(312f);
        assertThat(tiamatQuay.getPublicCode()).isEqualTo("B");
        assertThat(tiamatQuay.getLighting()).isEqualTo(LightingEnumeration.UNLIT);
    }

    @Test
    public void mapInternalQuayIdToNetex() {

        org.rutebanken.tiamat.model.Quay tiamatQuay = new org.rutebanken.tiamat.model.Quay();
        String netexId = "NSR:Quay:" + 1234567;
        tiamatQuay.setNetexId(netexId);

        org.rutebanken.netex.model.Quay netexQuay = netexMapper.mapToNetexModel(tiamatQuay);
        assertThat(netexQuay.getId()).isNotNull();
        assertThat(netexQuay.getId()).isEqualTo(netexId);
    }

    @Test
    public void mapNetexParkingPaymentMethodsToInternal() {
        org.rutebanken.netex.model.Parking netexParking = new org.rutebanken.netex.model.Parking();
        netexParking.setId("NSR:Parking:1");
        netexParking.getPaymentMethods().add(org.rutebanken.netex.model.PaymentMethodEnumeration.CASH);
        netexParking.getPaymentMethods().add(org.rutebanken.netex.model.PaymentMethodEnumeration.CREDIT_CARD);

        org.rutebanken.tiamat.model.Parking tiamatParking = netexMapper.mapToTiamatModel(netexParking);

        assertThat(tiamatParking.getPaymentMethods()).containsExactlyInAnyOrder(
                org.rutebanken.tiamat.model.PaymentMethodEnumeration.CASH,
                org.rutebanken.tiamat.model.PaymentMethodEnumeration.CREDIT_CARD);
    }

    @Test
    public void mapInternalParkingPaymentMethodsToNetex() {
        org.rutebanken.tiamat.model.Parking tiamatParking = new org.rutebanken.tiamat.model.Parking();
        tiamatParking.setNetexId("NSR:Parking:1");
        tiamatParking.getPaymentMethods().add(org.rutebanken.tiamat.model.PaymentMethodEnumeration.MOBILE_PHONE);

        org.rutebanken.netex.model.Parking netexParking = netexMapper.mapToNetexModel(tiamatParking);

        assertThat(netexParking.getPaymentMethods()).containsExactly(org.rutebanken.netex.model.PaymentMethodEnumeration.MOBILE_PHONE);
    }

    @Test
    public void mapNetexParkingLightingToInternal() {
        org.rutebanken.netex.model.Parking netexParking = new org.rutebanken.netex.model.Parking();
        netexParking.setId("NSR:Parking:1");
        netexParking.setLighting(org.rutebanken.netex.model.LightingEnumeration.WELL_LIT);

        org.rutebanken.tiamat.model.Parking tiamatParking = netexMapper.mapToTiamatModel(netexParking);

        assertThat(tiamatParking.getLighting()).isEqualTo(LightingEnumeration.WELL_LIT);
    }

    @Test
    public void mapInternalParkingLightingToNetex() {
        org.rutebanken.tiamat.model.Parking tiamatParking = new org.rutebanken.tiamat.model.Parking();
        tiamatParking.setNetexId("NSR:Parking:1");
        tiamatParking.setLighting(LightingEnumeration.UNLIT);

        org.rutebanken.netex.model.Parking netexParking = netexMapper.mapToNetexModel(tiamatParking);

        assertThat(netexParking.getLighting()).isEqualTo(org.rutebanken.netex.model.LightingEnumeration.UNLIT);
    }

    @Test
    public void mapNetexParkingVehicleEntrancesToInternal() {
        org.rutebanken.netex.model.ParkingEntranceForVehicles netexEntrance = new org.rutebanken.netex.model.ParkingEntranceForVehicles();
        netexEntrance.setId("NSR:ParkingEntranceForVehicles:1");
        netexEntrance.setVersion("1");
        netexEntrance.setLabel(new org.rutebanken.netex.model.MultilingualString().withValue("Main gate"));
        netexEntrance.setEntranceType(org.rutebanken.netex.model.EntranceEnumeration.GATE);
        netexEntrance.setWidth(new java.math.BigDecimal("2.50"));
        netexEntrance.setHeight(new java.math.BigDecimal("2.10"));
        netexEntrance.getAccessModes().add(org.rutebanken.netex.model.AccessModeEnumeration.FOOT);
        netexEntrance.getAccessModes().add(org.rutebanken.netex.model.AccessModeEnumeration.BICYCLE);

        org.rutebanken.netex.model.ParkingEntrancesForVehicles_RelStructure rel = new org.rutebanken.netex.model.ParkingEntrancesForVehicles_RelStructure();
        rel.getParkingEntranceForVehiclesRefOrParkingEntranceForVehicles().add(netexEntrance);

        org.rutebanken.netex.model.Parking netexParking = new org.rutebanken.netex.model.Parking();
        netexParking.setId("NSR:Parking:1");
        netexParking.setVersion("1");
        netexParking.setVehicleEntrances(rel);

        org.rutebanken.tiamat.model.Parking tiamatParking = netexMapper.mapToTiamatModel(netexParking);

        assertThat(tiamatParking.getVehicleEntrances()).hasSize(1);
        org.rutebanken.tiamat.model.ParkingEntranceForVehicles tiamatEntrance = tiamatParking.getVehicleEntrances().get(0);
        assertThat(tiamatEntrance.getNetexId()).isEqualTo("NSR:ParkingEntranceForVehicles:1");
        assertThat(tiamatEntrance.getLabel().getValue()).isEqualTo("Main gate");
        assertThat(tiamatEntrance.getEntranceType()).isEqualTo(EntranceEnumeration.GATE);
        assertThat(tiamatEntrance.getWidth()).isEqualByComparingTo("2.50");
        assertThat(tiamatEntrance.getHeight()).isEqualByComparingTo("2.10");
        assertThat(tiamatEntrance.getAccessModesList()).containsExactly(
                org.rutebanken.tiamat.model.AccessModeEnumeration.FOOT,
                org.rutebanken.tiamat.model.AccessModeEnumeration.BICYCLE);
    }

    @Test
    public void mapInternalParkingVehicleEntrancesToNetex() {
        org.rutebanken.tiamat.model.ParkingEntranceForVehicles tiamatEntrance = new org.rutebanken.tiamat.model.ParkingEntranceForVehicles();
        tiamatEntrance.setNetexId("NSR:ParkingEntranceForVehicles:1");
        tiamatEntrance.setLabel(new org.rutebanken.tiamat.model.EmbeddableMultilingualString("Main gate"));
        tiamatEntrance.setEntranceType(EntranceEnumeration.GATE);
        tiamatEntrance.setWidth(new java.math.BigDecimal("2.50"));
        tiamatEntrance.setHeight(new java.math.BigDecimal("2.10"));
        tiamatEntrance.setAccessModesList(List.of(
                org.rutebanken.tiamat.model.AccessModeEnumeration.FOOT,
                org.rutebanken.tiamat.model.AccessModeEnumeration.BICYCLE));

        org.rutebanken.tiamat.model.Parking tiamatParking = new org.rutebanken.tiamat.model.Parking();
        tiamatParking.setNetexId("NSR:Parking:1");
        tiamatParking.getVehicleEntrances().add(tiamatEntrance);

        org.rutebanken.netex.model.Parking netexParking = netexMapper.mapToNetexModel(tiamatParking);

        assertThat(netexParking.getVehicleEntrances()).isNotNull();
        List<Object> netexEntrances = netexParking.getVehicleEntrances().getParkingEntranceForVehiclesRefOrParkingEntranceForVehicles();
        assertThat(netexEntrances).hasSize(1);
        org.rutebanken.netex.model.ParkingEntranceForVehicles netexEntrance = (org.rutebanken.netex.model.ParkingEntranceForVehicles) netexEntrances.get(0);
        assertThat(netexEntrance.getId()).isEqualTo("NSR:ParkingEntranceForVehicles:1");
        assertThat(netexEntrance.getLabel().getValue()).isEqualTo("Main gate");
        assertThat(netexEntrance.getEntranceType()).isEqualTo(org.rutebanken.netex.model.EntranceEnumeration.GATE);
        assertThat(netexEntrance.getWidth()).isEqualByComparingTo("2.50");
        assertThat(netexEntrance.getHeight()).isEqualByComparingTo("2.10");
        assertThat(netexEntrance.getAccessModes()).containsExactly(
                org.rutebanken.netex.model.AccessModeEnumeration.FOOT,
                org.rutebanken.netex.model.AccessModeEnumeration.BICYCLE);
    }

    @Test
    public void mapNetexParkingInfoLinksToInternal() {
        org.rutebanken.netex.model.InfoLinkStructure netexLink = new org.rutebanken.netex.model.InfoLinkStructure();
        netexLink.setValue("https://example.org/parking-info");
        netexLink.getTypeOfInfoLink().add(org.rutebanken.netex.model.TypeOfInfolinkEnumeration.INFO);

        org.rutebanken.netex.model.GroupOfEntities_VersionStructure.InfoLinks infoLinks =
                new org.rutebanken.netex.model.GroupOfEntities_VersionStructure.InfoLinks();
        infoLinks.getInfoLink().add(netexLink);

        org.rutebanken.netex.model.Parking netexParking = new org.rutebanken.netex.model.Parking();
        netexParking.setId("NSR:Parking:1");
        netexParking.setVersion("1");
        netexParking.setInfoLinks(infoLinks);

        org.rutebanken.tiamat.model.Parking tiamatParking = netexMapper.mapToTiamatModel(netexParking);

        assertThat(tiamatParking.getInfoLinks()).hasSize(1);
        org.rutebanken.tiamat.model.InfoLink tiamatLink = tiamatParking.getInfoLinks().get(0);
        assertThat(tiamatLink.getUri()).isEqualTo("https://example.org/parking-info");
        assertThat(tiamatLink.getTypeOfInfoLink()).isEqualTo(org.rutebanken.tiamat.model.TypeOfInfolinkEnumeration.INFO);
    }

    @Test
    public void mapInternalParkingInfoLinksToNetex() {
        org.rutebanken.tiamat.model.InfoLink tiamatLink = new org.rutebanken.tiamat.model.InfoLink(
                "https://example.org/parking-info", org.rutebanken.tiamat.model.TypeOfInfolinkEnumeration.INFO);

        org.rutebanken.tiamat.model.Parking tiamatParking = new org.rutebanken.tiamat.model.Parking();
        tiamatParking.setNetexId("NSR:Parking:1");
        tiamatParking.setInfoLinks(List.of(tiamatLink));

        org.rutebanken.netex.model.Parking netexParking = netexMapper.mapToNetexModel(tiamatParking);

        assertThat(netexParking.getInfoLinks()).isNotNull();
        List<org.rutebanken.netex.model.InfoLinkStructure> netexLinks = netexParking.getInfoLinks().getInfoLink();
        assertThat(netexLinks).hasSize(1);
        org.rutebanken.netex.model.InfoLinkStructure netexLink = netexLinks.get(0);
        assertThat(netexLink.getValue()).isEqualTo("https://example.org/parking-info");
        assertThat(netexLink.getTypeOfInfoLink()).containsExactly(org.rutebanken.netex.model.TypeOfInfolinkEnumeration.INFO);
    }

    @Test
    public void mapNetexParkingAvailabilityConditionsToInternal() {
        org.rutebanken.netex.model.AvailabilityCondition netexCondition = new org.rutebanken.netex.model.AvailabilityCondition()
                .withId("NSR:Parking:1:AvailabilityCondition:1")
                .withVersion("1")
                .withIsAvailable(true);

        org.rutebanken.netex.model.DayTypeRefStructure dayTypeRef =
                new org.rutebanken.netex.model.DayTypeRefStructure().withRef("NSR:DayType:1");
        org.rutebanken.netex.model.DayTypes_RelStructure dayTypes = new org.rutebanken.netex.model.DayTypes_RelStructure();
        dayTypes.getDayTypeRefOrDayType_().add(new ObjectFactory().createDayTypeRef(dayTypeRef));
        netexCondition.withDayTypes(dayTypes);

        org.rutebanken.netex.model.Timeband_VersionedChildStructure timeband =
                new org.rutebanken.netex.model.Timeband_VersionedChildStructure()
                        .withId("NSR:Parking:1:Timeband:1")
                        .withVersion("1")
                        .withStartTime(java.time.LocalTime.of(6, 0))
                        .withEndTime(java.time.LocalTime.of(22, 0));
        org.rutebanken.netex.model.Timebands_RelStructure timebands = new org.rutebanken.netex.model.Timebands_RelStructure();
        timebands.getTimebandRefOrTimeband().add(timeband);
        netexCondition.withTimebands(timebands);

        org.rutebanken.netex.model.ValidityConditions_RelStructure validityConditions =
                new org.rutebanken.netex.model.ValidityConditions_RelStructure();
        validityConditions.getValidityConditionRefOrValidBetweenOrValidityCondition_()
                .add(new ObjectFactory().createAvailabilityCondition(netexCondition));

        org.rutebanken.netex.model.Parking netexParking = new org.rutebanken.netex.model.Parking();
        netexParking.setId("NSR:Parking:1");
        netexParking.setVersion("1");
        netexParking.setValidityConditions(validityConditions);

        org.rutebanken.tiamat.model.Parking tiamatParking = netexMapper.mapToTiamatModel(netexParking);

        assertThat(tiamatParking.getAvailabilityConditions()).hasSize(1);
        org.rutebanken.tiamat.model.AvailabilityCondition condition = tiamatParking.getAvailabilityConditions().get(0);
        assertThat(condition.getDayTypeRef()).isEqualTo("NSR:DayType:1");
        assertThat(condition.isAvailable()).isTrue();
        assertThat(condition.getStartTime()).isEqualTo(java.time.LocalTime.of(6, 0));
        assertThat(condition.getEndTime()).isEqualTo(java.time.LocalTime.of(22, 0));
    }

    @Test
    public void mapNetexParkingAvailabilityConditionsWithSameDayTypeRefAndDifferentContentKeepsBoth() {
        org.rutebanken.netex.model.ValidityConditions_RelStructure validityConditions =
                new org.rutebanken.netex.model.ValidityConditions_RelStructure();
        ObjectFactory objectFactory = new ObjectFactory();
        for (boolean available : new boolean[] {true, false}) {
            org.rutebanken.netex.model.AvailabilityCondition netexCondition = new org.rutebanken.netex.model.AvailabilityCondition()
                    .withId("NSR:Parking:1:AvailabilityCondition:" + available)
                    .withVersion("1")
                    .withIsAvailable(available);
            org.rutebanken.netex.model.DayTypeRefStructure dayTypeRef =
                    new org.rutebanken.netex.model.DayTypeRefStructure().withRef("NSR:DayType:1");
            org.rutebanken.netex.model.DayTypes_RelStructure dayTypes = new org.rutebanken.netex.model.DayTypes_RelStructure();
            dayTypes.getDayTypeRefOrDayType_().add(objectFactory.createDayTypeRef(dayTypeRef));
            netexCondition.withDayTypes(dayTypes);
            validityConditions.getValidityConditionRefOrValidBetweenOrValidityCondition_()
                    .add(objectFactory.createAvailabilityCondition(netexCondition));
        }

        org.rutebanken.netex.model.Parking netexParking = new org.rutebanken.netex.model.Parking();
        netexParking.setId("NSR:Parking:1");
        netexParking.setVersion("1");
        netexParking.setValidityConditions(validityConditions);

        org.rutebanken.tiamat.model.Parking tiamatParking = netexMapper.mapToTiamatModel(netexParking);

        assertThat(tiamatParking.getAvailabilityConditions())
                .as("conditions differing in content must not be collapsed by dayTypeRef")
                .hasSize(2)
                .extracting(org.rutebanken.tiamat.model.AvailabilityCondition::isAvailable)
                .containsExactly(true, false);
    }

    @Test
    public void mapNetexParkingIdenticalAvailabilityConditionsCollapseToOne() {
        org.rutebanken.netex.model.ValidityConditions_RelStructure validityConditions =
                new org.rutebanken.netex.model.ValidityConditions_RelStructure();
        ObjectFactory objectFactory = new ObjectFactory();
        for (int i = 0; i < 2; i++) {
            validityConditions.getValidityConditionRefOrValidBetweenOrValidityCondition_()
                    .add(objectFactory.createAvailabilityCondition(netexAvailabilityCondition(
                            "NSR:Parking:1:AvailabilityCondition:" + i, true,
                            List.of("NSR:DayType:1"),
                            List.of(netexTimeband("NSR:Parking:1:Timeband:" + i,
                                    java.time.LocalTime.of(6, 0), java.time.LocalTime.of(22, 0), null)))));
        }

        org.rutebanken.netex.model.Parking netexParking = new org.rutebanken.netex.model.Parking();
        netexParking.setId("NSR:Parking:1");
        netexParking.setVersion("1");
        netexParking.setValidityConditions(validityConditions);

        assertThat(netexMapper.mapToTiamatModel(netexParking).getAvailabilityConditions())
                .as("re-importing the same opening hours must be idempotent")
                .hasSize(1);
    }

    @Test
    public void mapNetexParkingAvailabilityConditionWithSeveralDayTypeRefsFansOut() {
        org.rutebanken.netex.model.Parking netexParking = netexParkingWithConditions(netexAvailabilityCondition(
                "NSR:Parking:1:AvailabilityCondition:1", true,
                List.of("NSR:DayType:1", "NSR:DayType:2", "NSR:DayType:3"),
                List.of(netexTimeband("NSR:Parking:1:Timeband:1",
                        java.time.LocalTime.of(6, 0), java.time.LocalTime.of(22, 0), null))));

        assertThat(netexMapper.mapToTiamatModel(netexParking).getAvailabilityConditions())
                .as("every DayTypeRef must survive, not only the first")
                .hasSize(3)
                .allSatisfy(condition -> {
                    assertThat(condition.getStartTime()).isEqualTo(java.time.LocalTime.of(6, 0));
                    assertThat(condition.getEndTime()).isEqualTo(java.time.LocalTime.of(22, 0));
                })
                .extracting(org.rutebanken.tiamat.model.AvailabilityCondition::getDayTypeRef)
                .containsExactly("NSR:DayType:1", "NSR:DayType:2", "NSR:DayType:3");
    }

    @Test
    public void mapNetexParkingAvailabilityConditionWithSplitTimebandsFansOut() {
        org.rutebanken.netex.model.Parking netexParking = netexParkingWithConditions(netexAvailabilityCondition(
                "NSR:Parking:1:AvailabilityCondition:1", true,
                List.of("NSR:DayType:1"),
                List.of(netexTimeband("NSR:Parking:1:Timeband:1",
                                java.time.LocalTime.of(6, 0), java.time.LocalTime.of(10, 0), null),
                        netexTimeband("NSR:Parking:1:Timeband:2",
                                java.time.LocalTime.of(15, 0), java.time.LocalTime.of(20, 0), null))));

        assertThat(netexMapper.mapToTiamatModel(netexParking).getAvailabilityConditions())
                .as("split opening hours for one day must both survive")
                .hasSize(2)
                .extracting(org.rutebanken.tiamat.model.AvailabilityCondition::getStartTime,
                        org.rutebanken.tiamat.model.AvailabilityCondition::getEndTime)
                .containsExactly(
                        tuple(java.time.LocalTime.of(6, 0), java.time.LocalTime.of(10, 0)),
                        tuple(java.time.LocalTime.of(15, 0), java.time.LocalTime.of(20, 0)));
    }

    @Test
    public void mapNetexParkingAvailabilityConditionWithSeveralDayTypesAndTimebandsFansOutEveryCombination() {
        org.rutebanken.netex.model.Parking netexParking = netexParkingWithConditions(netexAvailabilityCondition(
                "NSR:Parking:1:AvailabilityCondition:1", true,
                List.of("NSR:DayType:1", "NSR:DayType:2"),
                List.of(netexTimeband("NSR:Parking:1:Timeband:1",
                                java.time.LocalTime.of(6, 0), java.time.LocalTime.of(10, 0), null),
                        netexTimeband("NSR:Parking:1:Timeband:2",
                                java.time.LocalTime.of(15, 0), java.time.LocalTime.of(20, 0), null))));

        assertThat(netexMapper.mapToTiamatModel(netexParking).getAvailabilityConditions())
                .hasSize(4)
                .extracting(org.rutebanken.tiamat.model.AvailabilityCondition::getDayTypeRef,
                        org.rutebanken.tiamat.model.AvailabilityCondition::getStartTime)
                .containsExactly(
                        tuple("NSR:DayType:1", java.time.LocalTime.of(6, 0)),
                        tuple("NSR:DayType:1", java.time.LocalTime.of(15, 0)),
                        tuple("NSR:DayType:2", java.time.LocalTime.of(6, 0)),
                        tuple("NSR:DayType:2", java.time.LocalTime.of(15, 0)));
    }

    @Test
    public void mapParkingAvailabilityConditionDayOffsetRoundTripsEndOfDay() {
        org.rutebanken.netex.model.Parking netexParking = netexParkingWithConditions(netexAvailabilityCondition(
                "NSR:Parking:1:AvailabilityCondition:1", true,
                List.of("NSR:DayType:1"),
                List.of(netexTimeband("NSR:Parking:1:Timeband:1",
                        java.time.LocalTime.of(6, 0), java.time.LocalTime.MIDNIGHT, 1))));

        org.rutebanken.tiamat.model.Parking tiamatParking = netexMapper.mapToTiamatModel(netexParking);
        assertThat(tiamatParking.getAvailabilityConditions()).hasSize(1);
        org.rutebanken.tiamat.model.AvailabilityCondition condition = tiamatParking.getAvailabilityConditions().get(0);
        assertThat(condition.getEndTime()).isEqualTo(java.time.LocalTime.MIDNIGHT);
        assertThat(condition.getDayOffset())
                .as("end-of-day must stay distinguishable from start-of-day midnight")
                .isEqualTo(1);

        org.rutebanken.netex.model.Timeband_VersionedChildStructure exported =
                singleExportedTimeband(netexMapper.mapToNetexModel(tiamatParking));
        assertThat(exported.getEndTime()).isEqualTo(java.time.LocalTime.MIDNIGHT);
        assertThat(exported.getDayOffset()).isEqualTo(java.math.BigInteger.ONE);
    }

    @Test
    public void mapParkingAvailabilityConditionWithoutDayOffsetKeepsStartOfDayMidnightDistinct() {
        org.rutebanken.tiamat.model.Parking tiamatParking = new org.rutebanken.tiamat.model.Parking();
        tiamatParking.setNetexId("NSR:Parking:1");
        tiamatParking.setAvailabilityConditions(List.of(new org.rutebanken.tiamat.model.AvailabilityCondition(
                "NSR:DayType:1", true, java.time.LocalTime.of(6, 0), java.time.LocalTime.MIDNIGHT, 0)));

        org.rutebanken.netex.model.Timeband_VersionedChildStructure exported =
                singleExportedTimeband(netexMapper.mapToNetexModel(tiamatParking));
        assertThat(exported.getEndTime()).isEqualTo(java.time.LocalTime.MIDNIGHT);
        assertThat(exported.getDayOffset())
                .as("a zero dayOffset must not be emitted, keeping it distinct from end-of-day")
                .isNull();
    }

    @Test
    public void mapNetexParkingAvailabilityConditionWithEndTimeButNoStartTimeIsRejected() {
        org.rutebanken.netex.model.Parking netexParking = netexParkingWithConditions(netexAvailabilityCondition(
                "NSR:Parking:1:AvailabilityCondition:1", true,
                List.of("NSR:DayType:1"),
                List.of(netexTimeband("NSR:Parking:1:Timeband:1", null, java.time.LocalTime.of(22, 0), null))));

        assertThatThrownBy(() -> netexMapper.mapToTiamatModel(netexParking))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("EndTime")
                .hasMessageContaining("no StartTime");
    }

    @Test
    public void mapInternalParkingAvailabilityConditionWithoutStartTimeOmitsTimebandOnExport() {
        org.rutebanken.tiamat.model.Parking tiamatParking = new org.rutebanken.tiamat.model.Parking();
        tiamatParking.setNetexId("NSR:Parking:1");
        tiamatParking.setAvailabilityConditions(List.of(new org.rutebanken.tiamat.model.AvailabilityCondition(
                "NSR:DayType:1", true, null, java.time.LocalTime.of(22, 0))));

        org.rutebanken.netex.model.Parking netexParking = netexMapper.mapToNetexModel(tiamatParking);
        List<org.rutebanken.netex.model.AvailabilityCondition> conditions = exportedAvailabilityConditions(netexParking);
        assertThat(conditions).hasSize(1);
        assertThat(conditions.get(0).getTimebands())
                .as("NeTEx requires Timeband/StartTime, so no timeband may be emitted without one")
                .isNull();
    }

    private org.rutebanken.netex.model.Timeband_VersionedChildStructure netexTimeband(
            String id, java.time.LocalTime startTime, java.time.LocalTime endTime, Integer dayOffset) {
        org.rutebanken.netex.model.Timeband_VersionedChildStructure timeband =
                new org.rutebanken.netex.model.Timeband_VersionedChildStructure()
                        .withId(id)
                        .withVersion("1")
                        .withStartTime(startTime)
                        .withEndTime(endTime);
        if (dayOffset != null) {
            timeband.setDayOffset(java.math.BigInteger.valueOf(dayOffset));
        }
        return timeband;
    }

    private org.rutebanken.netex.model.AvailabilityCondition netexAvailabilityCondition(
            String id, Boolean isAvailable, List<String> dayTypeRefs,
            List<org.rutebanken.netex.model.Timeband_VersionedChildStructure> timebands) {
        ObjectFactory objectFactory = new ObjectFactory();
        org.rutebanken.netex.model.AvailabilityCondition condition =
                new org.rutebanken.netex.model.AvailabilityCondition()
                        .withId(id)
                        .withVersion("1")
                        .withIsAvailable(isAvailable);

        org.rutebanken.netex.model.DayTypes_RelStructure dayTypes = new org.rutebanken.netex.model.DayTypes_RelStructure();
        for (String dayTypeRef : dayTypeRefs) {
            dayTypes.getDayTypeRefOrDayType_().add(objectFactory.createDayTypeRef(
                    new org.rutebanken.netex.model.DayTypeRefStructure().withRef(dayTypeRef)));
        }
        condition.withDayTypes(dayTypes);

        if (!timebands.isEmpty()) {
            org.rutebanken.netex.model.Timebands_RelStructure timebandsRel = new org.rutebanken.netex.model.Timebands_RelStructure();
            timebandsRel.getTimebandRefOrTimeband().addAll(timebands);
            condition.withTimebands(timebandsRel);
        }
        return condition;
    }

    private org.rutebanken.netex.model.Parking netexParkingWithConditions(
            org.rutebanken.netex.model.AvailabilityCondition... conditions) {
        ObjectFactory objectFactory = new ObjectFactory();
        org.rutebanken.netex.model.ValidityConditions_RelStructure validityConditions =
                new org.rutebanken.netex.model.ValidityConditions_RelStructure();
        for (org.rutebanken.netex.model.AvailabilityCondition condition : conditions) {
            validityConditions.getValidityConditionRefOrValidBetweenOrValidityCondition_()
                    .add(objectFactory.createAvailabilityCondition(condition));
        }
        org.rutebanken.netex.model.Parking netexParking = new org.rutebanken.netex.model.Parking();
        netexParking.setId("NSR:Parking:1");
        netexParking.setVersion("1");
        netexParking.setValidityConditions(validityConditions);
        return netexParking;
    }

    private List<org.rutebanken.netex.model.AvailabilityCondition> exportedAvailabilityConditions(
            org.rutebanken.netex.model.Parking netexParking) {
        return netexParking.getValidityConditions().getValidityConditionRefOrValidBetweenOrValidityCondition_().stream()
                .filter(entry -> entry instanceof JAXBElement<?>)
                .map(entry -> ((JAXBElement<?>) entry).getValue())
                .filter(value -> value instanceof org.rutebanken.netex.model.AvailabilityCondition)
                .map(value -> (org.rutebanken.netex.model.AvailabilityCondition) value)
                .toList();
    }

    private org.rutebanken.netex.model.Timeband_VersionedChildStructure singleExportedTimeband(
            org.rutebanken.netex.model.Parking netexParking) {
        List<org.rutebanken.netex.model.AvailabilityCondition> conditions = exportedAvailabilityConditions(netexParking);
        assertThat(conditions).hasSize(1);
        List<Object> timebands = conditions.get(0).getTimebands().getTimebandRefOrTimeband();
        assertThat(timebands).hasSize(1);
        return (org.rutebanken.netex.model.Timeband_VersionedChildStructure) timebands.get(0);
    }

    @Test
    public void mapInternalParkingAvailabilityConditionsToNetex() {
        org.rutebanken.tiamat.model.AvailabilityCondition condition = new org.rutebanken.tiamat.model.AvailabilityCondition(
                "NSR:DayType:1", true, java.time.LocalTime.of(6, 0), java.time.LocalTime.of(22, 0));

        org.rutebanken.tiamat.model.Parking tiamatParking = new org.rutebanken.tiamat.model.Parking();
        tiamatParking.setNetexId("NSR:Parking:1");
        tiamatParking.setAvailabilityConditions(List.of(condition));

        org.rutebanken.netex.model.Parking netexParking = netexMapper.mapToNetexModel(tiamatParking);

        assertThat(netexParking.getValidityConditions()).isNotNull();
        List<Object> entries = netexParking.getValidityConditions().getValidityConditionRefOrValidBetweenOrValidityCondition_();
        List<org.rutebanken.netex.model.AvailabilityCondition> availabilityConditions = entries.stream()
                .filter(entry -> entry instanceof JAXBElement<?>)
                .map(entry -> ((JAXBElement<?>) entry).getValue())
                .filter(value -> value instanceof org.rutebanken.netex.model.AvailabilityCondition)
                .map(value -> (org.rutebanken.netex.model.AvailabilityCondition) value)
                .toList();
        assertThat(availabilityConditions).hasSize(1);

        org.rutebanken.netex.model.AvailabilityCondition netexCondition = availabilityConditions.get(0);
        assertThat(netexCondition.isIsAvailable()).isTrue();
        assertThat(netexCondition.getDayTypes().getDayTypeRefOrDayType_()).hasSize(1);
        org.rutebanken.netex.model.DayTypeRefStructure dayTypeRef =
                (org.rutebanken.netex.model.DayTypeRefStructure) netexCondition.getDayTypes().getDayTypeRefOrDayType_().get(0).getValue();
        assertThat(dayTypeRef.getRef()).isEqualTo("NSR:DayType:1");

        assertThat(netexCondition.getTimebands().getTimebandRefOrTimeband()).hasSize(1);
        Object timebandEntry = netexCondition.getTimebands().getTimebandRefOrTimeband().get(0);
        assertThat(timebandEntry).isInstanceOf(org.rutebanken.netex.model.Timeband_VersionedChildStructure.class);
        org.rutebanken.netex.model.Timeband_VersionedChildStructure timeband =
                (org.rutebanken.netex.model.Timeband_VersionedChildStructure) timebandEntry;
        assertThat(timeband.getStartTime()).isEqualTo(java.time.LocalTime.of(6, 0));
        assertThat(timeband.getEndTime()).isEqualTo(java.time.LocalTime.of(22, 0));
    }

    @Test
    public void mapInternalParkingAvailabilityConditionsToNetexIsIdempotentUnderRepeatInvocation() {
        org.rutebanken.tiamat.model.AvailabilityCondition condition = new org.rutebanken.tiamat.model.AvailabilityCondition(
                "NSR:DayType:1", true, java.time.LocalTime.of(6, 0), java.time.LocalTime.of(22, 0));

        org.rutebanken.tiamat.model.Parking tiamatParking = new org.rutebanken.tiamat.model.Parking();
        tiamatParking.setNetexId("NSR:Parking:1");
        tiamatParking.setAvailabilityConditions(List.of(condition));

        org.rutebanken.netex.model.Parking firstMap = netexMapper.mapToNetexModel(tiamatParking);
        org.rutebanken.netex.model.Parking secondMap = netexMapper.mapToNetexModel(tiamatParking);

        long firstCount = countAvailabilityConditions(firstMap);
        long secondCount = countAvailabilityConditions(secondMap);
        assertThat(firstCount).isEqualTo(1);
        assertThat(secondCount).isEqualTo(1);
    }

    private long countAvailabilityConditions(org.rutebanken.netex.model.Parking netexParking) {
        return netexParking.getValidityConditions().getValidityConditionRefOrValidBetweenOrValidityCondition_().stream()
                .filter(entry -> entry instanceof JAXBElement<?>)
                .map(entry -> ((JAXBElement<?>) entry).getValue())
                .filter(value -> value instanceof org.rutebanken.netex.model.AvailabilityCondition)
                .count();
    }

    @Test
    public void mapStopPlaceWithQuayToNetex() {
        org.rutebanken.tiamat.model.StopPlace stopPlace = new StopPlace();

        String netexId = "NSR:Quay:" + 1234567;
        org.rutebanken.tiamat.model.Quay tiamatQuay = new org.rutebanken.tiamat.model.Quay();
        tiamatQuay.setNetexId(netexId);
        tiamatQuay.setCompassBearing(123f);
        tiamatQuay.setLighting(LightingEnumeration.WELL_LIT);
        tiamatQuay.setPublicCode("A");

        stopPlace.getQuays().add(tiamatQuay);

        org.rutebanken.netex.model.StopPlace actualStop = netexMapper.mapToNetexModel(stopPlace);

        org.rutebanken.netex.model.Quay actualQuay = actualStop.getQuays().getQuayRefOrQuay().stream()
                .map(JAXBElement::getValue)
                .filter(object -> object instanceof org.rutebanken.netex.model.Quay)
                .map(object -> ((org.rutebanken.netex.model.Quay) object))
                .findFirst()
                .get();

        assertThat(actualQuay.getId()).isEqualTo(netexId);
        assertThat(actualQuay.getCompassBearing()).isEqualTo(123f);
        assertThat(actualQuay.getLighting()).isEqualTo(org.rutebanken.netex.model.LightingEnumeration.WELL_LIT);
        assertThat(actualQuay.getPublicCode()).isEqualTo("A");
    }

    @Test
    public void countryRefMappedToNetex() {
        SiteFrame tiamatSiteFrame = new SiteFrame();
        TopographicPlace topographicPlace = new TopographicPlace();

        topographicPlace.setNetexId("1");
        CountryRef countryRef = new CountryRef();
        countryRef.setRef(IanaCountryTldEnumeration.ZM);
        topographicPlace.setCountryRef(countryRef);

        tiamatSiteFrame
                .getTopographicPlaces()
                .getTopographicPlace()
                .add(topographicPlace);

        org.rutebanken.netex.model.SiteFrame netexSiteFrame = netexMapper.mapToNetexModel(tiamatSiteFrame);


        assertThat(netexSiteFrame).isNotNull();
        assertThat(netexSiteFrame.getTopographicPlaces().getTopographicPlace()).isNotEmpty();

        org.rutebanken.netex.model.TopographicPlace netexTopographicPlace = netexSiteFrame.getTopographicPlaces().getTopographicPlace().getFirst();
        assertThat(netexTopographicPlace.getCountryRef()).as("Reference to country shall not be null").isNotNull();
        assertThat(netexTopographicPlace.getCountryRef().getRef()).isEqualTo(org.rutebanken.netex.model.IanaCountryTldEnumeration.ZM);


    }

    @Test
    public void mapCountyRefsFromMunicipalitiesFromTiamatToNetex() {
        SiteFrame tiamatSiteFrame = new SiteFrame();

        CountryRef countryRef = new CountryRef();
        countryRef.setRef(IanaCountryTldEnumeration.ZM);

        TopographicPlace county = new TopographicPlace(new EmbeddableMultilingualString("Akershus"));
        county.setCountryRef(countryRef);
        county.setNetexId("1L");

        TopographicPlace municipality = new TopographicPlace(new EmbeddableMultilingualString("Asker"));
        municipality.setParentTopographicPlaceRef(new TopographicPlaceRefStructure(county));
        municipality.setNetexId("2L");

        tiamatSiteFrame
                .getTopographicPlaces()
                .getTopographicPlace()
                .add(municipality);

        tiamatSiteFrame
                .getTopographicPlaces()
                .getTopographicPlace()
                .add(county);

        org.rutebanken.netex.model.SiteFrame netexSiteFrame = netexMapper.mapToNetexModel(tiamatSiteFrame);


        assertThat(netexSiteFrame).isNotNull();
        assertThat(netexSiteFrame.getTopographicPlaces().getTopographicPlace()).isNotEmpty();

        org.rutebanken.netex.model.TopographicPlace netexMunicipality = netexSiteFrame.getTopographicPlaces().getTopographicPlace().getFirst();
        assertThat(netexMunicipality).isNotNull();
        assertThat(netexMunicipality.getParentTopographicPlaceRef()).describedAs("The municipality should have a reference to the parent topographic place").isNotNull();
        assertThat(netexMunicipality.getParentTopographicPlaceRef().getRef()).isEqualTo(county.getNetexId());
    }

    @Test
    public void mapCountyRefsFromMunicipalitiesFromNetexToTiamat() {
        org.rutebanken.netex.model.SiteFrame netexSiteFrame = new org.rutebanken.netex.model.SiteFrame();
        netexSiteFrame.withTopographicPlaces(new TopographicPlacesInFrame_RelStructure());

        org.rutebanken.netex.model.CountryRef countryRef = new org.rutebanken.netex.model.CountryRef();
        countryRef.setRef(org.rutebanken.netex.model.IanaCountryTldEnumeration.ZM);

        org.rutebanken.netex.model.TopographicPlace county = new org.rutebanken.netex.model.TopographicPlace();
        county.setId("NSR:TopographicPlace:1");
        county.setName(new MultilingualString().withValue("Akershus"));
        county.withCountryRef(countryRef);

        org.rutebanken.netex.model.TopographicPlace municipality = new org.rutebanken.netex.model.TopographicPlace();
        municipality.setId("NSR:TopographicPlace:2");
        municipality.setName(new MultilingualString().withValue("Asker"));
        municipality.withParentTopographicPlaceRef(
                new org.rutebanken.netex.model.TopographicPlaceRefStructure().withRef(county.getId())
        );

        netexSiteFrame
                .getTopographicPlaces()
                .getTopographicPlace()
                .add(municipality);

        netexSiteFrame
                .getTopographicPlaces()
                .getTopographicPlace()
                .add(county);

        // To be able to look up NSR references, we need to persist municipality and county
        TopographicPlace tiamatCounty = new TopographicPlace(new EmbeddableMultilingualString(county.getName().getValue()));
        tiamatCounty.setNetexId(county.getId());
        topographicPlaceRepository.save(tiamatCounty);

        TopographicPlace tiamatMunicipality = new TopographicPlace(new EmbeddableMultilingualString(municipality.getName().getValue()));
        tiamatMunicipality.setNetexId(municipality.getId());
        tiamatMunicipality.setParentTopographicPlaceRef(new TopographicPlaceRefStructure(tiamatCounty));
        topographicPlaceRepository.save(tiamatMunicipality);


        SiteFrame tiamatSiteFrame = netexMapper.mapToTiamatModel(netexSiteFrame);

        assertThat(netexSiteFrame).isNotNull();
        assertThat(netexSiteFrame.getTopographicPlaces().getTopographicPlace()).isNotEmpty();

        TopographicPlace actualTiamatMunicipality = tiamatSiteFrame.getTopographicPlaces().getTopographicPlace().getFirst();
        assertThat(actualTiamatMunicipality).isNotNull();
        assertThat(actualTiamatMunicipality.getParentTopographicPlaceRef())
                .describedAs("The municipality should have a parent topographic place").isNotNull();
        assertThat(actualTiamatMunicipality.getParentTopographicPlaceRef().getRef()).isEqualTo(county.getId());
    }


    @Test
    public void mapStopPlaceWithAccessibilityAssessmentToTiamat() {
        org.rutebanken.netex.model.StopPlace netexStopPlace = new org.rutebanken.netex.model.StopPlace();
        netexStopPlace.setAccessibilityAssessment(createNetexAccessibilityAssessment());

        assertThat(netexStopPlace.getAccessibilityAssessment()).isNotNull();
        assertThat(netexStopPlace.getAccessibilityAssessment().getLimitations()).isNotNull();

        StopPlace tiamatStopPlace = netexMapper.mapToTiamatModel(netexStopPlace);

        assertThat(tiamatStopPlace).isNotNull();
        assertThat(tiamatStopPlace.getAccessibilityAssessment()).isNotNull();
        assertThat(tiamatStopPlace.getAccessibilityAssessment().getLimitations()).isNotNull();
    }

    protected org.rutebanken.netex.model.AccessibilityAssessment createNetexAccessibilityAssessment() {
        org.rutebanken.netex.model.AccessibilityAssessment accessibilityAssessment = new org.rutebanken.netex.model.AccessibilityAssessment();

        final org.rutebanken.netex.model.AccessibilityLimitation accessibilityLimitation = getAccessibilityLimitation();
        AccessibilityLimitations_RelStructure limitationsRelStructure = new AccessibilityLimitations_RelStructure();


        limitationsRelStructure.setAccessibilityLimitation(accessibilityLimitation);
        accessibilityAssessment.setLimitations(limitationsRelStructure);
        return accessibilityAssessment;
    }

    @NotNull
    private static org.rutebanken.netex.model.AccessibilityLimitation getAccessibilityLimitation() {
        org.rutebanken.netex.model.AccessibilityLimitation accessibilityLimitation = new org.rutebanken.netex.model.AccessibilityLimitation();
        accessibilityLimitation.setWheelchairAccess(org.rutebanken.netex.model.LimitationStatusEnumeration.TRUE);
        accessibilityLimitation.setLiftFreeAccess(org.rutebanken.netex.model.LimitationStatusEnumeration.TRUE);
        accessibilityLimitation.setEscalatorFreeAccess(org.rutebanken.netex.model.LimitationStatusEnumeration.TRUE);
        accessibilityLimitation.setAudibleSignalsAvailable(org.rutebanken.netex.model.LimitationStatusEnumeration.TRUE);
        accessibilityLimitation.setVisualSignsAvailable(org.rutebanken.netex.model.LimitationStatusEnumeration.TRUE);
        accessibilityLimitation.setStepFreeAccess(org.rutebanken.netex.model.LimitationStatusEnumeration.TRUE);
        return accessibilityLimitation;
    }

    @Test
    public void mapStopPlaceWithAccessibilityAssessmentToNetex() {
        StopPlace tiamatStopPlace = new StopPlace();
        tiamatStopPlace.setAccessibilityAssessment(createAccessibilityAssessment());

        assertThat(tiamatStopPlace.getAccessibilityAssessment()).isNotNull();
        assertThat(tiamatStopPlace.getAccessibilityAssessment().getLimitations()).isNotNull();

        org.rutebanken.netex.model.StopPlace netexStopPlace = netexMapper.mapToNetexModel(tiamatStopPlace);

        assertThat(netexStopPlace).isNotNull();
        assertThat(netexStopPlace.getAccessibilityAssessment()).isNotNull();
        assertThat(netexStopPlace.getAccessibilityAssessment().getLimitations()).isNotNull();
        assertThat(netexStopPlace.getAccessibilityAssessment().getLimitations().getAccessibilityLimitation()).isNotNull();
    }

    @Test
    public void mapQuayAccessibilityAccessmentAssertNotNull() {
        Quay quay = new Quay();

        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();
        accessibilityAssessment.setMobilityImpairedAccess(null);

        quay.setAccessibilityAssessment(accessibilityAssessment);
        org.rutebanken.netex.model.Quay netexQuay = netexMapper.mapToNetexModel(quay);

        assertThat(netexQuay.getAccessibilityAssessment()).isNotNull();
        assertThat(netexQuay.getAccessibilityAssessment().getMobilityImpairedAccess())
                .as("mobilityImpairedAccess")
                .isNotNull()
                .isEqualByComparingTo(org.rutebanken.netex.model.LimitationStatusEnumeration.UNKNOWN);

    }

    protected AccessibilityAssessment createAccessibilityAssessment() {
        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();

        AccessibilityLimitation accessibilityLimitation = new AccessibilityLimitation();
        accessibilityLimitation.setWheelchairAccess(LimitationStatusEnumeration.TRUE);
        accessibilityLimitation.setLiftFreeAccess(LimitationStatusEnumeration.TRUE);
        accessibilityLimitation.setEscalatorFreeAccess(LimitationStatusEnumeration.TRUE);
        accessibilityLimitation.setAudibleSignalsAvailable(LimitationStatusEnumeration.TRUE);
        accessibilityLimitation.setVisualSignsAvailable(LimitationStatusEnumeration.TRUE);
        accessibilityLimitation.setStepFreeAccess(LimitationStatusEnumeration.TRUE);

        List<AccessibilityLimitation> limitations = new ArrayList<>();
        limitations.add(accessibilityLimitation);
        accessibilityAssessment.setLimitations(limitations);
        return accessibilityAssessment;
    }

    @Test
    public void accessibilityAssesmentIdToNetex() throws Exception {
        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();
        accessibilityAssessment.setNetexId("NSR:AccessibilityAssesment:123124");

        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:321123");
        stopPlace.setAccessibilityAssessment(accessibilityAssessment);

        org.rutebanken.netex.model.StopPlace netexStopPlace = netexMapper.mapToNetexModel(stopPlace);

        org.rutebanken.netex.model.AccessibilityAssessment netexAccessibilityAssesment = netexStopPlace.getAccessibilityAssessment();

        assertThat(netexAccessibilityAssesment.getId()).isNotEmpty();
        assertThat(netexAccessibilityAssesment.getId()).isEqualToIgnoringCase("NSR:AccessibilityAssesment:123124");
    }

    @Test
    public void pathLinkToNetex() throws Exception {
        PathLink pathLink = new PathLink(
                new PathLinkEnd(new AddressablePlaceRefStructure("NSR:StopPlace:1", "1")),
                new PathLinkEnd(new AddressablePlaceRefStructure("NSR:StopPlace:2", "1")));
        pathLink.setNetexId("NSR:PathLink:1");
        pathLink.setVersion(2L);

        org.rutebanken.netex.model.PathLink netexPathLink = netexMapper.mapToNetexModel(pathLink);
        assertThat(netexPathLink.getId()).isEqualTo(pathLink.getNetexId());
        assertThat(netexPathLink.getVersion()).as("version").isEqualTo(String.valueOf(pathLink.getVersion()));
        assertThat(netexPathLink.getTo()).isNotNull();
        assertThat(netexPathLink.getFrom()).isNotNull();

        assertThat(netexPathLink.getFrom().getPlaceRef().getRef()).isEqualTo(pathLink.getFrom().getPlaceRef().getRef());
        assertThat(netexPathLink.getFrom().getPlaceRef().getVersion()).isEqualTo(pathLink.getFrom().getPlaceRef().getVersion());

        assertThat(netexPathLink.getTo().getPlaceRef().getRef()).isEqualTo(pathLink.getTo().getPlaceRef().getRef());
        assertThat(netexPathLink.getTo().getPlaceRef().getVersion()).isEqualTo(pathLink.getTo().getPlaceRef().getVersion());
    }

    @Test
    public void mapAdjacentSitesToNetex() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlace.getAdjacentSites().add(new SiteRefStructure("NSR:StopPlace:1"));

        org.rutebanken.netex.model.StopPlace netexStopPlace = netexMapper.mapToNetexModel(stopPlace);

        SiteRefs_RelStructure siteRefs_relStructure = netexStopPlace.getAdjacentSites();


        assertThat(siteRefs_relStructure).isNotNull();

        assertThat(siteRefs_relStructure.getSiteRef()).isNotEmpty();

        org.rutebanken.netex.model.SiteRefStructure firstSiteRef = siteRefs_relStructure.
                getSiteRef().
                getFirst()
                .getValue();

        assertThat(firstSiteRef)
                .as("First site ref")
                .isNotNull();

        assertThat(
                firstSiteRef.getRef())
                .isEqualTo("NSR:StopPlace:1");
    }

    /**
     * A FareZone with EXPLICIT_STOPS scoping stores its members as StopPlace references.
     * On export each member is rewritten to a ScheduledStopPoint ref with an "S" prefix,
     * so the import must strip that prefix to restore the original StopPlace ref.
     * This verifies the export -> import round-trip is stable.
     */
    @Test
    public void fareZoneExplicitStopsMembersSurviveNetexRoundTrip() {
        NetexMappingContext mappingContext = new NetexMappingContext();
        mappingContext.defaultTimeZone = ZoneId.of("Europe/Oslo");
        NetexMappingContextThreadLocal.set(mappingContext);
        try {
            FareZone tiamatFareZone = new FareZone();
            tiamatFareZone.setNetexId("NSR:FareZone:1");
            tiamatFareZone.setVersion(1L);
            tiamatFareZone.setName(new EmbeddableMultilingualString("Round trip zone"));
            tiamatFareZone.setValidBetween(new ValidBetween(Instant.now().minusSeconds(3600)));
            tiamatFareZone.setScopingMethod(ScopingMethodEnumeration.EXPLICIT_STOPS);
            tiamatFareZone.getFareZoneMembers().add(new StopPlaceReference("NSR:StopPlace:123"));

            // Export: StopPlace ref -> ScheduledStopPoint ref with "S" prefix added.
            org.rutebanken.netex.model.FareZone netexFareZone = netexMapper.mapToNetexModel(tiamatFareZone);

            assertThat(netexFareZone.getScopingMethod())
                    .as("scoping method preserved on export")
                    .isEqualTo(org.rutebanken.netex.model.ScopingMethodEnumeration.EXPLICIT_STOPS);
            assertThat(netexFareZone.getMembers()).as("members").isNotNull();
            assertThat(netexFareZone.getMembers().getPointRef())
                    .as("exported member point refs")
                    .extracting(pointRef -> pointRef.getValue().getRef())
                    .containsExactly("NSR:ScheduledStopPoint:S123");

            // Import: ScheduledStopPoint ref -> StopPlace ref, "S" prefix stripped.
            FareZone roundTripped = netexMapper.mapToTiamatModel(netexFareZone);

            assertThat(roundTripped.getFareZoneMembers())
                    .as("round-tripped fare zone members")
                    .extracting(StopPlaceReference::getRef)
                    .containsExactly("NSR:StopPlace:123");
        } finally {
            NetexMappingContextThreadLocal.set(null);
        }
    }
}
