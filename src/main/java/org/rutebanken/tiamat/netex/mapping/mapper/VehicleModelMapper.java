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
import org.rutebanken.netex.model.TransportTypeRefStructure;
import org.rutebanken.netex.model.VehicleModel;
import org.rutebanken.netex.model.VehicleModel;

public class VehicleModelMapper extends CustomMapper<VehicleModel, org.rutebanken.tiamat.model.vehicle.VehicleModel> {

    final ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public void mapAtoB(VehicleModel VehicleModel, org.rutebanken.tiamat.model.vehicle.VehicleModel VehicleModel2, MappingContext context) {
        super.mapAtoB(VehicleModel, VehicleModel2, context);

    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.vehicle.VehicleModel tiamatVehicleModel, VehicleModel netexVehicleModel, MappingContext context) {
        super.mapBtoA(tiamatVehicleModel, netexVehicleModel, context);

        if (tiamatVehicleModel.getTransportTypeRef() != null) {
            JAXBElement<TransportTypeRefStructure> transportTypeRef = objectFactory.createTransportTypeRef(new TransportTypeRefStructure().withRef(tiamatVehicleModel.getTransportTypeRef()));
            netexVehicleModel.withTransportTypeRef(transportTypeRef);
        }
    }
}
