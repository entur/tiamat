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
import org.rutebanken.netex.model.TransportTypeRefStructure;
import org.rutebanken.tiamat.model.VersionOfObjectRefStructure;
import org.rutebanken.tiamat.model.vehicle.VehicleType;
import org.rutebanken.tiamat.netex.mapping.NetexMappingException;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransportTypeRefConverter extends BidirectionalConverter<TransportTypeRefStructure, VehicleType> {

    private static final Logger logger = LoggerFactory.getLogger(TransportTypeRefConverter.class);

    // TODO: a mapper or converter should ideally not use repositories
    private final ReferenceResolver resolver;

    @Autowired
    public TransportTypeRefConverter(ReferenceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public VehicleType convertTo(TransportTypeRefStructure vehicleTypeRefStructure, Type<VehicleType> type, MappingContext mappingContext) {
        VehicleType vehicleType = resolver.resolve(new VersionOfObjectRefStructure(vehicleTypeRefStructure.getRef(), vehicleTypeRefStructure.getVersion()), VehicleType.class);
        if(vehicleType != null) {
            return vehicleType;
        }
        throw new NetexMappingException("Cannot find vehicle type from ref: " +vehicleTypeRefStructure.getRef());
    }

    @Override
    public TransportTypeRefStructure convertFrom(VehicleType vehicleType, Type<TransportTypeRefStructure> type, MappingContext mappingContext) {
        TransportTypeRefStructure vehicleTypeRefStructure = new TransportTypeRefStructure()
                .withCreated(LocalDateTime.now())
                .withRef(vehicleType.getNetexId())
                .withVersion(String.valueOf(vehicleType.getVersion()));

        logger.debug("Mapped vehicle type ref structure: {}", vehicleTypeRefStructure);

        return vehicleTypeRefStructure;
    }
}
