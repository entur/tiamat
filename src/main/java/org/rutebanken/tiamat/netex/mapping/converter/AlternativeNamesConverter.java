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
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class AlternativeNamesConverter extends BidirectionalConverter<Set<AlternativeName>, AlternativeNames_RelStructure> {

    @Override
    public AlternativeNames_RelStructure convertTo(Set<AlternativeName> alternativeNames, Type<AlternativeNames_RelStructure> type, MappingContext mappingContext) {
        if (!CollectionUtils.isEmpty(alternativeNames)) {

            List<org.rutebanken.netex.model.AlternativeName> netexAlternativeNames = new ArrayList<>();

            for (org.rutebanken.tiamat.model.AlternativeName alternativeName : alternativeNames) {
                if (alternativeName != null
                        && alternativeName.getName() != null
                        && alternativeName.getName().getValue() != null
                        && !alternativeName.getName().getValue().isEmpty()) {
                    //Only include non-empty alternative names
                    org.rutebanken.netex.model.AlternativeName netexAlternativeName = new org.rutebanken.netex.model.AlternativeName();
                    mapperFacade.map(alternativeName, netexAlternativeName);
                    netexAlternativeName.setId(alternativeName.getNetexId());
                    netexAlternativeNames.add(netexAlternativeName);
                }
            }

            if (!netexAlternativeNames.isEmpty()) {
                AlternativeNames_RelStructure alternativeNamesRelStructure = new AlternativeNames_RelStructure();
                alternativeNamesRelStructure.getAlternativeName().addAll(netexAlternativeNames);
                return alternativeNamesRelStructure;
            }
        }
        return null;

    }

    @Override
    public Set<AlternativeName> convertFrom(AlternativeNames_RelStructure alternativeNames_relStructure, Type<Set<AlternativeName>> type, MappingContext mappingContext) {
        return null;
    }
}
