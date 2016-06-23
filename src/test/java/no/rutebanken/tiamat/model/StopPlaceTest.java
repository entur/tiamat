package no.rutebanken.tiamat.model;

import no.rutebanken.tiamat.TiamatApplication;
import no.rutebanken.tiamat.repository.QuayRepository;
import no.rutebanken.tiamat.repository.StopPlaceRepository;
import no.rutebanken.tiamat.repository.TariffZoneRepository;
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
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class StopPlaceTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private TariffZoneRepository tariffZoneRepository;

    @Autowired
    private QuayRepository quayRepository;

    @Test
    public void persistStopPlaceWithAccessSpace() {
        StopPlace stopPlace = new StopPlace();

        AccessSpace accessSpace = new AccessSpace();
        accessSpace.setShortName(new MultilingualString("Østbanehallen", "no", ""));

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

    @Test
    public void persistStopPlaceWithCentroid() {

        StopPlace stopPlace = new StopPlace();
        SimplePoint centroid = new SimplePoint();
        stopPlace.setCentroid(centroid);
        stopPlaceRepository.save(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findOne(stopPlace.getId());

        assertThat(actualStopPlace.getCentroid()).isNotNull();
    }

    @Test
    public void persistStopPlaceWithLevels() {
        StopPlace stopPlace = new StopPlace();

        Level level = new Level();
        level.setName(new MultilingualString("Erde", "fr", ""));
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

    @Test
    public void persistStopPlaceWithTariffZone() {
        StopPlace stopPlace = new StopPlace();

        TariffZone tariffZone = new TariffZone();
        tariffZone.setShortName(new MultilingualString("V2", "no", "type"));

        tariffZoneRepository.save(tariffZone);

        List<TariffZone> tariffZones = new ArrayList<>();
        tariffZones.add(tariffZone);
        stopPlace.setTariffZones(tariffZones);

        stopPlaceRepository.save(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findStopPlaceDetailed(stopPlace.getId());

        assertThat(actualStopPlace.getTariffZones()).isNotEmpty();
        assertThat(actualStopPlace.getTariffZones().get(0).getId()).isEqualTo(tariffZone.getId());
    }

    @Test
    public void persistStopPlaceWithValidityCondition() {

        StopPlace stopPlace = new StopPlace();

        ValidityCondition validityCondition = new ValidityCondition();
        validityCondition.setName(new MultilingualString("Validity condition", "en", ""));

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
        alternativeName.setShortName(new MultilingualString("short name", "en", ""));
        stopPlace.getAlternativeNames().add(alternativeName);
        stopPlaceRepository.save(stopPlace);
        StopPlace actualStopPlace = stopPlaceRepository.findStopPlaceDetailed(stopPlace.getId());
        assertThat(actualStopPlace.getAlternativeNames()).isNotEmpty();
        assertThat(actualStopPlace.getAlternativeNames().get(0).getShortName().getValue()).isEqualTo(alternativeName.getShortName().getValue());
    }

    @Test
    public void persistStopPlaceWithDescription() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setDescription(new MultilingualString("description", "en", ""));
        stopPlaceRepository.save(stopPlace);
        StopPlace actualStopPlace = stopPlaceRepository.findOne(stopPlace.getId());
        assertThat(actualStopPlace.getDescription().getValue()).isEqualTo(stopPlace.getDescription().getValue());
    }

    @Test
    public void persistStopPlaceShortNameAndPublicCode() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setPublicCode("public-code");
        MultilingualString shortName = new MultilingualString();
        shortName.setLang("no");
        shortName.setValue("Skjervik");
        stopPlace.setShortName(shortName);

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
        quay.setName(new MultilingualString("q", "en", ""));
        quayRepository.save(quay);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setQuays(new ArrayList<>());
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);
    }
}
