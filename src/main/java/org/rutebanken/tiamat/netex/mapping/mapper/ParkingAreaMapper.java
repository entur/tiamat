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
import org.rutebanken.netex.model.ParkingAreas_RelStructure;
import org.rutebanken.tiamat.model.ParkingArea;

public class ParkingAreaMapper extends CustomMapper<ParkingAreas_RelStructure, org.rutebanken.tiamat.model.ParkingArea> {

    @Override
    public void mapAtoB(ParkingAreas_RelStructure parkingAreas_relStructure, ParkingArea parkingArea, MappingContext context) {
        super.mapAtoB(parkingAreas_relStructure, parkingArea, context);
    }

    @Override
    public void mapBtoA(ParkingArea parkingArea, ParkingAreas_RelStructure parkingAreas_relStructure, MappingContext context) {
        super.mapBtoA(parkingArea, parkingAreas_relStructure, context);
    }
}
