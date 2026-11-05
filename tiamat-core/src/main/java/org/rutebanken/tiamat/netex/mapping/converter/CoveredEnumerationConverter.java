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
import org.entur.netex2.model.CoveredEnumeration;
import org.springframework.stereotype.Component;

/**
 * Since netex-java-model 3.0.0, {@code covered} (e.g. on CycleStorageEquipment) is a
 * {@link CoveredEnumeration} instead of a plain boolean. Tiamat's own model only ever stores a
 * boolean, as does the Nordic NeTEx profile, so only {@code TRUE}/{@code FALSE} are accepted on
 * import and any other value is rejected.
 */
@Component
public class CoveredEnumerationConverter extends BidirectionalConverter<Boolean, CoveredEnumeration> {

    @Override
    public CoveredEnumeration convertTo(Boolean source, Type<CoveredEnumeration> destinationType, MappingContext mappingContext) {
        if (source == null) {
            return null;
        }
        return source ? CoveredEnumeration.TRUE : CoveredEnumeration.FALSE;
    }

    @Override
    public Boolean convertFrom(CoveredEnumeration source, Type<Boolean> destinationType, MappingContext mappingContext) {
        if (source == null) {
            return null;
        }
        return switch (source) {
            case TRUE -> Boolean.TRUE;
            case FALSE -> Boolean.FALSE;
            default -> throw new IllegalArgumentException("Covered value '" + source.value()
                    + "' is not supported here. The Nordic NeTEx profile allows only true or false");
        };
    }
}
