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
import org.rutebanken.netex.model.ParkingStayEnumeration;
import org.rutebanken.netex.model.ParkingUserEnumeration;
import org.rutebanken.netex.model.ParkingVehicleEnumeration;
import org.rutebanken.tiamat.model.ParkingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ParkingPropertiesListConverter extends BidirectionalConverter<List<ParkingProperties>, ParkingProperties_RelStructure> {

    private static final Logger logger = LoggerFactory.getLogger(ParkingPropertiesListConverter.class);

    /**
     * Maps an enum constant to the matching constant of another enum, by name. Orika maps
     * enums the same way, through {@link Enum#valueOf}. Orika throws when the target enum
     * does not define the name. This method returns null instead.
     * <p>
     * The Tiamat parking enumerations that this converter reads mirror the NeTEx ones, so
     * no name is unmatched today. This method guards against a later NeTEx version that
     * adds a constant. An unknown name must drop a single attribute, and must not fail the
     * whole import. The guard covers this converter only. A parking enum that Orika maps by
     * default still fails an import on an unmatched name.
     *
     * @param source     the constant to map, or null
     * @param targetType the enum class to map into
     * @param recordId   the NeTEx id of the record, for the log line
     * @return the matching constant, or null when the source is null or has no match
     */
    static <T extends Enum<T>> T mapEnumByName(Enum<?> source, Class<T> targetType, String recordId) {
        if (source == null) {
            return null;
        }
        try {
            return Enum.valueOf(targetType, source.name());
        } catch (IllegalArgumentException e) {
            logger.warn("Parking record {}: dropped the value {}, because {} does not define it. "
                            + "The source type is {}.",
                    recordId, source.name(), targetType.getName(), source.getClass().getName());
            return null;
        }
    }

    /**
     * Maps each constant of a list to the matching constant of another enum, by name.
     * Drops a constant that the target enum does not define, under the rule that
     * {@link #mapEnumByName} states.
     *
     * @param source     the constants to map, or null
     * @param targetType the enum class to map into
     * @param recordId   the NeTEx id of the record, for the log line
     * @return the matching constants, never null
     */
    static <T extends Enum<T>> List<T> mapEnumListByName(List<? extends Enum<?>> source, Class<T> targetType, String recordId) {
        if (source == null) {
            return List.of();
        }
        return source.stream()
                .map(value -> mapEnumByName(value, targetType, recordId))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public ParkingProperties_RelStructure convertTo(List<ParkingProperties> parkingPropertiesList, Type<ParkingProperties_RelStructure> destinationType, MappingContext mappingContext) {
        if(parkingPropertiesList == null || parkingPropertiesList.isEmpty()) {
            return null;
        }
        ParkingProperties_RelStructure parkingProperties_relStructure = new ParkingProperties_RelStructure();

        logger.debug("Mapping {} parkingPropertiesList to netex", parkingPropertiesList.size());

        parkingPropertiesList.forEach(parkingProperties -> {
            // One relation structure per parking properties group. A structure shared across
            // the groups collects every capacity of every group, and each emitted group then
            // claims all of them.
            ParkingCapacities_RelStructure parkingCapacities_relStructure = new ParkingCapacities_RelStructure();
            List<ParkingCapacity> parkingCapacityList = new ArrayList<>();
            parkingProperties.getSpaces().forEach(
                    space -> {
                        ParkingCapacity parkingCapacity = new ParkingCapacity();
                        parkingCapacity.withParkingUserType(mapEnumByName(space.getParkingUserType(), ParkingUserEnumeration.class, space.getNetexId()));
                        parkingCapacity.withParkingVehicleType(mapEnumByName(space.getParkingVehicleType(), ParkingVehicleEnumeration.class, space.getNetexId()));
                        parkingCapacity.withParkingStayType(mapEnumByName(space.getParkingStayType(), ParkingStayEnumeration.class, space.getNetexId()));
                        parkingCapacity.withNumberOfSpaces(space.getNumberOfSpaces());
                        parkingCapacity.withNumberOfSpacesWithRechargePoint(space.getNumberOfSpacesWithRechargePoint());
                        parkingCapacity.setId(space.getNetexId());
                        parkingCapacity.setVersion(String.valueOf(space.getVersion()));
                        parkingCapacityList.add(parkingCapacity);

                    }
            );
           final List<ParkingUserEnumeration> parkingUserEnumerations = mapEnumListByName(parkingProperties.getParkingUserTypes(), ParkingUserEnumeration.class, parkingProperties.getNetexId());
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
                        final List<org.rutebanken.tiamat.model.ParkingUserEnumeration> parkingUserEnumeration = mapEnumListByName(netexParkingProperty.getParkingUserTypes(), org.rutebanken.tiamat.model.ParkingUserEnumeration.class, netexParkingProperty.getId());

                        parkingProperties.getParkingUserTypes().addAll(parkingUserEnumeration);
                        List<org.rutebanken.tiamat.model.ParkingCapacity> parkingCapacityList = new ArrayList<>();
                        netexParkingProperty.getSpaces().getParkingCapacityRefOrParkingCapacity().forEach(space -> {
                            org.rutebanken.tiamat.model.ParkingCapacity parkingCapacity = new org.rutebanken.tiamat.model.ParkingCapacity();
                            if (space instanceof ParkingCapacity netexParkingCapacity) {
                                parkingCapacity.setNetexId(netexParkingCapacity.getId());
                                parkingCapacity.setVersion(Integer.parseInt(netexParkingCapacity.getVersion()));
                                parkingCapacity.setNumberOfSpaces(netexParkingCapacity.getNumberOfSpaces());
                                parkingCapacity.setNumberOfSpacesWithRechargePoint(netexParkingCapacity.getNumberOfSpacesWithRechargePoint());
                                parkingCapacity.setParkingUserType(mapEnumByName(netexParkingCapacity.getParkingUserType(), org.rutebanken.tiamat.model.ParkingUserEnumeration.class, netexParkingCapacity.getId()));
                                parkingCapacity.setParkingVehicleType(mapEnumByName(netexParkingCapacity.getParkingVehicleType(), org.rutebanken.tiamat.model.ParkingVehicleEnumeration.class, netexParkingCapacity.getId()));
                                parkingCapacity.setParkingStayType(mapEnumByName(netexParkingCapacity.getParkingStayType(), org.rutebanken.tiamat.model.ParkingStayEnumeration.class, netexParkingCapacity.getId()));
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
