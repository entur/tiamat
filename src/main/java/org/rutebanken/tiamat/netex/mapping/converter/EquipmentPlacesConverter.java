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
import org.rutebanken.tiamat.model.EquipmentPlace;
import org.rutebanken.netex.model.EquipmentPlaces_RelStructure;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EquipmentPlacesConverter extends BidirectionalConverter<List<EquipmentPlace>, EquipmentPlaces_RelStructure> {

    @Override
    public EquipmentPlaces_RelStructure convertTo(List<EquipmentPlace> equipmentPlaces, Type<EquipmentPlaces_RelStructure> type, MappingContext mappingContext) {
        return null;
    }

    @Override
    public List<EquipmentPlace> convertFrom(EquipmentPlaces_RelStructure equipmentPlaces_relStructure, Type<List<EquipmentPlace>> type, MappingContext mappingContext) {
        return null;
    }
}
