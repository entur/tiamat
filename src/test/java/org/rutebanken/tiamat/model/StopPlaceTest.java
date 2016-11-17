package org.rutebanken.tiamat.model;

import org.junit.Ignore;
import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class StopPlaceTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private QuayRepository quayRepository;

    @Test
    public void persistStopPlaceWithAccessSpace() {
        StopPlace stopPlace = new StopPlace();

        AccessSpace accessSpace = new AccessSpace();
        accessSpace.setShortName(new EmbeddableMultilingualString("Østbanehallen", "no"));

        stopPlace.getAccessSpaces().add(accessSpace);

        stopPlaceRepository.save(stopPlace);
        StopPlace actualStopPlace = stopPlaceRepository.findStopPlaceDetailed(stopPlace.getId());

        assertThat(actualStopPlace.getAccessSpaces()).isNotEmpty();
        assertThat(actualStopPlace.getAccessSpaces().get(0).getShortName().getValue()).isEqualTo(accessSpace.getShortName().getValue());
    }

    @Test
    public void persistStopPlaceWithEquipmentPlace() {

        StopPlace stopPlace = new StopPlace();

        EquipmentPlace equipmentPlace = new EquipmentPlace();
        List<EquipmentPlace> equipmentPlaces = new ArrayList<>();
        equipmentPlaces.add(equipmentPlace);

        stopPlace.setEquipmentPlaces(equipmentPlaces);

        stopPlaceRepository.save(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findStopPlaceDetailed(stopPlace.getId());

        assertThat(actualStopPlace.getEquipmentPlaces()).isNotEmpty();
        assertThat(actualStopPlace.getEquipmentPlaces().get(0).getId()).isEqualTo(equipmentPlace.getId());
    }

    @Ignore // level is transient
    @Test
    public void persistStopPlaceWithLevels() {
        StopPlace stopPlace = new StopPlace();

        Level level = new Level();
        level.setName(new MultilingualStringEntity("Erde", "fr"));
        level.setPublicCode("E");
        level.setVersion("01");
        stopPlace.getLevels().add(level);

        stopPlaceRepository.save(stopPlace);
        StopPlace actualStopPlace = stopPlaceRepository.findStopPlaceDetailed(stopPlace.getId());

        assertThat(actualStopPlace.getLevels()).isNotEmpty();
        assertThat(actualStopPlace.getLevels().get(0).getName().getValue()).isEqualTo(level.getName().getValue());
        assertThat(actualStopPlace.getLevels().get(0).getPublicCode()).isEqualTo(level.getPublicCode());
        assertThat(actualStopPlace.getLevels().get(0).getVersion()).isEqualTo(level.getVersion());
    }

    @Ignore
    @Test
    public void persistStopPlaceWithRoadAddress() {
        StopPlace stopPlace = new StopPlace();
        RoadAddress roadAddress = new RoadAddress();
        stopPlace.setRoadAddress(roadAddress);

        stopPlaceRepository.save(stopPlace);
        StopPlace actualStopPlace = stopPlaceRepository.findStopPlaceDetailed(stopPlace.getId());

        assertThat(actualStopPlace.getRoadAddress()).isNotNull();
        assertThat(actualStopPlace.getRoadAddress().getId()).isEqualTo(roadAddress.getId());
    }

    @Ignore
    @Test
    public void persistStopPlaceWithValidityCondition() {

        StopPlace stopPlace = new StopPlace();

        ValidityCondition validityCondition = new ValidityCondition();
        validityCondition.setName(new MultilingualStringEntity("Validity condition", "en"));

        stopPlace.getValidityConditions().add(validityCondition);

        stopPlaceRepository.save(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findStopPlaceDetailed(stopPlace.getId());

        assertThat(actualStopPlace.getValidityConditions()).isNotEmpty();
        assertThat(actualStopPlace.getValidityConditions().get(0).getName().getValue()).isEqualTo(validityCondition.getName().getValue());
    }

    @Test
    public void persistStopPlaceWithParentReference() {
        StopPlace stopPlace = new StopPlace();

        StopPlaceReference stopPlaceReference = new StopPlaceReference();
        stopPlaceReference.setRef("id-to-another-stop-place");
        stopPlaceReference.setVersion("001");

        stopPlace.setParentSiteRef(stopPlaceReference);

        stopPlaceRepository.save(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findStopPlaceDetailed(stopPlace.getId());
        assertThat(actualStopPlace.getParentSiteRef().getRef()).isEqualTo(stopPlaceReference.getRef());
    }

    @Ignore // other vehicle mode is transient
    @Test
    public void persistStopPlaceWithOtherVehicleMode() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.getOtherTransportModes().add(VehicleModeEnumeration.AIR);
        stopPlaceRepository.save(stopPlace);
        StopPlace actualStopPlace = stopPlaceRepository.findStopPlaceDetailed(stopPlace.getId());
        assertThat(actualStopPlace.getOtherTransportModes()).contains(VehicleModeEnumeration.AIR);
    }

    @Test
    public void persistStopPlaceWithDataSourceRef() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setDataSourceRef("dataSourceRef");
        stopPlaceRepository.save(stopPlace);
        StopPlace actualStopPlace = stopPlaceRepository.findOne(stopPlace.getId());
        assertThat(actualStopPlace.getDataSourceRef()).isEqualTo(stopPlace.getDataSourceRef());
    }

    @Test
    public void persistStopPlaceWithCreatedDate() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setCreated(ZonedDateTime.ofInstant(Instant.ofEpochMilli(10000), ZoneId.systemDefault()));
        stopPlaceRepository.save(stopPlace);
        StopPlace actualStopPlace = stopPlaceRepository.findOne(stopPlace.getId());
        assertThat(actualStopPlace.getCreated()).isEqualTo(stopPlace.getCreated());
    }

    @Test
    public void persistStopPlaceWithChangedDate() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setChanged(ZonedDateTime.ofInstant(Instant.ofEpochMilli(10000), ZoneId.systemDefault()));
        stopPlaceRepository.save(stopPlace);
        StopPlace actualStopPlace = stopPlaceRepository.findOne(stopPlace.getId());
        assertThat(actualStopPlace.getChanged()).isEqualTo(stopPlace.getChanged());
    }

    @Test
    public void persistStopPlaceWitAlternativeName() {
        StopPlace stopPlace = new StopPlace();

        AlternativeName alternativeName = new AlternativeName();
        alternativeName.setShortName(new MultilingualStringEntity("short name", "en"));
        stopPlace.getAlternativeNames().add(alternativeName);
        stopPlaceRepository.save(stopPlace);
        StopPlace actualStopPlace = stopPlaceRepository.findStopPlaceDetailed(stopPlace.getId());
        assertThat(actualStopPlace.getAlternativeNames()).isNotEmpty();
        assertThat(actualStopPlace.getAlternativeNames().get(0).getShortName().getValue()).isEqualTo(alternativeName.getShortName().getValue());
    }

    @Test
    public void persistStopPlaceWithDescription() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setDescription(new EmbeddableMultilingualString("description", "en"));
        stopPlaceRepository.save(stopPlace);
        StopPlace actualStopPlace = stopPlaceRepository.findOne(stopPlace.getId());
        assertThat(actualStopPlace.getDescription().getValue()).isEqualTo(stopPlace.getDescription().getValue());
    }

    @Test
    public void persistStopPlaceShortNameAndPublicCode() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setPublicCode("public-code");
        stopPlace.setName(new EmbeddableMultilingualString("Skjervik", "no"));
        stopPlace.setShortName(new EmbeddableMultilingualString("short name"));

        stopPlaceRepository.save(stopPlace);
        StopPlace actualStopPlace = stopPlaceRepository.findOne(stopPlace.getId());

        assertThat(actualStopPlace.getPublicCode()).isEqualTo(stopPlace.getPublicCode());
        assertThat(actualStopPlace.getId()).isEqualTo(stopPlace.getId());
        assertThat(actualStopPlace.getShortName().getValue()).isEqualTo(stopPlace.getShortName().getValue());
    }

    @Test
    public void persistStopPlaceEnums() {
        StopPlace stopPlace = new StopPlace();

        stopPlace.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
        stopPlace.setTransportMode(VehicleModeEnumeration.RAIL);
        stopPlace.setAirSubmode(AirSubmodeEnumeration.UNDEFINED);
        stopPlace.setCoachSubmode(CoachSubmodeEnumeration.REGIONAL_COACH);
        stopPlace.setFunicularSubmode(FunicularSubmodeEnumeration.UNKNOWN);
        stopPlace.getOtherTransportModes().add(VehicleModeEnumeration.AIR);
        stopPlace.setLimitedUse(LimitedUseTypeEnumeration.LONG_WALK_TO_ACCESS);
        stopPlace.setWeighting(InterchangeWeightingEnumeration.RECOMMENDED_INTERCHANGE);
        stopPlace.setBusSubmode(BusSubmodeEnumeration.DEMAND_AND_RESPONSE_BUS);
        stopPlace.setCovered(CoveredEnumeration.INDOORS);
        stopPlace.setGated(GatedEnumeration.OPEN_AREA);
        stopPlace.setModification(ModificationEnumeration.NEW);
        stopPlace.setRailSubmode(RailSubmodeEnumeration.HIGH_SPEED_RAIL);
        stopPlace.setMetroSubmode(MetroSubmodeEnumeration.METRO);
        stopPlace.setSiteType(SiteTypeEnumeration.OFFICE);
        stopPlace.setStatus(StatusEnumeration.OTHER);
        stopPlace.setWaterSubmode(WaterSubmodeEnumeration.CABLE_FERRY);
        stopPlace.setTramSubmode(TramSubmodeEnumeration.REGIONAL_TRAM);
        stopPlace.setTelecabinSubmode(TelecabinSubmodeEnumeration.TELECABIN);

        stopPlaceRepository.save(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findOne(stopPlace.getId());

        assertThat(actualStopPlace).isEqualToComparingOnlyGivenFields(actualStopPlace,
                "stopPlaceType", "transportMode", "airSubmode", "coachSubmode",
                "funicularSubmode", "otherTransportModes", "limitedUse",
                "weighting", "busSubmode", "covered", "gated", "modification",
                "railSubmode", "metroSubmode", "siteType", "status", "waterSubmode",
                "tramSubmode", "telecabinSubmode");
    }

    @Test
    public void testAttachingQuaysToStopPlace() throws Exception {
        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("q", "en"));
        quayRepository.save(quay);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);
    }

    @Test
    public void testKeyValueStructure() throws Exception {
        StopPlace stopPlace = new StopPlace();
        List<String> ids = Arrays.asList("OPP:StopArea:123123", "TEL:StopArea:3251321");
        Value value = new Value(ids);
        stopPlace.getKeyValues().put("ORIGINAL_ID", value);

        stopPlaceRepository.save(stopPlace);

        StopPlace actual = stopPlaceRepository.findOne(stopPlace.getId());

        assertThat(actual.getKeyValues().get("ORIGINAL_ID").getItems().containsAll(ids));
    }

    @Test
    public void testAddKeyValueAndRemove() throws Exception {
        StopPlace stopPlace = new StopPlace();
        // Add two
        List<String> ids = Arrays.asList("OPP:StopArea:1337", "TEL:StopArea:666");
        Value value = new Value(ids);
        stopPlace.getKeyValues().put("ORIGINAL_ID", value);

        stopPlaceRepository.save(stopPlace);

        stopPlace.getKeyValues().get("ORIGINAL_ID").getItems().remove("TEL:StopArea:666");

        stopPlaceRepository.save(stopPlace);

        StopPlace actual = stopPlaceRepository.findOne(stopPlace.getId());

        assertThat(actual.getKeyValues().get("ORIGINAL_ID").getItems()).hasSize(1);
    }

    @Test
    public void testToString() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("stoppested"));
        stopPlace.setId(123123123L);

        System.out.println(stopPlace.toString());
    }
}
