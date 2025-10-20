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
import org.rutebanken.netex.model.*;

public class VehicleMapper extends CustomMapper<Vehicle, org.rutebanken.tiamat.model.vehicle.Vehicle> {

    final ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public void mapAtoB(Vehicle vehicle, org.rutebanken.tiamat.model.vehicle.Vehicle vehicle2, MappingContext context) {
        super.mapAtoB(vehicle, vehicle2, context);

    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.vehicle.Vehicle tiamatVehicle, Vehicle netexVehicle, MappingContext context) {
        super.mapBtoA(tiamatVehicle, netexVehicle, context);

        if (tiamatVehicle.getName() != null) {
            netexVehicle.getName().withContent(tiamatVehicle.getName().getValue());
        }

        if (tiamatVehicle.getShortName() != null) {
            netexVehicle.getShortName().withContent(tiamatVehicle.getShortName().getValue());
        }

        if (tiamatVehicle.getDescription() != null) {
            netexVehicle.getDescription().withContent(tiamatVehicle.getDescription().getValue());
        }

        if (tiamatVehicle.getTransportTypeRef() != null) {
            JAXBElement<TransportTypeRefStructure> transportTypeRef = objectFactory.createTransportTypeRef(new TransportTypeRefStructure().withRef(tiamatVehicle.getTransportTypeRef()));
            netexVehicle.withTransportTypeRef(transportTypeRef);
        }

        if (tiamatVehicle.getVehicleModelRef() != null) {
            netexVehicle.withVehicleModelRef(new VehicleModelRefStructure().withRef(tiamatVehicle.getVehicleModelRef()));
        }

    }
}
