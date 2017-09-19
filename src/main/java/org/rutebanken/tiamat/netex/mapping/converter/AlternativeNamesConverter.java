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
import org.rutebanken.netex.model.AlternativeNames_RelStructure;
import org.rutebanken.tiamat.model.AlternativeName;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlternativeNamesConverter extends BidirectionalConverter<List<AlternativeName>, AlternativeNames_RelStructure> {
    @Override
    public AlternativeNames_RelStructure convertTo(List<AlternativeName> alternativeNames, Type<AlternativeNames_RelStructure> type, MappingContext mappingContext) {
        return null;
    }

    @Override
    public List<AlternativeName> convertFrom(AlternativeNames_RelStructure alternativeNames_relStructure, Type<List<AlternativeName>> type, MappingContext mappingContext) {
        return null;
    }
}
