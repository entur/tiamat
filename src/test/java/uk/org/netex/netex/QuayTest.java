package uk.org.netex.netex;

import no.rutebanken.tiamat.TiamatApplication;
import no.rutebanken.tiamat.repository.ifopt.LocationRepository;
import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TiamatApplication.class)
public class QuayTest {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    public QuayRepository quayRepository;

    /**
     * Using example data from https://github.com/StichtingOpenGeo/NeTEx/blob/master/examples/functions/stopPlace/Netex_10_StopPlace_uk_ComplexStation_Wimbledon_1.xml
     * @throws ParseException
     */
    @Test
    public void persistQuayWithCommonValues() throws ParseException {
        Quay quay = new Quay();
        quay.setVersion("001");
        quay.setCreated(dateFormat.parse("2010-04-17T09:30:47Z"));
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

        quayRepository.save(quay);

        Quay actualQuay = quayRepository.findOne(quay.getId());

        assertThat(actualQuay).isNotNull();
        assertThat(actualQuay.getId()).isEqualTo(quay.getId());
        String[] verifyColumns = new String[] {"id", "name.value", "version",
                "created.time", "shortName.value", "covered", "description.value", "publicCode",
                "label.value", "boardingUse", "compassOctant", "quayType",  "alightingUse"};
        assertThat(actualQuay).isEqualToComparingOnlyGivenFields(quay, verifyColumns);
    }

    @Test
    public void persistQuayWithDestinations() {

        Quay quay = new Quay();
        DestinationDisplayView destinationDisplayView = new DestinationDisplayView();
        destinationDisplayView.setName(new MultilingualString("Towards London", "en", ""));

        quay.setDestinations(new ArrayList<>());
        quay.getDestinations().add(destinationDisplayView);

        quayRepository.save(quay);

        Quay actualQuay = quayRepository.findOne(quay.getId());

        assertThat(actualQuay.getDestinations()).isNotEmpty();
        DestinationDisplayView actualDestinationDisplayView = actualQuay.getDestinations().get(0);
        assertThat(actualDestinationDisplayView.getName().getValue()).isEqualTo(destinationDisplayView.getName().getValue());
    }

    @Test
    public void persistQuayWithRoadAddress() {
        Quay quay = new Quay();
        RoadAddress roadAddress = new RoadAddress();
        roadAddress.setVersion("any");
        roadAddress.setRoadName(new MultilingualString("Wimbledon Bridge", "en", ""));
        roadAddress.setBearingCompass("W");
        quay.setRoadAddress(roadAddress);
        quayRepository.save(quay);

        Quay actualQuay = quayRepository.findOne(quay.getId());

        assertThat(actualQuay.getRoadAddress()).isNotNull();
        assertThat(actualQuay.getRoadAddress().getId()).isEqualTo(quay.getRoadAddress().getId());
        assertThat(actualQuay.getRoadAddress().getVersion()).isEqualTo(quay.getRoadAddress().getVersion());
        assertThat(actualQuay.getRoadAddress().getRoadName().getValue()).isEqualTo(quay.getRoadAddress().getRoadName().getValue());
        assertThat(actualQuay.getRoadAddress().getBearingCompass()).isEqualTo(quay.getRoadAddress().getBearingCompass());
    }

    @Test
    public void persistQuayWithCentroid() {
        Quay quay = new Quay();
        Location location = new Location();
        BigDecimal longitude = new BigDecimal("-0.2068758371").setScale(10, BigDecimal.ROUND_CEILING);
        BigDecimal latitude = new BigDecimal("51.4207729447").setScale(10, BigDecimal.ROUND_CEILING);

        location.setLongitude(longitude);
        location.setLatitude(latitude);

        SimplePoint_VersionStructure centroid = new SimplePoint_VersionStructure();
        centroid.setLocation(location);
        quay.setCentroid(centroid);

        quayRepository.save(quay);
        Quay actualQuay = quayRepository.findOne(quay.getId());

        assertThat(actualQuay).isNotNull();
        assertThat(actualQuay.getCentroid()).isNotNull();
        assertThat(actualQuay.getCentroid().getLocation()).isNotNull();
        assertThat(actualQuay.getCentroid().getLocation().getLatitude()).isEqualTo(latitude);
        assertThat(actualQuay.getCentroid().getLocation().getLongitude()).isEqualTo(longitude);
    }

    @Test
    public void persistQuayWithAccessibilityAssessment() {
        Quay quay = new Quay();

        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();
        accessibilityAssessment.setVersion("any");
        accessibilityAssessment.setMobilityImpairedAccess(LimitationStatusEnumeration.TRUE);
        quay.setAccessibilityAssessment(accessibilityAssessment);

        quayRepository.save(quay);
        Quay actualQuay = quayRepository.findOne(quay.getId());

        assertThat(actualQuay.getAccessibilityAssessment()).isNotNull();
        AccessibilityAssessment actualAccessibilityAssessment = actualQuay.getAccessibilityAssessment();

        assertThat(actualAccessibilityAssessment.getVersion()).isEqualTo(accessibilityAssessment.getVersion());
        assertThat(actualAccessibilityAssessment.getMobilityImpairedAccess()).isEqualTo(actualAccessibilityAssessment.getMobilityImpairedAccess());
        assertThat(actualAccessibilityAssessment.getId()).isEqualTo(actualAccessibilityAssessment.getId());
    }

    @Test
    public void persistQuayWithLevelReference() {
        Quay quay = new Quay();

        LevelRefStructure levelRefStructure = new LevelRefStructure();
        levelRefStructure.setVersion("001");
        levelRefStructure.setRef("tbd:Level:9100WIMBLDN_Lvl_ST");

        quay.setLevelRef(levelRefStructure);

        quayRepository.save(quay);
        Quay actualQuay = quayRepository.findOne(quay.getId());

        assertThat(actualQuay.getLevelRef()).isNotNull();
        assertThat(actualQuay.getLevelRef().getRef()).isEqualTo(levelRefStructure.getRef());
    }

    @Test
    public void persistQuayWithSiteReference() {
        Quay quay = new Quay();

        SiteRefStructure siteRefStructure = new SiteRefStructure();
        siteRefStructure.setVersion("001");
        siteRefStructure.setRef("napt:StopPlace:490G00272P");

        quay.setSiteRef(siteRefStructure);

        quayRepository.save(quay);
        Quay actualQuay = quayRepository.findOne(quay.getId());

        assertThat(actualQuay.getSiteRef()).isNotNull();
        assertThat(actualQuay.getSiteRef().getRef()).isEqualTo(siteRefStructure.getRef());
    }

}