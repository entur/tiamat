package uk.org.netex.netex;

import no.rutebanken.tiamat.TiamatApplication;
import no.rutebanken.tiamat.repository.ifopt.AccessSpaceRepository;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import no.rutebanken.tiamat.repository.ifopt.TariffZoneRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TiamatApplication.class)
public class StopPlaceTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private TariffZoneRepository tariffZoneRepository;

    @Autowired
    private AccessSpaceRepository accessSpaceRepository;


    @Test
    public void persistStopPlaceWithAccessSpace() {
        StopPlace stopPlace = new StopPlace();

        AccessSpace accessSpace = new AccessSpace();
        accessSpace.setShortName(new MultilingualString("Østbanehallen", "no", ""));

        stopPlace.getAccessSpaces().add(accessSpace);

        stopPlaceRepository.save(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findOne(stopPlace.getId());

        assertThat(actualStopPlace.getAccessSpaces()).isNotEmpty();
        assertThat(actualStopPlace.getAccessSpaces().get(0).getShortName().getValue()).isEqualTo(accessSpace.getShortName().getValue());
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

        StopPlace actualStopPlace = stopPlaceRepository.findOne(stopPlace.getId());

        assertThat(actualStopPlace.getTariffZones()).isNotEmpty();
        assertThat(actualStopPlace.getTariffZones().get(0).getId()).isEqualTo(tariffZone.getId());
    }

    @Test
    public void persistStopPlaceWithParentReference() {
        StopPlace stopPlace = new StopPlace();

        StopPlaceReference stopPlaceReference = new StopPlaceReference();
        stopPlaceReference.setRef("id-to-another-stop-place");
        stopPlaceReference.setVersion("001");

        stopPlace.setParentSiteRef(stopPlaceReference);

        stopPlaceRepository.save(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findOne(stopPlace.getId());
        assertThat(actualStopPlace.getParentSiteRef().getRef()).isEqualTo(stopPlaceReference.getRef());
    }

    @Test
    public void persistStopPlaceWithOtherVehicleMode() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.getOtherTransportModes().add(VehicleModeEnumeration.AIR);
        stopPlaceRepository.save(stopPlace);
        StopPlace actualStopPlace = stopPlaceRepository.findOne(stopPlace.getId());
        assertThat(actualStopPlace.getOtherTransportModes()).contains(VehicleModeEnumeration.AIR);
    }

    @Test
    public void persistStopPlaceShortNameAndPublicCode() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setPublicCode("public-code");
        MultilingualString shortName = new MultilingualString();
        shortName.setLang("no");
        shortName.setValue("Skjervik");
        stopPlace.setShortName(shortName);
        stopPlace.setPublicCode("publicCode");

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
}
