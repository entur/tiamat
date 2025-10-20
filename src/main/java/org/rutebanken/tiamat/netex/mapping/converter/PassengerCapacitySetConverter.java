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
import org.rutebanken.netex.model.PassengerCapacities_RelStructure;
import org.rutebanken.tiamat.model.vehicle.PassengerCapacity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PassengerCapacitySetConverter extends BidirectionalConverter<Set<PassengerCapacity>, PassengerCapacities_RelStructure> {
    @Override
    public PassengerCapacities_RelStructure convertTo(Set<PassengerCapacity> capacities, Type<PassengerCapacities_RelStructure> type, MappingContext mappingContext) {

        if(capacities == null || capacities.isEmpty()) {
            return null;
        }

        return new PassengerCapacities_RelStructure()
                .withPassengerCapacityRefOrPassengerCapacityOrPassengerVehicleCapacity(capacities.stream()
                        .map(pc -> mapperFacade.map(pc, org.rutebanken.netex.model.PassengerCapacity.class))
                        .collect(Collectors.toSet()));
    }

    @Override
    public Set<PassengerCapacity> convertFrom(PassengerCapacities_RelStructure passengerCapacitiesRelStructure, Type<Set<PassengerCapacity>> type, MappingContext mappingContext) {
        return null;
        //        if(tariffZoneRefs_relStructure == null TODO
//                || tariffZoneRefs_relStructure.getTariffZoneRef() == null
//                || tariffZoneRefs_relStructure.getTariffZoneRef().isEmpty()) {
//            return null;
//        }

//        return tariffZoneRefs_relStructure
//                .getTariffZoneRef()
//                .stream()
//                .map(tariffZoneRef -> mapperFacade.map(tariffZoneRef, TariffZoneRef.class))
//                .collect(toSet());

    }
}

