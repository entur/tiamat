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

package org.rutebanken.tiamat.netex.id;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class NetexIdRangeConfigurationTest {

    @Test
    public void emptyConfigurationAllowsAnyId() {
        NetexIdRangeConfiguration config = new NetexIdRangeConfiguration();
        assertThat(config.isIdInRange("StopPlace", 1L)).isTrue();
        assertThat(config.isIdInRange("StopPlace", 999999999L)).isTrue();
        assertThat(config.isIdInRange("Quay", 1L)).isTrue();
    }

    @Test
    public void idWithinConfiguredRangeIsValid() {
        NetexIdRangeConfiguration config = createConfigWithRanges();
        assertThat(config.isIdInRange("StopPlace", 100L)).isTrue();
        assertThat(config.isIdInRange("StopPlace", 150L)).isTrue();
        assertThat(config.isIdInRange("StopPlace", 200L)).isTrue();
    }

    @Test
    public void idOutsideConfiguredRangeIsInvalid() {
        NetexIdRangeConfiguration config = createConfigWithRanges();
        assertThat(config.isIdInRange("StopPlace", 99L)).isFalse();
        assertThat(config.isIdInRange("StopPlace", 201L)).isFalse();
        assertThat(config.isIdInRange("StopPlace", 1L)).isFalse();
    }

    @Test
    public void quayRangeIsValidated() {
        NetexIdRangeConfiguration config = createConfigWithRanges();
        assertThat(config.isIdInRange("Quay", 500L)).isTrue();
        assertThat(config.isIdInRange("Quay", 550L)).isTrue();
        assertThat(config.isIdInRange("Quay", 600L)).isTrue();
        assertThat(config.isIdInRange("Quay", 499L)).isFalse();
        assertThat(config.isIdInRange("Quay", 601L)).isFalse();
    }

    @Test
    public void entityWithoutConfiguredRangeAllowsAnyId() {
        NetexIdRangeConfiguration config = createConfigWithRanges();
        assertThat(config.isIdInRange("TopographicPlace", 1L)).isTrue();
        assertThat(config.isIdInRange("TopographicPlace", Long.MAX_VALUE)).isTrue();
    }

    @Test
    public void getRangeForEntityReturnsEmptyWhenNotConfigured() {
        NetexIdRangeConfiguration config = createConfigWithRanges();
        assertThat(config.getRangeForEntity("TopographicPlace")).isEmpty();
    }

    @Test
    public void getRangeForEntityReturnsPresentWhenConfigured() {
        NetexIdRangeConfiguration config = createConfigWithRanges();
        Optional<NetexIdRangeConfiguration.IdRange> stopPlaceRange = config.getRangeForEntity("StopPlace");
        assertThat(stopPlaceRange).isPresent();
        assertThat(stopPlaceRange.get().getMin()).isEqualTo(100L);
        assertThat(stopPlaceRange.get().getMax()).isEqualTo(200L);
    }

    @Test
    public void stringOverloadValidatesNumericPostfix() {
        NetexIdRangeConfiguration config = createConfigWithRanges();
        assertThat(config.isIdInRange("StopPlace", "150")).isTrue();
        assertThat(config.isIdInRange("StopPlace", "1")).isFalse();
    }

    @Test
    public void stringOverloadRejectsNonNumericPostfixWhenRangeConfigured() {
        NetexIdRangeConfiguration config = createConfigWithRanges();
        assertThat(config.isIdInRange("StopPlace", "ABC")).isFalse();
    }

    @Test
    public void stringOverloadAllowsNonNumericPostfixWhenNoRangeConfigured() {
        NetexIdRangeConfiguration config = createConfigWithRanges();
        assertThat(config.isIdInRange("TopographicPlace", "ABC")).isTrue();
    }

    @Test
    public void idRangeBoundaryMinIsInclusive() {
        NetexIdRangeConfiguration.IdRange range = new NetexIdRangeConfiguration.IdRange(100, 200);
        assertThat(range.isInRange(100)).isTrue();
    }

    @Test
    public void idRangeBoundaryMaxIsInclusive() {
        NetexIdRangeConfiguration.IdRange range = new NetexIdRangeConfiguration.IdRange(100, 200);
        assertThat(range.isInRange(200)).isTrue();
    }

    private NetexIdRangeConfiguration createConfigWithRanges() {
        NetexIdRangeConfiguration config = new NetexIdRangeConfiguration();
        Map<String, NetexIdRangeConfiguration.IdRange> ranges = new HashMap<>();
        ranges.put("StopPlace", new NetexIdRangeConfiguration.IdRange(100, 200));
        ranges.put("Quay", new NetexIdRangeConfiguration.IdRange(500, 600));
        config.setRange(ranges);
        return config;
    }
}

