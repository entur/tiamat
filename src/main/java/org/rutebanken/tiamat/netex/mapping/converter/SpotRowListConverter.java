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
import org.rutebanken.netex.model.SpotRows_RelStructure;
import org.rutebanken.tiamat.model.vehicle.SpotRow;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SpotRowListConverter extends BidirectionalConverter<List<SpotRow>, SpotRows_RelStructure> {


    @Override
    public SpotRows_RelStructure convertTo(List<SpotRow> spotRows, Type<SpotRows_RelStructure> type, MappingContext mappingContext) {

        if(spotRows == null || spotRows.isEmpty()) {
            return null;
        }

        return new SpotRows_RelStructure()
                .withSpotRow(spotRows.stream()
                        .map(ds -> mapperFacade.map(ds, org.rutebanken.netex.model.SpotRow.class))
                        .collect(Collectors.toList()));
    }

    @Override
    public List<SpotRow> convertFrom(SpotRows_RelStructure spotRowsRelStructure, Type<List<SpotRow>> type, MappingContext mappingContext) {
        return null;
    }
}

