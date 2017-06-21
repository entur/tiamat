package org.rutebanken.tiamat.rest.graphql.scalars;

import graphql.schema.*;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
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

    private static GraphQLEnumType.Builder filteredTransportModesType = newEnum()
            .name(TRANSPORT_MODE_TYPE);

    private static GraphQLEnumType.Builder filteredSubmodes = newEnum()
            .name(SUBMODE_TYPE);

    static {
        allVehiclesModesOfTransportationEnum.getValues()
                .stream()
                .filter(value -> includeEnumValue(value))
                .forEach(value -> filteredTransportModesType.value(value.getName(), value.getValue()));

        busSubmodeType.getValues()
                .stream()
                .filter(value -> includeEnumValue(VehicleModeEnumeration.BUS, value))
                .forEach(value -> filteredSubmodes.value(value.getName(), value.getValue()));
        tramSubmodeType.getValues()
                .stream()
                .filter(value -> includeEnumValue(VehicleModeEnumeration.TRAM, value))
                .forEach(value -> filteredSubmodes.value(value.getName(), value.getValue()));
        railSubmodeType.getValues()
                .stream()
                .filter(value -> includeEnumValue(VehicleModeEnumeration.RAIL, value))
                .forEach(value -> filteredSubmodes.value(value.getName(), value.getValue()));
        metroSubmodeType.getValues()
                .stream()
                .filter(value -> includeEnumValue(VehicleModeEnumeration.METRO, value))
                .forEach(value -> filteredSubmodes.value(value.getName(), value.getValue()));
        airSubmodeType.getValues()
                .stream()
                .filter(value -> includeEnumValue(VehicleModeEnumeration.AIR, value))
                .forEach(value -> filteredSubmodes.value(value.getName(), value.getValue()));
        waterSubmodeType.getValues()
                .stream()
                .filter(value -> includeEnumValue(VehicleModeEnumeration.WATER, value))
                .forEach(value -> filteredSubmodes.value(value.getName(), value.getValue()));
        cablewaySubmodeType.getValues()
                .stream()
                .filter(value -> includeEnumValue(VehicleModeEnumeration.CABLEWAY, value))
                .forEach(value -> filteredSubmodes.value(value.getName(), value.getValue()));
        funicularSubmodeType.getValues()
                .stream()
                .filter(value -> includeEnumValue(VehicleModeEnumeration.FUNICULAR, value))
                .forEach(value -> filteredSubmodes.value(value.getName(), value.getValue()));
    }

    private static boolean includeEnumValue(GraphQLEnumValueDefinition transportModeValue) {
        Map<String, Object> validTransportModes = getTransportModes();

        return (validTransportModes != null && validTransportModes.containsKey(transportModeValue.getName()));
    }

    private static boolean includeEnumValue(VehicleModeEnumeration transportMode, GraphQLEnumValueDefinition subModeValue) {
        Map<String, Object> validTransportModes = getTransportModes();

        if (validTransportModes != null && validTransportModes.containsKey(transportMode.value())) {
            Object modes = validTransportModes.get(transportMode.value());
            return (modes instanceof List && ((List)modes).contains(subModeValue.getName()));
        }
        return false;
    }

    public static List<String> getValidSubmodes(String transportMode) {
        return (List<String>) getTransportModes().get(transportMode);
    }

    public static Map<String, Object> getTransportModes() {
        if (validTransportModes == null) {
            YamlMapFactoryBean yaml = new YamlMapFactoryBean();
            yaml.setResources(new ClassPathResource("transportmodes.yml"));
            validTransportModes = yaml.getObject();
        }
        return validTransportModes;
    }

    public List<GraphQLFieldDefinition> createTransportModeFieldsList() {

        List<GraphQLFieldDefinition> fields = new ArrayList<>();
        fields.add(newFieldDefinition()
                .name(TRANSPORT_MODE)
                .type(filteredTransportModesType.build())
                .build());

        fields.add(newFieldDefinition()
                .name(SUBMODE)
                .type(filteredSubmodes.build())
                .dataFetcher(env -> resolveSubmode(env))
                .build());

        return fields;
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
                .type(filteredTransportModesType.build())
                .build()
        );
        inputFields.add(newInputObjectField()
                        .name(SUBMODE)
                        .type(filteredSubmodes.build())
                        .build()
        );

        return inputFields;
    }
}
