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

package org.rutebanken.tiamat.service.merge;

import org.junit.Test;
import org.rutebanken.tiamat.model.Value;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class KeyValuesMergerTest {

    @Test
    public void testMergeKeyValues() {
        String fromOriginalId = "1234";
        String toOriginalId = "4321";
        String testKey = "test-key";
        String otherTestKey = "other-test-key";
        String testValue = "test";

        Map<String, Value> fromKeyValues = new HashMap<>();

        fromKeyValues.put(NetexIdMapper.ORIGINAL_ID_KEY, new Value(fromOriginalId));
        fromKeyValues.put(testKey, new Value(testValue));

        Map<String, Value> toKeyValues = new HashMap<>();
        toKeyValues.put(NetexIdMapper.ORIGINAL_ID_KEY, new Value(fromOriginalId, toOriginalId));
        toKeyValues.put(otherTestKey, new Value(testValue));

        new KeyValuesMerger().mergeKeyValues(fromKeyValues, toKeyValues);

        assertThat(toKeyValues).hasSize(3);
        assertThat(toKeyValues.get(NetexIdMapper.ORIGINAL_ID_KEY)).isNotNull();
        assertThat(toKeyValues.get(NetexIdMapper.ORIGINAL_ID_KEY).getItems()).hasSize(2);
        assertThat(toKeyValues.get(NetexIdMapper.ORIGINAL_ID_KEY).getItems()).contains(fromOriginalId, toOriginalId);
        assertThat(toKeyValues.get(testKey).getItems()).contains(testValue);
        assertThat(toKeyValues.get(otherTestKey).getItems()).contains(testValue);

    }
}