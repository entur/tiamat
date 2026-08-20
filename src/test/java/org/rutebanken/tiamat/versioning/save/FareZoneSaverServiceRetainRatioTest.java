package org.rutebanken.tiamat.versioning.save;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The prune retain floor is safety-critical and its effect is invisible in operation: a value that
 * cannot express a fraction disables the guard rather than announcing itself. It is therefore
 * rejected at startup.
 */
public class FareZoneSaverServiceRetainRatioTest {

    private FareZoneSaverService withRatio(double ratio) {
        return new FareZoneSaverService(null, null, null, null, null, null, ratio);
    }

    @Test
    public void acceptsFractionsWithinRange() {
        assertThatCode(() -> withRatio(0.0)).doesNotThrowAnyException();
        assertThatCode(() -> withRatio(0.5)).doesNotThrowAnyException();
        assertThatCode(() -> withRatio(1.0)).doesNotThrowAnyException();
    }

    /**
     * Every comparison against NaN is false, so the guard would never fire.
     */
    @Test
    public void rejectsNaN() {
        assertThatThrownBy(() -> withRatio(Double.NaN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fareZone.prune.minRetainRatio");
    }

    @Test
    public void rejectsInfinity() {
        assertThatThrownBy(() -> withRatio(Double.POSITIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fareZone.prune.minRetainRatio");
    }

    /**
     * A negative floor is unreachable from below, so it silently permits any prune.
     */
    @Test
    public void rejectsNegative() {
        assertThatThrownBy(() -> withRatio(-0.1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fareZone.prune.minRetainRatio");
    }

    /**
     * Above 1 no prune can ever satisfy the floor.
     */
    @Test
    public void rejectsAboveOne() {
        assertThatThrownBy(() -> withRatio(1.5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fareZone.prune.minRetainRatio");
    }
}
