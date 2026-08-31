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
import org.rutebanken.netex.model.PublicCodeStructure;
import org.springframework.stereotype.Component;

/**
 * Since netex-java-model 3.0.0, {@code PublicCode} (e.g. on Quay) is a structured
 * {@link PublicCodeStructure} instead of a plain string. Tiamat's own model only ever stores a
 * plain string, so we bridge the two explicitly rather than relying on Orika's default handling,
 * which otherwise falls back to a lossy/incorrect conversion (e.g. via toString()).
 */
@Component
public class PublicCodeStructureConverter extends BidirectionalConverter<String, PublicCodeStructure> {

    @Override
    public PublicCodeStructure convertTo(String source, Type<PublicCodeStructure> destinationType, MappingContext mappingContext) {
        if (source == null) {
            return null;
        }
        return new PublicCodeStructure().withValue(source);
    }

    @Override
    public String convertFrom(PublicCodeStructure source, Type<String> destinationType, MappingContext mappingContext) {
        return source != null ? source.getValue() : null;
    }
}
