package org.rutebanken.tiamat.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Ignore;
import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
@Transactional
public class QuayTest {

    @Autowired
    public QuayRepository quayRepository;

    @Autowired
    private GeometryFactory geometryFactory;

    /**
     * Using example data from https://github.com/StichtingOpenGeo/NeTEx/blob/master/examples/functions/stopPlace/Netex_10_StopPlace_uk_ComplexStation_Wimbledon_1.xml
     *
     * @throws ParseException
     */
    @Test
    public void persistQuayWithCommonValues() throws ParseException {
        Quay quay = new Quay();
        quay.setVersion("001");
        quay.setCreated(ZonedDateTime.parse("2010-04-17T09:30:47Z"));
        quay.setDataSourceRef("nptg:DataSource:NaPTAN");
        quay.setResponsibilitySetRef("nptg:ResponsibilitySet:082");

        quay.setName(new EmbeddableMultilingualString("Wimbledon, Stop P", "en"));
        quay.setShortName(new EmbeddableMultilingualString("Wimbledon", "en"));
        quay.setDescription(new EmbeddableMultilingualString("Stop P  is paired with Stop C outside the station", "en"));

        quay.setCovered(CoveredEnumeration.COVERED);

        quay.setBoardingUse(true);
        quay.setAlightingUse(true);
        quay.setLabel(new MultilingualStringEntity("Stop P", "en"));

        quay.setQuayType(QuayTypeEnumeration.BUS_STOP);

        quayRepository.save(quay);

        Quay actualQuay = quayRepository.findOne(quay.getId());

        assertThat(actualQuay).isNotNull();
        assertThat(actualQuay.getId()).isEqualTo(quay.getId());
        String[] verifyColumns = new String[]{"id", "name.value", "version",
                "created", "shortName.value", "covered", "description.value",
                "label.value", "boardingUse", "quayType", "alightingUse"};
        assertThat(actualQuay).isEqualToComparingOnlyGivenFields(quay, verifyColumns);
    }

    @Test
    public void persistQuayWithCompassBearing() {
        Quay quay = new Quay();
        quay.setCompassBearing(new Float(0.01));
        quayRepository.save(quay);

        Quay actual = quayRepository.findOne(quay.getId());
        assertThat(actual.getCompassBearing()).isEqualTo(quay.getCompassBearing());
    }

    @Test
    public void persistQuayWithDestinations() {

        Quay quay = new Quay();
        DestinationDisplayView destinationDisplayView = new DestinationDisplayView();
        destinationDisplayView.setName(new MultilingualStringEntity("Towards London", "en"));

        quay.setDestinations(new ArrayList<>());
        quay.getDestinations().add(destinationDisplayView);

        quayRepository.save(quay);

        Quay actualQuay = quayRepository.findOne(quay.getId());

        Assertions.assertThat(actualQuay.getDestinations()).isNotEmpty();
        DestinationDisplayView actualDestinationDisplayView = actualQuay.getDestinations().get(0);
        assertThat(actualDestinationDisplayView.getName().getValue()).isEqualTo(destinationDisplayView.getName().getValue());
    }

    @Ignore
    @Test
    public void persistQuayWithRoadAddress() {
        Quay quay = new Quay();
        RoadAddress roadAddress = new RoadAddress();
        roadAddress.setVersion("any");
        roadAddress.setRoadName(new MultilingualStringEntity("Wimbledon Bridge", "en"));
        roadAddress.setBearingCompass("W");
        quay.setRoadAddress(roadAddress);
        quayRepository.save(quay);

        Quay actualQuay = quayRepository.findQuayDetailed(quay.getId());

        assertThat(actualQuay.getRoadAddress()).isNotNull();
        assertThat(actualQuay.getRoadAddress().getId()).isEqualTo(quay.getRoadAddress().getId());
        assertThat(actualQuay.getRoadAddress().getVersion()).isEqualTo(quay.getRoadAddress().getVersion());
        assertThat(actualQuay.getRoadAddress().getRoadName().getValue()).isEqualTo(quay.getRoadAddress().getRoadName().getValue());
        assertThat(actualQuay.getRoadAddress().getBearingCompass()).isEqualTo(quay.getRoadAddress().getBearingCompass());
    }

    @Test
    public void persistQuayWithCentroid() {
        Quay quay = new Quay();

        double longitude = 39.61441;
        double latitude = -144.22765;

        quay.setCentroid(geometryFactory.createPoint(new Coordinate(longitude, latitude)));

        quayRepository.save(quay);
        Quay actualQuay = quayRepository.findOne(quay.getId());

        assertThat(actualQuay).isNotNull();
        assertThat(actualQuay.getCentroid()).isNotNull();
        assertThat(actualQuay.getCentroid().getY()).isEqualTo(latitude);
        assertThat(actualQuay.getCentroid().getX()).isEqualTo(longitude);
    }

    @Test
    public void persistQuayWithMobilityImpairedAccessibilityAssessment() {
        Quay quay = new Quay();

        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();
        accessibilityAssessment.setVersion("any");
        accessibilityAssessment.setMobilityImpairedAccess(LimitationStatusEnumeration.TRUE);
        quay.setAccessibilityAssessment(accessibilityAssessment);

        quayRepository.save(quay);
        Quay actualQuay = quayRepository.findQuayDetailed(quay.getId());

        assertThat(actualQuay.getAccessibilityAssessment()).isNotNull();
        AccessibilityAssessment actualAccessibilityAssessment = actualQuay.getAccessibilityAssessment();

        assertThat(actualAccessibilityAssessment.getVersion()).isEqualTo(accessibilityAssessment.getVersion());
        assertThat(actualAccessibilityAssessment.getMobilityImpairedAccess()).isEqualTo(actualAccessibilityAssessment.getMobilityImpairedAccess());
        assertThat(actualAccessibilityAssessment.getId()).isEqualTo(actualAccessibilityAssessment.getId());
    }

    @Test
    public void persistQuayWithAccessibilityAssessmentLimitation() {
        Quay quay = new Quay();

        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();

        List<AccessibilityLimitation> accessibilityLimitations = new ArrayList<>();

        AccessibilityLimitation accessibilityLimitation = new AccessibilityLimitation();
        accessibilityLimitation.setWheelchairAccess(LimitationStatusEnumeration.TRUE);

        accessibilityLimitations.add(accessibilityLimitation);

        accessibilityAssessment.setLimitations(accessibilityLimitations);

        quay.setAccessibilityAssessment(accessibilityAssessment);

        quayRepository.save(quay);
        Quay actualQuay = quayRepository.findQuayDetailed(quay.getId());

        assertThat(actualQuay.getAccessibilityAssessment()).isNotNull();
        List<AccessibilityLimitation> actualAccessibilityLimitations = actualQuay.getAccessibilityAssessment().getLimitations();

        assertThat(actualAccessibilityLimitations).isNotEmpty();
        AccessibilityLimitation actualAccessibilityLimitation = actualAccessibilityLimitations.get(0);
        assertThat(actualAccessibilityLimitation.getWheelchairAccess()).isEqualTo(accessibilityLimitation.getWheelchairAccess());
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

    @Test
    public void persistQuayWithEquipmentPlaceThatContainsAnEquipmentPosition() {
        //arrange
        PointRefStructure pointRefStructure = new PointRefStructure();
        pointRefStructure.setRef("tbd:StopPlaceEntrance:9100WIMBLDN_5n6-EL1p");
        pointRefStructure.setVersion("001");

        EquipmentRefStructure equipmentRefStructure = new EquipmentRefStructure();
        equipmentRefStructure.setRef("tbd:WaitingRoomEquipment:4900ZZLUWIM3n4_Eq-Seats1");

        EquipmentPosition equipmentPosition = new EquipmentPosition();
        equipmentPosition.setDescription(new MultilingualStringEntity("Seats on Platform 1 and 2. \"0 metres from platform entrance", "en"));
        equipmentPosition.setReferencePointRef(pointRefStructure);
        equipmentPosition.setXOffset(new BigDecimal(1).setScale(2, BigDecimal.ROUND_HALF_UP));
        equipmentPosition.setYOffset(new BigDecimal(20).setScale(2, BigDecimal.ROUND_HALF_UP));

        List<EquipmentPosition> equipmentPositions = new ArrayList<>();
        equipmentPositions.add(equipmentPosition);

        EquipmentPlace equipmentPlace = new EquipmentPlace();
        equipmentPlace.setModification(ModificationEnumeration.NEW);
        equipmentPlace.setEquipmentPositions(equipmentPositions);
        List<EquipmentPlace> equipmentPlaces = new ArrayList<>();
        equipmentPlaces.add(equipmentPlace);

        Quay quay = new Quay();
        quay.setEquipmentPlaces(equipmentPlaces);

        // act
        quayRepository.save(quay);
        Quay actualQuay = quayRepository.findQuayDetailed(quay.getId());

        // assert
        assertThat(actualQuay.getEquipmentPlaces()).isNotNull();
        List<EquipmentPlace> actualEquipmentPlaces = actualQuay.getEquipmentPlaces();

        assertThat(actualEquipmentPlaces).isNotEmpty();
        EquipmentPlace actualEquipmentPlace = actualEquipmentPlaces.get(0);

        assertThat(actualEquipmentPlace.getEquipmentPositions()).isNotEmpty();

        EquipmentPosition actualEquipmentPosition = actualEquipmentPlace.getEquipmentPositions().get(0);
        assertThat(actualEquipmentPosition).isEqualToComparingOnlyGivenFields(equipmentPosition, "description.value", "xOffset", "yOffset");
        assertThat(actualEquipmentPosition.getReferencePointRef().getRef()).isEqualTo(pointRefStructure.getRef());
    }

    @Test
    public void persistQuayWithRelationToCheckConstraint() {
        Quay quay = new Quay();

        CheckConstraint checkConstraint = new CheckConstraint();
        checkConstraint.setName(new MultilingualStringEntity("Queue for Ticket Barrier", "en"));

        List<CheckConstraint> checkConstraints = new ArrayList<>();
        checkConstraints.add(checkConstraint);
        quay.setCheckConstraints(checkConstraints);

        quayRepository.save(quay);

        Quay actualQuay = quayRepository.findOne(quay.getId());

        assertThat(actualQuay.getCheckConstraints()).isNotNull();
        assertThat(actualQuay.getCheckConstraints()).isNotEmpty();

        CheckConstraint actualCheckConstraint = actualQuay.getCheckConstraints().get(0);
        assertThat(actualCheckConstraint.getName().getValue()).isEqualTo(checkConstraint.getName().getValue());
    }

    @Test
    public void persistQuayWithAlternativeName() {
        Quay quay = new Quay();

        AlternativeName alternativeName = new AlternativeName();
        alternativeName.setShortName(new MultilingualStringEntity("short name", "en"));
        alternativeName.setName(new MultilingualStringEntity("name", "en"));

        quay.getAlternativeNames().add(alternativeName);

        quayRepository.save(quay);

        Quay actualQuay = quayRepository.findQuayDetailed(quay.getId());
        assertThat(actualQuay.getAlternativeNames()).isNotEmpty();
        AlternativeName actualAlternativeName = actualQuay.getAlternativeNames().get(0);
        assertThat(actualAlternativeName.getId()).isEqualTo(actualAlternativeName.getId());
        assertThat(actualAlternativeName.getName().getValue()).isEqualTo(alternativeName.getName().getValue());
        assertThat(actualAlternativeName.getShortName().getValue()).isEqualTo(alternativeName.getShortName().getValue());
    }

    @Test
    public void persistQuayWithBoardingPosition() {
        BoardingPosition boardingPosition = new BoardingPosition();
        boardingPosition.setName(new EmbeddableMultilingualString("boarding position", "en"));
        boardingPosition.setPublicCode("A");

        Quay quay = new Quay();
        quay.getBoardingPositions().add(boardingPosition);

        quayRepository.save(quay);

        Quay actualQuay = quayRepository.findOne(quay.getId());

        assertThat(actualQuay.getBoardingPositions()).isNotEmpty();
    }

    @Test
    public void persistQuayWithParentQuayReference() {
        Quay quay = persistedQuayWithParentReference();
        Quay actualQuay = quayRepository.findOne(quay.getId());

        assertThat(actualQuay.getParentQuayRef()).isNotNull();
        assertThat(actualQuay.getParentQuayRef().getRef()).isEqualTo(quay.getParentQuayRef().getRef());
    }

    @Test
    public void orphanRemovalOfQuayReference() {
        Quay quay = persistedQuayWithParentReference();
        quay.setParentQuayRef(null);
        quayRepository.save(quay);

        Quay actualQuay = quayRepository.findOne(quay.getId());
        assertThat(actualQuay.getParentQuayRef()).isNull();
    }

    private Quay persistedQuayWithParentReference() {
        Quay quay = new Quay();

        QuayReference quayReference = new QuayReference();
        quayReference.setRef("id-to-parent-quay");
        quay.setParentQuayRef(quayReference);
        quayRepository.save(quay);

        return quay;
    }
}