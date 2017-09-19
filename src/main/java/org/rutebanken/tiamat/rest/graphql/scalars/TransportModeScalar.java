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

package org.rutebanken.tiamat.rest.graphql.scalars;

import graphql.schema.*;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static graphql.schema.GraphQLEnumType.newEnum;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.*;

@Component
public class TransportModeScalar {

    private static Map<String, Object> validTransportModes;

    private static GraphQLEnumType filteredTransportModeEnumType;

    private static GraphQLEnumType filteredSubmodes;

    static {
        GraphQLEnumType.Builder filteredTransportModesTypeBuilder = newEnum()
                .name(TRANSPORT_MODE_TYPE);

        GraphQLEnumType.Builder filteredSubmodesBuilder = newEnum()
                .name(SUBMODE_TYPE);

        allVehiclesModesOfTransportationEnum.getValues()
                .stream()
                .filter(value -> includeEnumValue(value))
                .forEach(value -> filteredTransportModesTypeBuilder.value(value.getName(), value.getValue()));

        busSubmodeType.getValues()
                .stream()
                .filter(value -> includeEnumValue(VehicleModeEnumeration.BUS, value))
                .forEach(value -> filteredSubmodesBuilder.value(value.getName(), value.getValue()));
        tramSubmodeType.getValues()
                .stream()
                .filter(value -> includeEnumValue(VehicleModeEnumeration.TRAM, value))
                .forEach(value -> filteredSubmodesBuilder.value(value.getName(), value.getValue()));
        railSubmodeType.getValues()
                .stream()
                .filter(value -> includeEnumValue(VehicleModeEnumeration.RAIL, value))
                .forEach(value -> filteredSubmodesBuilder.value(value.getName(), value.getValue()));
        metroSubmodeType.getValues()
                .stream()
                .filter(value -> includeEnumValue(VehicleModeEnumeration.METRO, value))
                .forEach(value -> filteredSubmodesBuilder.value(value.getName(), value.getValue()));
        airSubmodeType.getValues()
                .stream()
                .filter(value -> includeEnumValue(VehicleModeEnumeration.AIR, value))
                .forEach(value -> filteredSubmodesBuilder.value(value.getName(), value.getValue()));
        waterSubmodeType.getValues()
                .stream()
                .filter(value -> includeEnumValue(VehicleModeEnumeration.WATER, value))
                .forEach(value -> filteredSubmodesBuilder.value(value.getName(), value.getValue()));
        cablewaySubmodeType.getValues()
                .stream()
                .filter(value -> includeEnumValue(VehicleModeEnumeration.CABLEWAY, value))
                .forEach(value -> filteredSubmodesBuilder.value(value.getName(), value.getValue()));
        funicularSubmodeType.getValues()
                .stream()
                .filter(value -> includeEnumValue(VehicleModeEnumeration.FUNICULAR, value))
                .forEach(value -> filteredSubmodesBuilder.value(value.getName(), value.getValue()));

        filteredTransportModeEnumType = filteredTransportModesTypeBuilder.build();
        filteredSubmodes = filteredSubmodesBuilder.build();
    }

    private static boolean includeEnumValue(GraphQLEnumValueDefinition transportModeValue) {
        Map<String, Object> validTransportModes = getConfiguredTransportModes();

        return (validTransportModes != null && validTransportModes.containsKey(transportModeValue.getName()));
    }

    private static boolean includeEnumValue(VehicleModeEnumeration transportMode, GraphQLEnumValueDefinition subModeValue) {
        Map<String, Object> validTransportModes = getConfiguredTransportModes();

        if (validTransportModes != null && validTransportModes.containsKey(transportMode.value())) {
            Object modes = validTransportModes.get(transportMode.value());
            return (modes instanceof List && ((List)modes).contains(subModeValue.getName()));
        }
        return false;
    }

    public static List<String> getValidSubmodes(String transportMode) {
        return (List<String>) getConfiguredTransportModes().get(transportMode);
    }

    public static Map<String, Object> getConfiguredTransportModes() {
        if (validTransportModes == null) {
            YamlMapFactoryBean yaml = new YamlMapFactoryBean();
            yaml.setResources(new ClassPathResource("transportmodes.yml"));
            validTransportModes = yaml.getObject();
        }
        return validTransportModes;
    }

    public List<GraphQLFieldDefinition> getTransportModeFieldsList() {
        List<GraphQLFieldDefinition> transportModeFieldList = new ArrayList<>();

        transportModeFieldList.add(newFieldDefinition()
                .name(TRANSPORT_MODE)
                .type(filteredTransportModeEnumType)
                .build());

        transportModeFieldList.add(newFieldDefinition()
                .name(SUBMODE)
                .type(filteredSubmodes)
                .dataFetcher(env -> resolveSubmode(env))
                .build());

        return transportModeFieldList;
    }

    private Object resolveSubmode(DataFetchingEnvironment env) {
        Object o = env.getSource();
        if (o instanceof StopPlace) {
            StopPlace stopPlace = (StopPlace) o;
            VehicleModeEnumeration mode = stopPlace.getTransportMode();
            if (mode != null) {
                switch (mode) {
                    case BUS:
                        return stopPlace.getBusSubmode();
                    case TRAM:
                        return stopPlace.getTramSubmode();
                    case RAIL:
                        return stopPlace.getRailSubmode();
                    case METRO:
                        return stopPlace.getMetroSubmode();
                    case AIR:
                        return stopPlace.getAirSubmode();
                    case WATER:
                        return stopPlace.getWaterSubmode();
                    case CABLEWAY:
                        return stopPlace.getTelecabinSubmode();
                    case FUNICULAR:
                        return stopPlace.getFunicularSubmode();
                }
            }
        }
        return null;
    }

    public List<GraphQLInputObjectField> createTransportModeInputFieldsList() {

        List<GraphQLInputObjectField> inputFields = new ArrayList<>();
        inputFields.add(newInputObjectField()
                .name(TRANSPORT_MODE)
                .type(filteredTransportModeEnumType)
                .build()
        );
        inputFields.add(newInputObjectField()
                        .name(SUBMODE)
                        .type(filteredSubmodes)
                        .build()
        );

        return inputFields;
    }
}
