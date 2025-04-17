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

package org.rutebanken.tiamat.model;

import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class QuayTest extends TiamatIntegrationTest {

    /**
     * Using example data from https://github.com/StichtingOpenGeo/NeTEx/blob/master/examples/functions/stopPlace/Netex_10_StopPlace_uk_ComplexStation_Wimbledon_1.xml
     *
     * @throws ParseException
     */
    @Test
    public void persistQuayWithCommonValues() throws ParseException {
        Quay quay = new Quay();
        quay.setVersion(1L);
        quay.setCreated(Instant.parse("2010-04-17T09:30:47Z"));
        quay.setDataSourceRef("nptg:DataSource:NaPTAN");
        quay.setResponsibilitySetRef("nptg:ResponsibilitySet:082");

        quay.setName(new EmbeddableMultilingualString("Wimbledon, Stop P", "en"));
        quay.setShortName(new EmbeddableMultilingualString("Wimbledon", "en"));
        quay.setDescription(new EmbeddableMultilingualString("Stop P  is paired with Stop C outside the station", "en"));

        quay.setCovered(CoveredEnumeration.COVERED);
        quay.setLabel(new EmbeddableMultilingualString("Stop P", "en"));

        quayRepository.save(quay);

        Quay actualQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quay.getNetexId());
        assertThat(actualQuay).isNotNull();
        assertThat(actualQuay.getNetexId()).isEqualTo(quay.getNetexId());
        String[] verifyColumns = new String[]{"id", "name.value", "version",
                "created", "shortName.value", "covered", "description.value",
                "label.value"};
        assertThat(actualQuay).usingRecursiveComparison().isEqualTo(quay);
    }

    @Test
    public void persistQuayWithCompassBearing() {
        Quay quay = new Quay();
        quay.setCompassBearing((float) 0.01);
        quayRepository.save(quay);

        Quay actual = quayRepository.findFirstByNetexIdOrderByVersionDesc(quay.getNetexId());
        assertThat(actual.getCompassBearing()).isEqualTo(quay.getCompassBearing());
    }

    @Test
    public void persistQuayWithPrivateCode() {
        Quay quay = new Quay();

        quay.setPrivateCode(new PrivateCodeStructure("P01", "type"));
        quayRepository.save(quay);

        Quay actual = quayRepository.findFirstByNetexIdOrderByVersionDesc(quay.getNetexId());
        assertThat(actual.getPrivateCode()).isEqualTo(quay.getPrivateCode());
    }

    @Test
    public void persistQuayWithCentroid() {
        Quay quay = new Quay();

        double longitude = 39.61441;
        double latitude = -144.22765;

        quay.setCentroid(geometryFactory.createPoint(new Coordinate(longitude, latitude)));

        quayRepository.save(quay);
        Quay actualQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quay.getNetexId());

        assertThat(actualQuay).isNotNull();
        assertThat(actualQuay.getCentroid()).isNotNull();
        assertThat(actualQuay.getCentroid().getY()).isEqualTo(latitude);
        assertThat(actualQuay.getCentroid().getX()).isEqualTo(longitude);
    }

    @Test
    public void persistQuayWithMobilityImpairedAccessibilityAssessment() {
        Quay quay = new Quay();

        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();
        accessibilityAssessment.setVersion(1L);
        accessibilityAssessment.setMobilityImpairedAccess(LimitationStatusEnumeration.TRUE);
        quay.setAccessibilityAssessment(accessibilityAssessment);

        quayRepository.save(quay);
        Quay actualQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quay.getNetexId());

        assertThat(actualQuay.getAccessibilityAssessment()).isNotNull();
        AccessibilityAssessment actualAccessibilityAssessment = actualQuay.getAccessibilityAssessment();

        assertThat(actualAccessibilityAssessment.getVersion()).isEqualTo(accessibilityAssessment.getVersion());
        assertThat(actualAccessibilityAssessment.getMobilityImpairedAccess()).isEqualTo(actualAccessibilityAssessment.getMobilityImpairedAccess());
        assertThat(actualAccessibilityAssessment.getNetexId()).isEqualTo(actualAccessibilityAssessment.getNetexId());
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
        Quay actualQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quay.getNetexId());

        assertThat(actualQuay.getAccessibilityAssessment()).isNotNull();
        List<AccessibilityLimitation> actualAccessibilityLimitations = actualQuay.getAccessibilityAssessment().getLimitations();

        assertThat(actualAccessibilityLimitations).isNotEmpty();
        AccessibilityLimitation actualAccessibilityLimitation = actualAccessibilityLimitations.getFirst();
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
        Quay actualQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quay.getNetexId());

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
        Quay actualQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quay.getNetexId());

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
        equipmentPosition.setXOffset(new BigDecimal(1).setScale(2, RoundingMode.HALF_UP));
        equipmentPosition.setYOffset(new BigDecimal(20).setScale(2, RoundingMode.HALF_UP));

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
        Quay actualQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quay.getNetexId());

        // assert
        assertThat(actualQuay.getEquipmentPlaces()).isNotNull();
        List<EquipmentPlace> actualEquipmentPlaces = actualQuay.getEquipmentPlaces();

        assertThat(actualEquipmentPlaces).isNotEmpty();
        EquipmentPlace actualEquipmentPlace = actualEquipmentPlaces.getFirst();

        assertThat(actualEquipmentPlace.getEquipmentPositions()).isNotEmpty();

        EquipmentPosition actualEquipmentPosition = actualEquipmentPlace.getEquipmentPositions().getFirst();

        assertThat(actualEquipmentPlaces).usingRecursiveComparison().ignoringFields("id","version").isEqualTo(equipmentPlaces);
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

        Quay actualQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quay.getNetexId());

        assertThat(actualQuay.getCheckConstraints()).isNotNull();
        assertThat(actualQuay.getCheckConstraints()).isNotEmpty();

        CheckConstraint actualCheckConstraint = actualQuay.getCheckConstraints().getFirst();
        assertThat(actualCheckConstraint.getName().getValue()).isEqualTo(checkConstraint.getName().getValue());
    }

    @Test
    public void persistQuayWithAlternativeName() {
        Quay quay = new Quay();

        AlternativeName alternativeName = new AlternativeName();
        alternativeName.setShortName(new EmbeddableMultilingualString("short name", "en"));
        alternativeName.setName(new EmbeddableMultilingualString("name", "en"));

        quay.getAlternativeNames().add(alternativeName);

        quayRepository.save(quay);

        Quay actualQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quay.getNetexId());
        assertThat(actualQuay.getAlternativeNames()).isNotEmpty();
        AlternativeName actualAlternativeName = actualQuay.getAlternativeNames().getFirst();
        assertThat(actualAlternativeName.getNetexId()).isEqualTo(actualAlternativeName.getNetexId());
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

        Quay actualQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quay.getNetexId());

        assertThat(actualQuay.getBoardingPositions()).isNotEmpty();
    }

    @Test
    public void persistHSLExternalLinks() {
        Quay quay = new Quay();

        QuayExternalLink link1 = new QuayExternalLink();
        link1.setName("testName");
        link1.setLocation("www.hsl.fi");

        QuayExternalLink link2 = new QuayExternalLink();
        link2.setName("test2Name");
        link2.setLocation("www.hsl.fi");

        quay.setExternalLinks(List.of(link1, link2));

        quay = quayRepository.save(quay);

        Quay quayInRepository = quayRepository.findById(quay.getId()).orElseThrow();

        Assert.assertEquals(quayInRepository.externalLinks, quay.externalLinks);
    }
}
