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
import org.rutebanken.netex.model.VehicleModelRefStructure;
import org.rutebanken.tiamat.model.VersionOfObjectRefStructure;
import org.rutebanken.tiamat.model.vehicle.VehicleModel;
import org.rutebanken.tiamat.netex.mapping.NetexMappingException;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class VehicleModelRefConverter extends BidirectionalConverter<VehicleModelRefStructure, VehicleModel> {

    private static final Logger logger = LoggerFactory.getLogger(VehicleModelRefConverter.class);

    // TODO: a mapper or converter should ideally not use repositories
    private final ReferenceResolver resolver;

    @Autowired
    public VehicleModelRefConverter(ReferenceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public VehicleModel convertTo(VehicleModelRefStructure vehicleModelRefStructure, Type<VehicleModel> type, MappingContext mappingContext) {
        VehicleModel vehicleModel = resolver.resolve(new VersionOfObjectRefStructure(vehicleModelRefStructure.getRef(), vehicleModelRefStructure.getVersion()), VehicleModel.class);
        if(vehicleModel != null) {
            return vehicleModel;
        }
        throw new NetexMappingException("Cannot find vehicle model from ref: " +vehicleModelRefStructure.getRef());
    }

    @Override
    public VehicleModelRefStructure convertFrom(VehicleModel vehicleModel, Type<VehicleModelRefStructure> type, MappingContext mappingContext) {
        VehicleModelRefStructure vehicleModelRefStructure = new VehicleModelRefStructure()
                .withCreated(LocalDateTime.now())
                .withRef(vehicleModel.getNetexId())
                .withVersion(String.valueOf(vehicleModel.getVersion()));

        logger.debug("Mapped vehicle model ref structure: {}", vehicleModelRefStructure);

        return vehicleModelRefStructure;
    }
}
