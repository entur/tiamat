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
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.netex.mapping.NetexMultilingualStringHelper;
import org.springframework.stereotype.Component;

/**
 * Since netex-java-model 3.0.0, netex's {@link MultilingualString} holds a list of {@code Text}
 * elements instead of a single value, to support multiple language variants. Tiamat's
 * {@link EmbeddableMultilingualString} only ever stores a single value/lang pair, so Orika's
 * automatic property mapping (which relied on both sides exposing a matching value/lang getter
 * pair) no longer applies here; this converter bridges the two explicitly.
 */
@Component
public class MultilingualStringConverter extends BidirectionalConverter<EmbeddableMultilingualString, MultilingualString> {

    @Override
    public MultilingualString convertTo(EmbeddableMultilingualString source, Type<MultilingualString> destinationType, MappingContext mappingContext) {
        if (source == null || source.getValue() == null) {
            return null;
        }
        return NetexMultilingualStringHelper.toNetexModel(source.getValue(), source.getLang());
    }

    @Override
    public EmbeddableMultilingualString convertFrom(MultilingualString source, Type<EmbeddableMultilingualString> destinationType, MappingContext mappingContext) {
        String value = NetexMultilingualStringHelper.getValue(source);
        if (value == null) {
            return null;
        }
        return new EmbeddableMultilingualString(value, NetexMultilingualStringHelper.getLang(source));
    }
}
