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