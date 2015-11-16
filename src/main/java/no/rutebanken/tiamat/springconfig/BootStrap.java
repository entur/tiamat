package no.rutebanken.tiamat.springconfig;

import no.rutebanken.tiamat.repository.ifopt.AccessSpaceRepository;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import no.rutebanken.tiamat.repository.ifopt.TariffZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import uk.org.netex.netex.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
public class BootStrap implements InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger(BootStrap.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private TariffZoneRepository tariffZoneRepository;

    @Autowired
    private AccessSpaceRepository accessSpaceRepository;

    /**
     * Set up test object.
     */
    @Override
    public void afterPropertiesSet() throws Exception {


        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        MultilingualString name = new MultilingualString();
        name.setTextIdType("textIdType");
        name.setValue("Bootstrapped stop place.");

        stopPlace.setName(name);

        MultilingualString shortName = new MultilingualString();
        shortName.setValue("Bahnhof");
        shortName.setTextIdType("textId");

        stopPlace.setShortName(shortName);

        AccessSpace accessSpace = new AccessSpace();
        accessSpace.setShortName(new MultilingualString("Østbanehallen", "no", ""));
        accessSpace.setAccessSpaceType(AccessSpaceTypeEnumeration.CONCOURSE);
        accessSpaceRepository.save(accessSpace);

        List<AccessSpace> accessSpaces = new ArrayList<>();
        accessSpaces.add(accessSpace);
        stopPlace.setAccessSpaces(accessSpaces);

        LocationStructure locationStructure = new LocationStructure();
        locationStructure.setLatitude(new BigDecimal(10));
        locationStructure.setLongitude(new BigDecimal(20));

        SimplePoint_VersionStructure centroid = new SimplePoint_VersionStructure();
        centroid.setLocation(locationStructure);

        stopPlace.setCentroid(centroid);

        TariffZone tariffZone = new TariffZone();
        tariffZone.setShortName(new MultilingualString("V2", "no", "type"));

        tariffZoneRepository.save(tariffZone);


        List<TariffZone> tariffZones = new ArrayList<>();
        tariffZones.add(tariffZone);
        stopPlace.setTariffZones(tariffZones);

        stopPlaceRepository.save(stopPlace);

        MultilingualString description = new MultilingualString();
        description.setValue("description");
        stopPlace.setDescription(description);

        stopPlace.setTransportMode(VehicleModeEnumeration.RAIL);
        stopPlace.setAirSubmode(AirSubmodeEnumeration.UNDEFINED);
        stopPlace.setCoachSubmode(CoachSubmodeEnumeration.REGIONAL_COACH);
        stopPlace.setFunicularSubmode(FunicularSubmodeEnumeration.UNKNOWN);
        stopPlace.getOtherTransportModes().add(VehicleModeEnumeration.AIR);

        stopPlaceRepository.save(stopPlace);
    }
}
