package org.rutebanken.tiamat.ext.fintraffic.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link FintrafficParkingEntranceForVehicles}, focused on the
 * {@code accessModes} raw-string / {@code List<String>} conversion helpers and
 * value-based {@code equals()}/{@code hashCode()}.
 */
class FintrafficParkingEntranceForVehiclesTest {

    @Test
    void getAccessModesList_parsesSpaceSeparatedTokens() {
        FintrafficParkingEntranceForVehicles entrance = new FintrafficParkingEntranceForVehicles(
                "Main", "door", null, null, true, false, "A1", "foot bicycle");

        assertThat(entrance.getAccessModesList()).containsExactly("foot", "bicycle");
    }

    @Test
    void getAccessModesList_nullAccessModes_returnsEmptyList() {
        FintrafficParkingEntranceForVehicles entrance = new FintrafficParkingEntranceForVehicles(
                "Main", "door", null, null, true, false, "A1");

        assertThat(entrance.getAccessModesList()).isEmpty();
    }

    @Test
    void setAccessModesList_joinsWithSpace() {
        FintrafficParkingEntranceForVehicles entrance = new FintrafficParkingEntranceForVehicles();

        entrance.setAccessModesList(List.of("foot", "bicycle"));

        assertThat(entrance.getAccessModes()).isEqualTo("foot bicycle");
    }

    @Test
    void setAccessModesList_emptyList_setsNull() {
        FintrafficParkingEntranceForVehicles entrance = new FintrafficParkingEntranceForVehicles();
        entrance.setAccessModes("foot");

        entrance.setAccessModesList(List.of());

        assertThat(entrance.getAccessModes()).isNull();
    }

    @Test
    void equalsAndHashCode_considerAccessModes() {
        FintrafficParkingEntranceForVehicles a = new FintrafficParkingEntranceForVehicles(
                "Main", "door", new BigDecimal("1.0"), new BigDecimal("2.0"), true, false, "A1", "foot");
        FintrafficParkingEntranceForVehicles b = new FintrafficParkingEntranceForVehicles(
                "Main", "door", new BigDecimal("1.0"), new BigDecimal("2.0"), true, false, "A1", "foot");
        FintrafficParkingEntranceForVehicles differentAccessModes = new FintrafficParkingEntranceForVehicles(
                "Main", "door", new BigDecimal("1.0"), new BigDecimal("2.0"), true, false, "A1", "bicycle");

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a).isNotEqualTo(differentAccessModes);
    }
}
