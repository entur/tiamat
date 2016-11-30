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

public class KeyValuesToKeyListConverterTest {
    private final Type<KeyListStructure> keyListStructureType = new TypeBuilder<KeyListStructure>() {}.build();

    private KeyValuesToKeyListConverter keyValuesToKeyListConverter = new KeyValuesToKeyListConverter();

    @Test
    public void convertFrom() throws Exception {

        Map<String, Value> keyValues = new HashMap<>();
        keyValues.put("key", new Value("value"));

        KeyListStructure keyValueStructure = keyValuesToKeyListConverter.convert(keyValues, keyListStructureType);
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

        KeyListStructure keyValueStructure = keyValuesToKeyListConverter.convert(keyValues, keyListStructureType);
        assertThat(keyValueStructure).isNull();
    }

}