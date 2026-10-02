package org.rutebanken.tiamat.netex.mapping.mapper;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ScheduledStopPointRefConverterTest {

    @Test
    public void scheduledStopPointRefInvertsToStopPlaceRef() {
        assertThat(ScheduledStopPointRefConverter.toStopPlaceRef("NSR:ScheduledStopPoint:S123"))
                .isEqualTo("NSR:StopPlace:123");
    }

    @Test
    public void scheduledStopPointRefWithoutSPrefixStillInverts() {
        assertThat(ScheduledStopPointRefConverter.toStopPlaceRef("NSR:ScheduledStopPoint:123"))
                .isEqualTo("NSR:StopPlace:123");
    }

    @Test
    public void stopPlaceRefPassesThrough() {
        assertThat(ScheduledStopPointRefConverter.toStopPlaceRef("NSR:StopPlace:123"))
                .isEqualTo("NSR:StopPlace:123");
    }

    @Test
    public void stopPlaceRefConvertsToScheduledStopPointRef() {
        assertThat(ScheduledStopPointRefConverter.toScheduledStopPointRef("NSR:StopPlace:123"))
                .isEqualTo("NSR:ScheduledStopPoint:S123");
    }

    @Test
    public void nullReturnsNull() {
        assertThat(ScheduledStopPointRefConverter.toStopPlaceRef(null)).isNull();
        assertThat(ScheduledStopPointRefConverter.toScheduledStopPointRef(null)).isNull();
    }

    @Test
    public void otherRefTypeReturnsNull() {
        assertThat(ScheduledStopPointRefConverter.toStopPlaceRef("NSR:Quay:123")).isNull();
    }

    @Test
    public void malformedRefThrows() {
        assertThatThrownBy(() -> ScheduledStopPointRefConverter.toStopPlaceRef("NSR:StopPlace"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
