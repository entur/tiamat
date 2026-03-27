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

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.netex.id.GaplessIdGeneratorService.INITIAL_LAST_ID;

/**
 * Tests for {@link GeneratedIdState} with configured ID ranges.
 * Validates that entities with a configured range get the range minimum as their initial last ID,
 * while entities without a configured range fall back to {@link GaplessIdGeneratorService#INITIAL_LAST_ID}.
 */
public class GeneratedIdStateRangeTest {

    private HazelcastInstance hazelcastInstance;

    @Before
    public void setUp() {
        Config config = new Config();
        config.setClusterName("generatedIdStateRangeTest-" + System.currentTimeMillis());
        hazelcastInstance = Hazelcast.newHazelcastInstance(config);
    }

    @After
    public void tearDown() {
        if (hazelcastInstance != null) {
            hazelcastInstance.shutdown();
        }
    }

    @Test
    public void entityWithConfiguredRangeSeedsWithMinMinusOne() {
        NetexIdRangeConfiguration rangeConfig = createRangeConfig();
        GeneratedIdState state = new GeneratedIdState(hazelcastInstance, rangeConfig);

        long lastId = state.getLastIdForEntity("StopPlace");

        // Seeded with min - 1 so that generate_series(lastId + 1, ...) starts exactly at min
        assertThat(lastId).isEqualTo(99L);
    }

    @Test
    public void quayWithConfiguredRangeSeedsWithMinMinusOne() {
        NetexIdRangeConfiguration rangeConfig = createRangeConfig();
        GeneratedIdState state = new GeneratedIdState(hazelcastInstance, rangeConfig);

        long lastId = state.getLastIdForEntity("Quay");

        assertThat(lastId).isEqualTo(499L);
    }

    @Test
    public void entityWithoutConfiguredRangeFallsBackToDefault() {
        NetexIdRangeConfiguration rangeConfig = createRangeConfig();
        GeneratedIdState state = new GeneratedIdState(hazelcastInstance, rangeConfig);

        long lastId = state.getLastIdForEntity("TopographicPlace");

        assertThat(lastId).isEqualTo(INITIAL_LAST_ID);
    }

    @Test
    public void emptyRangeConfigFallsBackToDefault() {
        NetexIdRangeConfiguration emptyConfig = new NetexIdRangeConfiguration();
        GeneratedIdState state = new GeneratedIdState(hazelcastInstance, emptyConfig);

        long lastId = state.getLastIdForEntity("StopPlace");

        assertThat(lastId).isEqualTo(INITIAL_LAST_ID);
    }

    private NetexIdRangeConfiguration createRangeConfig() {
        NetexIdRangeConfiguration config = new NetexIdRangeConfiguration();
        Map<String, NetexIdRangeConfiguration.IdRange> ranges = new HashMap<>();
        ranges.put("StopPlace", new NetexIdRangeConfiguration.IdRange(100, 200));
        ranges.put("Quay", new NetexIdRangeConfiguration.IdRange(500, 600));
        config.setRange(ranges);
        return config;
    }
}

