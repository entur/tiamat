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

package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.PassengerCapacityStructure;

public class PassengerCapacityStructureMapper extends CustomMapper<PassengerCapacityStructure, org.rutebanken.tiamat.model.vehicle.PassengerCapacity> {

    @Override
    public void mapAtoB(PassengerCapacityStructure passengerCapacity, org.rutebanken.tiamat.model.vehicle.PassengerCapacity passengerCapacity2, MappingContext context) {
        super.mapAtoB(passengerCapacity, passengerCapacity2, context);

    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.vehicle.PassengerCapacity tiamatPassengerCapacity, PassengerCapacityStructure netexPassengerCapacity, MappingContext context) {
            super.mapBtoA(tiamatPassengerCapacity, netexPassengerCapacity, context);
    }
}
