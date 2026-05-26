package org.rutebanken.tiamat.rest.write;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.PostalAddress;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.model.Value;
import org.rutebanken.tiamat.rest.write.mapper.CreateStopPlaceMapper;
import org.rutebanken.tiamat.rest.write.mapper.UpdateStopPlaceMapper;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class StopPlaceUpdaterTest {

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
        original.setVersion(1L);
        var originalPostalAddress = new PostalAddress();
        originalPostalAddress.setPostCode("1234");
        original.setPostalAddress(originalPostalAddress);

        var update = new StopPlace();
        var editedPostalAddress = new PostalAddress();
        editedPostalAddress.setPostCode("5678");
        update.setPostalAddress(editedPostalAddress);

        var result = stopPlaceUpdater.update(original, update);

        assertThat(result.getVersion()).isEqualTo(2L);
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

}
