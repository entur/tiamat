package uk.org.netex.netex;

import no.rutebanken.tiamat.TiamatApplication;
import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
    public void persistExampleQuay() throws ParseException {

        Quay quay = new Quay();
        quay.setVersion("001");
        quay.setCreated(dateFormat.parse("2010-04-17T09:30:47Z"));
        quay.setDataSourceRef("nptg:DataSource:NaPTAN");
        quay.setResponsibilitySetRef("nptg:ResponsibilitySet:082");
        quay.setId("napt:Quay:490000272P");

        quay.setName(new MultilingualString("Wimbledon, Stop P", "en", ""));
        quay.setShortName(new MultilingualString("Wimbledon", "en", ""));
        quay.setDescription(new MultilingualString("Stop P  is paired with Stop C outside the station", "en", ""));


        //quay.setTypes();

        location(quay);
        roadAddress(quay);
        accessibilityAssessment(quay);

        quay.setCovered(CoveredEnumeration.COVERED);

        //Reference to stop place
        SiteRefStructure siteRefStructure = new SiteRefStructure();
        siteRefStructure.setVersion("001");
        siteRefStructure.setValue("napt:StopPlace:490G00272P");


     //   LevelRefStructure levelRefStructure = new LevelRefStructure();
     //   levelRefStructure.setVersion("001");
     //   levelRefStructure.setChanged(new Date());


        quay.setBoardingUse(true);
        quay.setAlightingUse(true);
        quay.setLabel(new MultilingualString("Stop P", "en", ""));
        quay.setPublicCode("1-2345");
        //quay.setDestinationDisplayView

        quay.setCompassOctant(CompassBearing8Enumeration.W);
        quay.setQuayType(QuayTypeEnumeration.BUS_STOP);

        quayRepository.save(quay);

    }

    private void accessibilityAssessment(Quay quay) {
        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();
        accessibilityAssessment.setVersion("any");
        accessibilityAssessment.setId("tbd:AccessibilityAssessment:490000272P");
        accessibilityAssessment.setMobilityImpairedAccess(LimitationStatusEnumeration.TRUE);
        quay.setAccessibilityAssessment(accessibilityAssessment);
    }

    private void location(Quay quay) {
        LocationStructure location = new LocationStructure();
        location.setLongitude(new BigDecimal(-0.2068758371));
        location.setLatitude(new BigDecimal(51.4207729447));
        SimplePoint_VersionStructure simplePoint = new SimplePoint_VersionStructure();
        simplePoint.setLocation(location);
        quay.setCentroid(simplePoint);
    }

    private void roadAddress(Quay quay) {
        RoadAddress roadAddress = new RoadAddress();
        roadAddress.setId("tbd:RoadAddress:Rd_Addr_03");
        roadAddress.setVersion("any");
        roadAddress.setRoadName(new MultilingualString("Wimbledon Bridge", "en", ""));
        roadAddress.setBearingCompass("W");
        quay.setRoadAddress(roadAddress);
    }
}