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

package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.MappingContext;
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

    private MappingContext mappingContext = new MappingContext(new HashMap<>());

    @Test
    public void convertFrom() throws Exception {

        Map<String, Value> keyValues = new HashMap<>();
        keyValues.put("key", new Value("value"));

        KeyListStructure keyValueStructure = keyValuesToKeyListConverter.convert(keyValues, keyListStructureType, mappingContext);
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

        KeyListStructure keyValueStructure = keyValuesToKeyListConverter.convert(keyValues, keyListStructureType, mappingContext);
        assertThat(keyValueStructure).isNull();
    }

}