package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.MappingContext;
import org.junit.Test;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.Value;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class KeyListToKeyValuesMapMapperTest {

    private final KeyListToKeyValuesMapMapper mapper = new KeyListToKeyValuesMapMapper();

    @Test
    public void mapKeyVal() {
        Map<String, Value> keyValues = new HashMap<>();

        KeyListStructure keyListStructure = new KeyListStructure()
                .withKeyValue(new KeyValueStructure()
                        .withKey("myKey")
                        .withValue("myValue"));
        mapper.mapAtoB(keyListStructure, keyValues, mock(MappingContext.class));
        assertThat(keyValues).containsKeys("myKey");
        assertThat(keyValues.get("myKey").getItems()).contains("myValue");
    }

    @Test
    public void mapEmpty() {
        Map<String, Value> keyValues = new HashMap<>();
        KeyListStructure keyListStructure = new KeyListStructure();
        mapper.mapAtoB(keyListStructure, keyValues, mock(MappingContext.class));
        assertThat(keyValues).isEmpty();
    }




}