package no.rutebanken.tiamat.config;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.gtfs.GtfsStopsReader;
import no.rutebanken.tiamat.nvdb.service.NvdbSync;
import no.rutebanken.tiamat.repository.ifopt.AccessSpaceRepository;
import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import no.rutebanken.tiamat.repository.ifopt.TariffZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import uk.org.netex.netex.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Create some example data.
 */
@Configuration
@Profile("bootstrap")
public class BootStrap implements InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger(BootStrap.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private TariffZoneRepository tariffZoneRepository;

    @Autowired
    private AccessSpaceRepository accessSpaceRepository;

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private NvdbSync nvdbSync;

    @Autowired
    private GtfsStopsReader gtfsStopsReader;

    @Autowired
    private GeometryFactory geometryFactory;

    /**
     * Set up test object.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Quay quay = createExampleQuay();


        createExampleStopPlace(quay);
//       nvdbSync.fetchNvdb();

        Executors.newSingleThreadExecutor().execute(() -> gtfsStopsReader.read());
    }

    private Quay createExampleQuay() {

        Quay quay = new Quay();
        quay.setVersion("001");
        quay.setCreated(new Date());
        quay.setDataSourceRef("nptg:DataSource:NaPTAN");
        quay.setResponsibilitySetRef("nptg:ResponsibilitySet:082");

        quay.setName(new MultilingualString("Wimbledon, Stop P", "en", ""));
        quay.setShortName(new MultilingualString("Wimbledon", "en", ""));
        quay.setDescription(new MultilingualString("Stop P  is paired with Stop C outside the station", "en", ""));

        quay.setCovered(CoveredEnumeration.COVERED);

        quay.setBoardingUse(true);
        quay.setAlightingUse(true);
        quay.setLabel(new MultilingualString("Stop P", "en", ""));
        quay.setPublicCode("1-2345");

        quay.setCompassOctant(CompassBearing8Enumeration.W);
        quay.setQuayType(QuayTypeEnumeration.BUS_STOP);

        DestinationDisplayView destinationDisplayView = new DestinationDisplayView();
        destinationDisplayView.setName(new MultilingualString("Towards London", "en", ""));

        quay.setDestinations(new ArrayList<>());
        quay.getDestinations().add(destinationDisplayView);

        RoadAddress roadAddress = new RoadAddress();
        roadAddress.setVersion("any");
        roadAddress.setRoadName(new MultilingualString("Wimbledon Bridge", "en", ""));
        roadAddress.setBearingCompass("W");
        quay.setRoadAddress(roadAddress);

        SimplePoint centroid = new SimplePoint();
        centroid.setLocation(geometryFactory.createPoint(new Coordinate(10, 59.4207729447)));
        quay.setCentroid(centroid);

        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();
        accessibilityAssessment.setVersion("any");
        accessibilityAssessment.setMobilityImpairedAccess(LimitationStatusEnumeration.TRUE);

        List<AccessibilityLimitation> accessibilityLimitations = new ArrayList<>();

        AccessibilityLimitation accessibilityLimitation = new AccessibilityLimitation();
        accessibilityLimitation.setWheelchairAccess(LimitationStatusEnumeration.TRUE);
        accessibilityLimitation.setStepFreeAccess(LimitationStatusEnumeration.TRUE);
        accessibilityLimitation.setEscalatorFreeAccess(LimitationStatusEnumeration.TRUE);
        accessibilityLimitation.setLiftFreeAccess(LimitationStatusEnumeration.TRUE);
        accessibilityLimitation.setAudibleSignalsAvailable(LimitationStatusEnumeration.TRUE);
        accessibilityLimitation.setVisualSignsAvailable(LimitationStatusEnumeration.TRUE);

        accessibilityLimitations.add(accessibilityLimitation);

        accessibilityAssessment.setLimitations(accessibilityLimitations);

        quay.setAccessibilityAssessment(accessibilityAssessment);

        LevelRefStructure levelRefStructure = new LevelRefStructure();
        levelRefStructure.setVersion("001");
        levelRefStructure.setRef("tbd:Level:9100WIMBLDN_Lvl_ST");

        quay.setLevelRef(levelRefStructure);

        SiteRefStructure siteRefStructure = new SiteRefStructure();
        siteRefStructure.setVersion("001");
        siteRefStructure.setRef("napt:StopPlace:490G00272P");

        quay.setSiteRef(siteRefStructure);

        return quay;
    }

    public void createExampleStopPlace(Quay quay) {
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

        stopPlace.getAccessSpaces().add(accessSpace);

        SimplePoint centroid = new SimplePoint();
        centroid.setLocation(geometryFactory.createPoint(new Coordinate(10.0, 59.0)));

        stopPlace.setCentroid(centroid);

        TariffZone tariffZone = new TariffZone();
        tariffZone.setShortName(new MultilingualString("V2", "no", "type"));

        tariffZoneRepository.save(tariffZone);


        List<TariffZone> tariffZones = new ArrayList<>();
        tariffZones.add(tariffZone);
        stopPlace.setTariffZones(tariffZones);

        MultilingualString description = new MultilingualString();
        description.setValue("description");
        stopPlace.setDescription(description);

        stopPlace.setTransportMode(VehicleModeEnumeration.RAIL);
        stopPlace.setAirSubmode(AirSubmodeEnumeration.UNDEFINED);
        stopPlace.setCoachSubmode(CoachSubmodeEnumeration.REGIONAL_COACH);
        stopPlace.setFunicularSubmode(FunicularSubmodeEnumeration.UNKNOWN);
        stopPlace.getOtherTransportModes().add(VehicleModeEnumeration.AIR);

        List<Quay> quays = new ArrayList<>();
        quays.add(quay);
        stopPlace.setQuays(quays);

        stopPlaceRepository.save(stopPlace);
    }
}
