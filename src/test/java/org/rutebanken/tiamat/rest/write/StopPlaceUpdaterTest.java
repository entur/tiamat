package org.rutebanken.tiamat.rest.write;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.AlternativeName;
import org.rutebanken.tiamat.model.CoveredEnumeration;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.InterchangeWeightingEnumeration;
import org.rutebanken.tiamat.model.LimitationStatusEnumeration;
import org.rutebanken.tiamat.model.NameTypeEnumeration;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.PostalAddress;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.model.TicketingEquipment;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.model.Value;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.rutebanken.tiamat.rest.write.mapper.CreateStopPlaceMapper;
import org.rutebanken.tiamat.rest.write.mapper.UpdateStopPlaceMapper;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class StopPlaceUpdaterTest {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    private final StopPlaceUpdater stopPlaceUpdater = new StopPlaceUpdater(
            new CreateStopPlaceMapper(),
            new UpdateStopPlaceMapper()
    );

    @Test
    public void ignoresUserSuppliedVersions() {
        var original = new StopPlace();
        original.setVersion(1L);

        var update = new StopPlace();
        update.setVersion(123L);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getVersion()).isEqualTo(1L);
    }

    @Test
    public void ignoresUserSuppliedNetexIds() {
        var original = new StopPlace();
        original.setNetexId("original-id");

        var update = new StopPlace();
        update.setNetexId("edited-id");

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getNetexId()).isEqualTo("original-id");
    }

    @Test
    public void ignoresUserSuppliedValidBetween() {
        var original = new StopPlace();
        var originalValidBetween = new ValidBetween(Instant.parse("2024-01-01T00:00:00Z"), null);
        original.setValidBetween(originalValidBetween);

        var update = new StopPlace();
        var editedValidBetween = new ValidBetween(Instant.parse("2025-01-01T00:00:00Z"), Instant.parse("2025-12-31T23:59:59Z"));
        update.setValidBetween(editedValidBetween);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getValidBetween().getFromDate()).isEqualTo(Instant.parse("2024-01-01T00:00:00Z"));
        assertThat(result.getValidBetween().getToDate()).isNull();
    }

    @Test
    public void mergesQuaysByNetexId() {
        var original = new StopPlace();
        var originalQuay = new Quay();
        originalQuay.setNetexId("quay-1");
        var originalName = new EmbeddableMultilingualString();
        originalName.setValue("Original quay");
        originalQuay.setName(originalName);
        original.getQuays().add(originalQuay);

        var update = new StopPlace();
        var editedQuay = new Quay();
        editedQuay.setNetexId("quay-1");
        var editedName = new EmbeddableMultilingualString();
        editedName.setValue("Edited quay");
        editedQuay.setName(editedName);
        update.getQuays().add(editedQuay);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getQuays()).hasSize(1);
        assertThat(result.getQuays().iterator().next().getName().getValue()).isEqualTo("Edited quay");
    }

    @Test
    public void throwsWhenEditedQuayHasNetexIdThatDoesNotExistInOriginal() {
        var original = new StopPlace();
        var originalQuay = new Quay();
        originalQuay.setNetexId("quay-1");
        original.getQuays().add(originalQuay);

        var update = new StopPlace();
        var editedQuay = new Quay();
        editedQuay.setNetexId("quay-123");
        update.getQuays().add(editedQuay);

        try {
            stopPlaceUpdater.update(original, update);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("Attempting to update Quay [id = quay-123] on StopPlace [id = null], but the quay does not exist on the stop place");
        }
    }

    @Test
    public void doesNotIncrementVersionIfPostalAddressIsUnchanged() {
        var original = new StopPlace();
        original.setVersion(1L);
        var originalPostalAddress = new PostalAddress();
        originalPostalAddress.setPostCode("1234");
        original.setPostalAddress(originalPostalAddress);

        var update = new StopPlace();
        var editedPostalAddress = new PostalAddress();
        editedPostalAddress.setPostCode("1234");
        update.setPostalAddress(editedPostalAddress);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getVersion()).isEqualTo(1L);
    }

    @Test
    public void doesIncrementVersionIfPostalAddressIsChanged() {
        var original = new StopPlace();
        var originalPostalAddress = new PostalAddress();
        originalPostalAddress.setVersion(1L);
        originalPostalAddress.setPostCode("1234");
        original.setPostalAddress(originalPostalAddress);

        var update = new StopPlace();
        var editedPostalAddress = new PostalAddress();
        editedPostalAddress.setPostCode("5678");
        update.setPostalAddress(editedPostalAddress);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getPostalAddress().getVersion()).isEqualTo(2L);
    }

    @Test
    public void setsAdjacentSites() {
        var original = new StopPlace();
        var adjacentSite1 = new SiteRefStructure();
        adjacentSite1.setRef("adjacent-site-1");
        original.getAdjacentSites().add(adjacentSite1);

        var update = new StopPlace();
        var adjacentSite2 = new SiteRefStructure();
        adjacentSite2.setRef("adjacent-site-2");
        update.getAdjacentSites().add(adjacentSite2);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getAdjacentSites()).hasSize(1);
        assertThat(result.getAdjacentSites().stream().findFirst().get().getRef()).isEqualTo("adjacent-site-2");
    }

    @Test
    public void setsTariffZones() {
        var original = new StopPlace();
        original.getTariffZones().add(new TariffZoneRef("tariff-zone-1"));

        var update = new StopPlace();
        update.getTariffZones().add(new TariffZoneRef("tariff-zone-2"));

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getTariffZones()).hasSize(1);
        assertThat(result.getTariffZones().iterator().next().getRef()).isEqualTo("tariff-zone-2");
    }

    @Test
    public void setsKeys() {
        var original = new StopPlace();
        original.getKeyValues().put("key1", new Value("value1"));

        var update = new StopPlace();
        update.getKeyValues().put("key2", new Value("value2"));

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getKeyValues()).hasSize(2); // imported-id gets included by netexmapper
        assertThat(result.getKeyValues().get("key1")).isNull();
        assertThat(result.getKeyValues().get("key2").getItems()).isEqualTo(Set.of("value2"));
    }

    @Test
    public void updatesName() {
        var original = new StopPlace();
        var originalName = new EmbeddableMultilingualString();
        originalName.setValue("Original name");
        original.setName(originalName);

        var update = new StopPlace();
        var editedName = new EmbeddableMultilingualString();
        editedName.setValue("Edited name");
        update.setName(editedName);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getName().getValue()).isEqualTo("Edited name");
    }

    @Test
    public void updatesDescription() {
        var original = new StopPlace();
        var originalDescription = new EmbeddableMultilingualString();
        originalDescription.setValue("Original description");
        original.setDescription(originalDescription);

        var update = new StopPlace();
        var editedDescription = new EmbeddableMultilingualString();
        editedDescription.setValue("Edited description");
        update.setDescription(editedDescription);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getDescription().getValue()).isEqualTo("Edited description");
    }

    @Test
    public void updatesStopPlaceType() {
        var original = new StopPlace();
        original.setStopPlaceType(StopTypeEnumeration.BUS_STATION);

        var update = new StopPlace();
        update.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getStopPlaceType()).isEqualTo(StopTypeEnumeration.RAIL_STATION);
    }

    @Test
    public void updatesTransportMode() {
        var original = new StopPlace();
        original.setTransportMode(VehicleModeEnumeration.BUS);

        var update = new StopPlace();
        update.setTransportMode(VehicleModeEnumeration.RAIL);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getTransportMode()).isEqualTo(VehicleModeEnumeration.RAIL);
    }

    @Test
    public void updatesWeighting() {
        var original = new StopPlace();
        original.setWeighting(InterchangeWeightingEnumeration.NO_INTERCHANGE);

        var update = new StopPlace();
        update.setWeighting(InterchangeWeightingEnumeration.PREFERRED_INTERCHANGE);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getWeighting()).isEqualTo(InterchangeWeightingEnumeration.PREFERRED_INTERCHANGE);
    }

    @Test
    public void updatesCovered() {
        var original = new StopPlace();
        original.setCovered(CoveredEnumeration.OUTDOORS);

        var update = new StopPlace();
        update.setCovered(CoveredEnumeration.COVERED);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getCovered()).isEqualTo(CoveredEnumeration.COVERED);
    }

    @Test
    public void updatesPublicCode() {
        var original = new StopPlace();
        original.setPublicCode("OLD");

        var update = new StopPlace();
        update.setPublicCode("NEW");

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getPublicCode()).isEqualTo("NEW");
    }

    @Test
    public void updatesCentroid() {
        var original = new StopPlace();
        original.setCentroid(GEOMETRY_FACTORY.createPoint(new Coordinate(10.0, 59.0)));

        var update = new StopPlace();
        update.setCentroid(GEOMETRY_FACTORY.createPoint(new Coordinate(10.5, 59.5)));

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getCentroid().getX()).isEqualTo(10.5);
        assertThat(result.getCentroid().getY()).isEqualTo(59.5);
    }

    @Test
    public void updatesAllAreasWheelchairAccessible() {
        var original = new StopPlace();
        original.setAllAreasWheelchairAccessible(false);

        var update = new StopPlace();
        update.setAllAreasWheelchairAccessible(true);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.isAllAreasWheelchairAccessible()).isTrue();
    }

    @Test
    public void updatesAccessibilityAssessment() {
        var original = new StopPlace();
        var originalAssessment = new AccessibilityAssessment();
        originalAssessment.setNetexId("NSR:AccessibilityAssessment:1");
        originalAssessment.setMobilityImpairedAccess(LimitationStatusEnumeration.UNKNOWN);
        original.setAccessibilityAssessment(originalAssessment);

        var update = new StopPlace();
        var editedAssessment = new AccessibilityAssessment();
        editedAssessment.setNetexId("NSR:AccessibilityAssessment:1");
        editedAssessment.setMobilityImpairedAccess(LimitationStatusEnumeration.TRUE);
        var limitation = new AccessibilityLimitation();
        limitation.setWheelchairAccess(LimitationStatusEnumeration.TRUE);
        editedAssessment.setLimitations(List.of(limitation));
        update.setAccessibilityAssessment(editedAssessment);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getAccessibilityAssessment().getMobilityImpairedAccess())
                .isEqualTo(LimitationStatusEnumeration.TRUE);
        assertThat(result.getAccessibilityAssessment().getLimitations()).hasSize(1);
        assertThat(result.getAccessibilityAssessment().getLimitations().getFirst().getWheelchairAccess())
                .isEqualTo(LimitationStatusEnumeration.TRUE);
    }

    @Test
    public void clearsAccessibilityAssessmentWhenEditedIsNull() {
        var original = new StopPlace();
        var originalAssessment = new AccessibilityAssessment();
        originalAssessment.setNetexId("NSR:AccessibilityAssessment:1");
        originalAssessment.setMobilityImpairedAccess(LimitationStatusEnumeration.TRUE);
        original.setAccessibilityAssessment(originalAssessment);

        var update = new StopPlace();
        // no accessibility assessment set on update

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getAccessibilityAssessment()).isNull();
    }

    @Test
    public void throwsWhenAccessibilityAssessmentNetexIdDoesNotMatch() {
        var original = new StopPlace();
        original.setNetexId("NSR:StopPlace:1");
        var originalAssessment = new AccessibilityAssessment();
        originalAssessment.setNetexId("NSR:AccessibilityAssessment:1");
        original.setAccessibilityAssessment(originalAssessment);

        var update = new StopPlace();
        var editedAssessment = new AccessibilityAssessment();
        editedAssessment.setNetexId("NSR:AccessibilityAssessment:999");
        update.setAccessibilityAssessment(editedAssessment);

        try {
            stopPlaceUpdater.update(original, update);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("NSR:AccessibilityAssessment:999")
                    .contains("NSR:StopPlace:1")
                    .contains("NSR:AccessibilityAssessment:1");
        }
    }

    @Test
    public void updatesAlternativeNames() {
        var original = new StopPlace();
        var originalAltName = new AlternativeName();
        originalAltName.setName(new EmbeddableMultilingualString("Gammel navn"));
        originalAltName.setNameType(NameTypeEnumeration.ALIAS);
        original.getAlternativeNames().add(originalAltName);

        var update = new StopPlace();
        var editedAltName = new AlternativeName();
        editedAltName.setName(new EmbeddableMultilingualString("Nytt navn"));
        editedAltName.setNameType(NameTypeEnumeration.TRANSLATION);
        update.getAlternativeNames().add(editedAltName);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getAlternativeNames()).hasSize(1);
        assertThat(result.getAlternativeNames().getFirst().getName().getValue()).isEqualTo("Nytt navn");
        assertThat(result.getAlternativeNames().getFirst().getNameType()).isEqualTo(NameTypeEnumeration.TRANSLATION);
    }

    @Test
    public void updatesParentSiteRef() {
        var original = new StopPlace();
        var originalRef = new SiteRefStructure();
        originalRef.setRef("NSR:StopPlace:1");
        original.setParentSiteRef(originalRef);

        var update = new StopPlace();
        var editedRef = new SiteRefStructure();
        editedRef.setRef("NSR:StopPlace:2");
        update.setParentSiteRef(editedRef);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getParentSiteRef().getRef()).isEqualTo("NSR:StopPlace:2");
    }

    @Test
    public void updatesInstalledEquipmentOnStopPlaceAndQuay() {
        var originalQuay = new Quay();
        originalQuay.setNetexId("quay-1");
        originalQuay.setName(new EmbeddableMultilingualString("quay with place equipments"));
        var originalQuayEquipment = new PlaceEquipment();
        originalQuayEquipment.getInstalledEquipment().add(new TicketingEquipment());
        originalQuay.setPlaceEquipments(originalQuayEquipment);

        var original = new StopPlace();
        original.getQuays().add(originalQuay);
        var originalStopEquipment = new PlaceEquipment();
        originalStopEquipment.setNetexId("NSR:PlaceEquipment:1");
        originalStopEquipment.setVersion(1L);
        originalStopEquipment.getInstalledEquipment().add(new TicketingEquipment());
        original.setPlaceEquipments(originalStopEquipment);

        var editedQuay = new Quay();
        editedQuay.setNetexId("quay-1");
        var editedQuayEquipment = new PlaceEquipment();
        editedQuayEquipment.setNetexId("NSR:PlaceEquipment:2");
        editedQuayEquipment.setVersion(3L);
        editedQuayEquipment.getInstalledEquipment().add(new TicketingEquipment());
        editedQuay.setPlaceEquipments(editedQuayEquipment);

        var update = new StopPlace();
        update.getQuays().add(editedQuay);
        var editedStopEquipment = new PlaceEquipment();
        editedStopEquipment.setNetexId("NSR:PlaceEquipment:1");
        editedStopEquipment.setVersion(2L);
        editedStopEquipment.getInstalledEquipment().add(new TicketingEquipment());
        update.setPlaceEquipments(editedStopEquipment);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getPlaceEquipments().getInstalledEquipment()).hasSize(1);
        // version is incremented by stopPlaceVersionSaverService,
        // so we should just pass the original version through the update
        assertThat(result.getPlaceEquipments().getVersion()).isEqualTo(1L);

        var resultQuay = result.getQuays().iterator().next();
        assertThat(resultQuay.getPlaceEquipments().getInstalledEquipment()).hasSize(1);
    }

    @Test
    public void clearsInstalledEquipmentWhenEditedIsNull() {
        var original = new StopPlace();
        var originalStopEquipment = new PlaceEquipment();
        originalStopEquipment.setNetexId("NSR:PlaceEquipment:1");
        originalStopEquipment.getInstalledEquipment().add(new TicketingEquipment());
        original.setPlaceEquipments(originalStopEquipment);

        var update = new StopPlace();
        // no place equipment set on update

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getPlaceEquipments()).isNull();
    }

    @Test
    public void throwsWhenPlaceEquipmentNetexIdDoesNotMatch() {
        var original = new StopPlace();
        original.setNetexId("NSR:StopPlace:1");
        var originalStopEquipment = new PlaceEquipment();
        originalStopEquipment.setNetexId("NSR:PlaceEquipment:1");
        original.setPlaceEquipments(originalStopEquipment);

        var update = new StopPlace();
        var editedStopEquipment = new PlaceEquipment();
        editedStopEquipment.setNetexId("NSR:PlaceEquipment:999");
        update.setPlaceEquipments(editedStopEquipment);

        assertThatThrownBy(() -> stopPlaceUpdater.update(original, update)).hasMessageContaining("NSR:PlaceEquipment:999")
                .hasMessageContaining("NSR:StopPlace:1")
                .hasMessageContaining("NSR:PlaceEquipment:1");
    }

    @Test
    public void preservesTopographicPlace() {
        var original = new StopPlace();
        var topographicPlace = new TopographicPlace();
        topographicPlace.setNetexId("NSR:TopographicPlace:1");
        original.setTopographicPlace(topographicPlace);

        var update = new StopPlace();
        var incomingTopographicPlace = new TopographicPlace();
        incomingTopographicPlace.setNetexId("NSR:TopographicPlace:99");
        update.setTopographicPlace(incomingTopographicPlace);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getTopographicPlace().getNetexId()).isEqualTo("NSR:TopographicPlace:1");
    }

}
