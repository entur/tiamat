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
import org.rutebanken.tiamat.model.CoveredEnumeration;
import org.springframework.stereotype.Component;

/**
 * NeTEx 2.0 added {@code true}/{@code false} to {@code CoveredEnumeration}. Tiamat's own enum,
 * used on sites such as StopPlace and Quay, has no such values and the Nordic NeTEx profile does
 * not allow them there, so they are rejected on import. All other values map by name.
 */
@Component
public class SiteCoveredEnumerationConverter extends BidirectionalConverter<CoveredEnumeration, org.entur.netex2.model.CoveredEnumeration> {

    @Override
    public org.entur.netex2.model.CoveredEnumeration convertTo(CoveredEnumeration source, Type<org.entur.netex2.model.CoveredEnumeration> destinationType, MappingContext mappingContext) {
        return source != null ? org.entur.netex2.model.CoveredEnumeration.valueOf(source.name()) : null;
    }

    @Override
    public CoveredEnumeration convertFrom(org.entur.netex2.model.CoveredEnumeration source, Type<CoveredEnumeration> destinationType, MappingContext mappingContext) {
        if (source == null) {
            return null;
        }
        return switch (source) {
            case TRUE, FALSE -> throw new IllegalArgumentException("Covered value '" + source.value()
                    + "' is not supported here. The Nordic NeTEx profile allows covered, indoors, outdoors or mixed");
            default -> CoveredEnumeration.valueOf(source.name());
        };
    }
}
