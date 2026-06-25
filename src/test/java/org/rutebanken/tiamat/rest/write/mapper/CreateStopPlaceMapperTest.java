package org.rutebanken.tiamat.rest.write.mapper;

import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that the second StopPlace.class classMap registration (lines 115-126 of
 * CreateStopPlaceMapper) does not overwrite the netexId/version exclusions set
 * up in the first registration (lines 106-113).
 *
 * If the second registration silently replaces the first, netexId and version
 * from the user payload would be copied into the new StopPlace, breaking the
 * contract that a user cannot supply their own IDs or version numbers on create.
 */
public class CreateStopPlaceMapperTest {

    private final CreateStopPlaceMapper mapper = new CreateStopPlaceMapper();

    @Test
    public void createCopy_doesNotCopyNetexIdFromPayload() {
        var payload = new StopPlace();
        payload.setNetexId("NSR:StopPlace:999");

        var copy = mapper.createCopy(payload, StopPlace.class);

        assertThat(copy.getNetexId())
                .as("netexId supplied in the payload must not be copied into the new StopPlace")
                .isNull();
    }

    @Test
    public void createCopy_doesNotCopyVersionFromPayload() {
        var payload = new StopPlace();
        payload.setVersion(42L);

        var copy = mapper.createCopy(payload, StopPlace.class);

        assertThat(copy.getVersion())
                .as("version supplied in the payload must not be copied into the new StopPlace")
                .isNotEqualTo(42L);
    }

    @Test
    public void createCopy_doesNotCopyNetexIdFromQuayPayload() {
        var payload = new StopPlace();
        var quay = new Quay();
        quay.setNetexId("NSR:Quay:999");
        payload.getQuays().add(quay);

        var copy = mapper.createCopy(payload, StopPlace.class);

        assertThat(copy.getQuays()).hasSize(1);
        assertThat(copy.getQuays().iterator().next().getNetexId())
                .as("netexId supplied on a Quay in the payload must not be copied")
                .isNull();
    }

    @Test
    public void createCopy_doesNotCopyVersionFromQuayPayload() {
        var payload = new StopPlace();
        var quay = new Quay();
        quay.setVersion(7L);
        payload.getQuays().add(quay);

        var copy = mapper.createCopy(payload, StopPlace.class);

        assertThat(copy.getQuays()).hasSize(1);
        assertThat(copy.getQuays().iterator().next().getVersion())
                .as("version supplied on a Quay in the payload must not be copied")
                .isNotEqualTo(7L);
    }

    @Test
    public void createCopy_doesNotCopyNetexIdFromAccessibilityAssessmentPayload() {
        var payload = new StopPlace();
        var assessment = new AccessibilityAssessment();
        assessment.setNetexId("NSR:AccessibilityAssessment:999");
        payload.setAccessibilityAssessment(assessment);

        var copy = mapper.createCopy(payload, StopPlace.class);

        assertThat(copy.getAccessibilityAssessment()).isNotNull();
        assertThat(copy.getAccessibilityAssessment().getNetexId())
                .as("netexId supplied on AccessibilityAssessment in the payload must not be copied")
                .isNull();
    }
}
