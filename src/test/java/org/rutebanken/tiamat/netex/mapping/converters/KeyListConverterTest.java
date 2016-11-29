package org.rutebanken.tiamat.netex.mapping.converters;

import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.junit.Test;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.Value;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyListConverterTest {
    private final Type<KeyListStructure> keyListStructureType = new TypeBuilder<KeyListStructure>() {}.build();
    private final Type<Map<String, Value>> keyValueMapType = new TypeBuilder<Map<String, Value>>() {}.build();

    private KeyListConverter keyListConverter = new KeyListConverter();

    @Test
    public void convertTo() {
        KeyListStructure keyListStructure = new KeyListStructure()
                .withKeyValue(new KeyValueStructure()
                        .withKey("myKey")
                        .withValue("myValue"));
        Map<String, Value> keyValues = keyListConverter.convertTo(keyListStructure, keyValueMapType);
        assertThat(keyValues).containsKeys("myKey");
        assertThat(keyValues.get("myKey").getItems()).contains("myValue");
    }

    @Test
    public void convertToReturnsEmptyMap() {
        KeyListStructure keyListStructure = new KeyListStructure();
        Map<String, Value> keyValues = keyListConverter.convertTo(keyListStructure, keyValueMapType);
        assertThat(keyValues).isEmpty();
    }


    @Test
    public void convertFrom() throws Exception {

        Map<String, Value> keyValues = new HashMap<>();
        keyValues.put("key", new Value("value"));

        KeyListStructure keyValueStructure = keyListConverter.convertFrom(keyValues, keyListStructureType);
        assertThat(keyValueStructure.getKeyValue())
                .isNotEmpty()
                .extracting(KeyValueStructure::getKey).contains("key");
        assertThat(keyValueStructure.getKeyValue())
                .extracting(KeyValueStructure::getValue).contains("value");

    }

    /**
     * Expect null to avoid empty keylist in netex xml
     */
    @Test
    public void convertFromEmptyExpectsNull() throws Exception {
        Map<String, Value> keyValues = new HashMap<>();

        KeyListStructure keyValueStructure = keyListConverter.convertFrom(keyValues, keyListStructureType);
        assertThat(keyValueStructure).isNull();
    }

}