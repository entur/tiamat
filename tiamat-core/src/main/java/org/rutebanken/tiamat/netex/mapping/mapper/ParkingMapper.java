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

import jakarta.xml.bind.JAXBElement;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.ParkingArea;
import org.rutebanken.netex.model.ParkingAreas_RelStructure;

import java.util.List;

public class ParkingMapper extends CustomMapper<Parking, org.rutebanken.tiamat.model.Parking> {

    @Override
    public void mapAtoB(Parking parking, org.rutebanken.tiamat.model.Parking parking2, MappingContext context) {
        super.mapAtoB(parking, parking2, context);
        if (parking.getParkingAreas() != null &&
                parking.getParkingAreas().getParkingAreaRefOrParkingArea_() != null &&
                !parking.getParkingAreas().getParkingAreaRefOrParkingArea_().isEmpty()) {
            List<org.rutebanken.tiamat.model.ParkingArea> parkingAreas = mapperFacade.mapAsList(parking.getParkingAreas().getParkingAreaRefOrParkingArea_(), org.rutebanken.tiamat.model.ParkingArea.class, context);
            if (!parkingAreas.isEmpty()) {
                parking2.setParkingAreas(parkingAreas);
            }
        }
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.Parking tiamatParking, Parking netexParking, MappingContext context) {
        super.mapBtoA(tiamatParking, netexParking, context);
        if (tiamatParking.getParkingAreas() != null &&
                !tiamatParking.getParkingAreas().isEmpty()) {

            List<ParkingArea> parkingAreas = mapperFacade.mapAsList(tiamatParking.getParkingAreas(), ParkingArea.class, context);
            final List<JAXBElement<ParkingArea>> wrappedParkingAreas = parkingAreas.stream()
                    .map(pa -> new ObjectFactory().createParkingArea(pa))
                    .toList();

            if (!parkingAreas.isEmpty()) {
                ParkingAreas_RelStructure parkingAreas_relStructure = new ParkingAreas_RelStructure();
                parkingAreas_relStructure.getParkingAreaRefOrParkingArea_().addAll(wrappedParkingAreas);

                netexParking.setParkingAreas(parkingAreas_relStructure);
            }
        }
    }
}
