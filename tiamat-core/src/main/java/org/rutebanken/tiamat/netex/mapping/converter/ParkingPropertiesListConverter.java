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
import org.rutebanken.netex.model.ParkingCapacities_RelStructure;
import org.rutebanken.netex.model.ParkingCapacity;
import org.rutebanken.netex.model.ParkingProperties_RelStructure;
import org.rutebanken.netex.model.ParkingUserEnumeration;
import org.rutebanken.tiamat.model.ParkingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ParkingPropertiesListConverter extends BidirectionalConverter<List<ParkingProperties>, ParkingProperties_RelStructure> {

    private static final Logger logger = LoggerFactory.getLogger(ParkingPropertiesListConverter.class);

    @Override
    public ParkingProperties_RelStructure convertTo(List<ParkingProperties> parkingPropertiesList, Type<ParkingProperties_RelStructure> destinationType, MappingContext mappingContext) {
        if(parkingPropertiesList == null || parkingPropertiesList.isEmpty()) {
            return null;
        }
        ParkingProperties_RelStructure parkingProperties_relStructure = new ParkingProperties_RelStructure();
        ParkingCapacities_RelStructure parkingCapacities_relStructure = new ParkingCapacities_RelStructure();

        logger.debug("Mapping {} parkingPropertiesList to netex", parkingPropertiesList.size());

        parkingPropertiesList.forEach(parkingProperties -> {
            List<ParkingCapacity> parkingCapacityList = new ArrayList<>();
            parkingProperties.getSpaces().forEach(
                    space -> {
                        ParkingCapacity parkingCapacity = new ParkingCapacity();
                        ParkingUserEnumeration netexParkingUserType = mapperFacade.map(space.getParkingUserType(), ParkingUserEnumeration.class);
                        parkingCapacity.withParkingUserType(netexParkingUserType);
                        parkingCapacity.withNumberOfSpaces(space.getNumberOfSpaces());
                        parkingCapacity.withNumberOfSpacesWithRechargePoint(space.getNumberOfSpacesWithRechargePoint());
                        parkingCapacity.setId(space.getNetexId());
                        parkingCapacity.setVersion(String.valueOf(space.getVersion()));
                        parkingCapacityList.add(parkingCapacity);

                    }
            );
           final List<ParkingUserEnumeration> parkingUserEnumerations = mapperFacade.mapAsList(parkingProperties.getParkingUserTypes(), ParkingUserEnumeration.class);
            parkingCapacities_relStructure.getParkingCapacityRefOrParkingCapacity().addAll(parkingCapacityList);
            final org.rutebanken.netex.model.ParkingProperties netexParkingProperties = new org.rutebanken.netex.model.ParkingProperties();
            netexParkingProperties.setId(parkingProperties.getNetexId());
            netexParkingProperties.setVersion(String.valueOf(parkingProperties.getVersion()));
            netexParkingProperties.getParkingUserTypes().addAll(parkingUserEnumerations);
            netexParkingProperties.withSpaces(parkingCapacities_relStructure);
            parkingProperties_relStructure.getParkingProperties().add(netexParkingProperties);
        });
        return parkingProperties_relStructure;
    }

    @Override
    public List<ParkingProperties> convertFrom(ParkingProperties_RelStructure parkingProperties_relStructure, Type<List<ParkingProperties>> destinationType, MappingContext mappingContext) {
        logger.debug("Mapping {} quays to internal model", parkingProperties_relStructure != null ? parkingProperties_relStructure.getParkingProperties().size() : 0);
        List<ParkingProperties> parkingPropertiesList = new ArrayList<>();
        if (parkingProperties_relStructure != null && parkingProperties_relStructure.getParkingProperties() != null) {
            parkingProperties_relStructure.getParkingProperties().stream()
                    .map(netexParkingProperty -> {
                        ParkingProperties parkingProperties = new ParkingProperties();
                        parkingProperties.setNetexId(netexParkingProperty.getId());
                        parkingProperties.setVersion(Integer.parseInt(netexParkingProperty.getVersion()));
                        final List<org.rutebanken.tiamat.model.ParkingUserEnumeration> parkingUserEnumeration = mapperFacade.mapAsList(netexParkingProperty.getParkingUserTypes(), org.rutebanken.tiamat.model.ParkingUserEnumeration.class);

                        parkingProperties.getParkingUserTypes().addAll(parkingUserEnumeration);
                        List<org.rutebanken.tiamat.model.ParkingCapacity> parkingCapacityList = new ArrayList<>();
                        netexParkingProperty.getSpaces().getParkingCapacityRefOrParkingCapacity().forEach(space -> {
                            org.rutebanken.tiamat.model.ParkingCapacity parkingCapacity = new org.rutebanken.tiamat.model.ParkingCapacity();
                            if (space instanceof ParkingCapacity netexParkingCapacity) {
                                parkingCapacity.setNetexId(netexParkingCapacity.getId());
                                parkingCapacity.setVersion(Integer.parseInt(netexParkingCapacity.getVersion()));
                                parkingCapacity.setNumberOfSpaces(netexParkingCapacity.getNumberOfSpaces());
                                parkingCapacity.setNumberOfSpacesWithRechargePoint(netexParkingCapacity.getNumberOfSpacesWithRechargePoint());
                                parkingCapacity.setParkingUserType(mapperFacade.map(netexParkingCapacity.getParkingUserType(), org.rutebanken.tiamat.model.ParkingUserEnumeration.class));
                            }
                            parkingCapacityList.add(parkingCapacity);
                        });
                        parkingProperties.setSpaces(parkingCapacityList);
                        return parkingProperties;
                    })
                    .forEach(parkingPropertiesList::add);
        }

        return parkingPropertiesList;
    }
}
