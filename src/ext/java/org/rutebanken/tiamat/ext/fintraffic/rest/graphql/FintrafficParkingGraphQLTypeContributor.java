package org.rutebanken.tiamat.ext.fintraffic.rest.graphql;

import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import org.rutebanken.netex.model.AccessModeEnumeration;
import org.rutebanken.netex.model.EntranceEnumeration;
import org.rutebanken.netex.model.TypeOfInfolinkEnumeration;
import org.rutebanken.tiamat.model.LightingEnumeration;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;
import org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes;
import org.rutebanken.tiamat.rest.graphql.types.ParkingGraphQLTypeContributor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLFloat;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLInputObjectType.newInputObject;

/**
 * Contributes Fintraffic-specific fields to the GraphQL parking types:
 * <ul>
 *   <li>{@code lighting} — {@link LightingEnumeration} scalar</li>
 *   <li>{@code paymentMethods} — list of {@link PaymentMethodEnumeration} values</li>
 *   <li>{@code infoLinks} — list of info link objects with {@code uri} and {@code typeOfInfoLink}</li>
 *   <li>{@code vehicleEntrances} — list of vehicle entrance objects</li>
 * </ul>
 */
@Profile("fintraffic")
@Component
public class FintrafficParkingGraphQLTypeContributor implements ParkingGraphQLTypeContributor {

    static final String LIGHTING = "lighting";
    static final String PAYMENT_METHODS = "paymentMethods";
    static final String PAYMENT_METHOD_ENUM = "PaymentMethodEnum";
    static final String INFO_LINKS = "infoLinks";
    static final String INFO_LINK_OUTPUT_TYPE = "FintrafficInfoLink";
    static final String INFO_LINK_INPUT_TYPE = "FintrafficInfoLinkInput";
    static final String TYPE_OF_INFO_LINK_ENUM = "TypeOfInfoLinkEnum";
    static final String URI = "uri";
    static final String TYPE_OF_INFO_LINK = "typeOfInfoLink";
    static final String VEHICLE_ENTRANCES = "vehicleEntrances";
    static final String VEHICLE_ENTRANCE_OUTPUT_TYPE = "FintrafficVehicleEntrance";
    static final String VEHICLE_ENTRANCE_INPUT_TYPE = "FintrafficVehicleEntranceInput";
    static final String ENTRANCE_TYPE_ENUM = "EntranceTypeEnum";
    static final String VEHICLE_ENTRANCE_LABEL = "label";
    static final String ENTRANCE_TYPE = "entranceType";
    static final String WIDTH = "width";
    static final String HEIGHT = "height";
    static final String IS_ENTRY = "isEntry";
    static final String IS_EXIT = "isExit";
    static final String PUBLIC_CODE = "publicCode";
    static final String ACCESS_MODES = "accessModes";
    static final String ACCESS_MODE_ENUM = "AccessModeEnum";
    static final String AVAILABILITY_CONDITIONS = "availabilityConditions";
    static final String AVAILABILITY_CONDITION_OUTPUT_TYPE = "FintrafficAvailabilityCondition";
    static final String AVAILABILITY_CONDITION_INPUT_TYPE = "FintrafficAvailabilityConditionInput";
    static final String DAY_TYPE_REF = "dayTypeRef";
    static final String IS_AVAILABLE = "isAvailable";
    static final String START_TIME = "startTime";
    static final String END_TIME = "endTime";

    static final GraphQLEnumType paymentMethodEnum =
            CustomGraphQLTypes.createCustomEnumType(PAYMENT_METHOD_ENUM, PaymentMethodEnumeration.class);

    static final GraphQLEnumType typeOfInfoLinkEnum =
            CustomGraphQLTypes.createCustomEnumType(TYPE_OF_INFO_LINK_ENUM, TypeOfInfolinkEnumeration.class);

    static final GraphQLEnumType entranceTypeEnum =
            CustomGraphQLTypes.createCustomEnumType(ENTRANCE_TYPE_ENUM, EntranceEnumeration.class);

    static final GraphQLEnumType accessModeEnum =
            CustomGraphQLTypes.createCustomEnumType(ACCESS_MODE_ENUM, AccessModeEnumeration.class);

    static final GraphQLObjectType infoLinkOutputType = newObject()
            .name(INFO_LINK_OUTPUT_TYPE)
            .field(newFieldDefinition().name(URI).type(GraphQLNonNull.nonNull(GraphQLString)))
            .field(newFieldDefinition().name(TYPE_OF_INFO_LINK).type(typeOfInfoLinkEnum))
            .build();

    static final GraphQLInputObjectType infoLinkInputType = newInputObject()
            .name(INFO_LINK_INPUT_TYPE)
            .field(newInputObjectField().name(URI).type(GraphQLNonNull.nonNull(GraphQLString)))
            .field(newInputObjectField().name(TYPE_OF_INFO_LINK).type(typeOfInfoLinkEnum))
            .build();

    static final GraphQLObjectType vehicleEntranceOutputType = newObject()
            .name(VEHICLE_ENTRANCE_OUTPUT_TYPE)
            .field(newFieldDefinition().name(VEHICLE_ENTRANCE_LABEL).type(GraphQLString))
            .field(newFieldDefinition().name(ENTRANCE_TYPE).type(entranceTypeEnum))
            .field(newFieldDefinition().name(WIDTH).type(GraphQLFloat))
            .field(newFieldDefinition().name(HEIGHT).type(GraphQLFloat))
            .field(newFieldDefinition().name(IS_ENTRY).type(GraphQLBoolean))
            .field(newFieldDefinition().name(IS_EXIT).type(GraphQLBoolean))
            .field(newFieldDefinition().name(PUBLIC_CODE).type(GraphQLString))
            .field(newFieldDefinition().name(ACCESS_MODES).type(new GraphQLList(accessModeEnum)))
            .build();

    static final GraphQLInputObjectType vehicleEntranceInputType = newInputObject()
            .name(VEHICLE_ENTRANCE_INPUT_TYPE)
            .field(newInputObjectField().name(VEHICLE_ENTRANCE_LABEL).type(GraphQLString))
            .field(newInputObjectField().name(ENTRANCE_TYPE).type(entranceTypeEnum))
            .field(newInputObjectField().name(WIDTH).type(GraphQLFloat))
            .field(newInputObjectField().name(HEIGHT).type(GraphQLFloat))
            .field(newInputObjectField().name(IS_ENTRY).type(GraphQLBoolean))
            .field(newInputObjectField().name(IS_EXIT).type(GraphQLBoolean))
            .field(newInputObjectField().name(PUBLIC_CODE).type(GraphQLString))
            .field(newInputObjectField().name(ACCESS_MODES).type(new GraphQLList(accessModeEnum)))
            .build();

    static final GraphQLObjectType availabilityConditionOutputType = newObject()
            .name(AVAILABILITY_CONDITION_OUTPUT_TYPE)
            .field(newFieldDefinition().name(DAY_TYPE_REF).type(GraphQLNonNull.nonNull(GraphQLString)))
            .field(newFieldDefinition().name(IS_AVAILABLE).type(GraphQLBoolean))
            .field(newFieldDefinition().name(START_TIME).type(GraphQLString))
            .field(newFieldDefinition().name(END_TIME).type(GraphQLString))
            .build();

    static final GraphQLInputObjectType availabilityConditionInputType = newInputObject()
            .name(AVAILABILITY_CONDITION_INPUT_TYPE)
            .field(newInputObjectField().name(DAY_TYPE_REF).type(GraphQLNonNull.nonNull(GraphQLString)))
            .field(newInputObjectField().name(IS_AVAILABLE).type(GraphQLBoolean))
            .field(newInputObjectField().name(START_TIME).type(GraphQLString))
            .field(newInputObjectField().name(END_TIME).type(GraphQLString))
            .build();

    @Override
    public void contributeToOutputType(GraphQLObjectType.Builder builder) {
        builder.field(newFieldDefinition()
                .name(LIGHTING)
                .type(CustomGraphQLTypes.lightingEnumType)
                .dataFetcher(env -> {
                    Object source = env.getSource();
                    if (!(source instanceof org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking fp)) {
                        return null;
                    }
                    return fp.getLighting();
                }));
        builder.field(newFieldDefinition()
                .name(PAYMENT_METHODS)
                .type(new GraphQLList(paymentMethodEnum)));
        builder.field(newFieldDefinition()
                .name(INFO_LINKS)
                .type(new GraphQLList(infoLinkOutputType))
                .dataFetcher(env -> {
                    Object source = env.getSource();
                    if (!(source instanceof org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking fp)) {
                        return java.util.List.of();
                    }
                    return fp.getInfoLinks().stream()
                            .map(link -> {
                                var m = new java.util.HashMap<String, Object>();
                                m.put(URI, link.getUri());
                                if (link.getTypeOfInfoLink() != null) {
                                    try {
                                        m.put(TYPE_OF_INFO_LINK,
                                                TypeOfInfolinkEnumeration.fromValue(link.getTypeOfInfoLink()));
                                    } catch (IllegalArgumentException ignored) {
                                        // stored value no longer valid; skip
                                    }
                                }
                                return m;
                            })
                            .collect(java.util.stream.Collectors.toList());
                }));
        builder.field(newFieldDefinition()
                .name(VEHICLE_ENTRANCES)
                .type(new GraphQLList(vehicleEntranceOutputType))
                .dataFetcher(env -> {
                    Object source = env.getSource();
                    if (!(source instanceof org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking fp)) {
                        return java.util.List.of();
                    }
                    return fp.getFintrafficVehicleEntrances().stream()
                            .map(entrance -> {
                                var m = new java.util.HashMap<String, Object>();
                                m.put(VEHICLE_ENTRANCE_LABEL, entrance.getLabel());
                                if (entrance.getEntranceType() != null) {
                                    try {
                                        m.put(ENTRANCE_TYPE,
                                                EntranceEnumeration.fromValue(entrance.getEntranceType()));
                                    } catch (IllegalArgumentException ignored) {
                                        // stored value no longer valid; skip
                                    }
                                }
                                m.put(WIDTH, entrance.getWidth() != null ? entrance.getWidth().doubleValue() : null);
                                m.put(HEIGHT, entrance.getHeight() != null ? entrance.getHeight().doubleValue() : null);
                                m.put(IS_ENTRY, entrance.getIsEntry());
                                m.put(IS_EXIT, entrance.getIsExit());
                                m.put(PUBLIC_CODE, entrance.getPublicCode());
                                m.put(ACCESS_MODES, entrance.getAccessModesList().stream()
                                        .map(value -> {
                                            try {
                                                return AccessModeEnumeration.fromValue(value);
                                            } catch (IllegalArgumentException ignored) {
                                                return null;
                                            }
                                        })
                                        .filter(java.util.Objects::nonNull)
                                        .collect(java.util.stream.Collectors.toList()));
                                return m;
                            })
                            .collect(java.util.stream.Collectors.toList());
                }));
        builder.field(newFieldDefinition()
                .name(AVAILABILITY_CONDITIONS)
                .type(new GraphQLList(availabilityConditionOutputType))
                .dataFetcher(env -> {
                    Object source = env.getSource();
                    if (!(source instanceof org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking fp)) {
                        return java.util.List.of();
                    }
                    return fp.getAvailabilityConditions().stream()
                            .map(condition -> {
                                var m = new java.util.HashMap<String, Object>();
                                m.put(DAY_TYPE_REF, condition.getDayTypeRef());
                                m.put(IS_AVAILABLE, condition.isAvailable());
                                m.put(START_TIME, condition.getStartTime() != null ? condition.getStartTime().toString() : null);
                                m.put(END_TIME, condition.getEndTime() != null ? condition.getEndTime().toString() : null);
                                return m;
                            })
                            .collect(java.util.stream.Collectors.toList());
                }));
    }

    @Override
    public void contributeToInputType(GraphQLInputObjectType.Builder builder) {
        builder.field(newInputObjectField()
                .name(LIGHTING)
                .type(CustomGraphQLTypes.lightingEnumType));
        builder.field(newInputObjectField()
                .name(PAYMENT_METHODS)
                .type(new GraphQLList(paymentMethodEnum)));
        builder.field(newInputObjectField()
                .name(INFO_LINKS)
                .type(new GraphQLList(infoLinkInputType)));
        builder.field(newInputObjectField()
                .name(VEHICLE_ENTRANCES)
                .type(new GraphQLList(vehicleEntranceInputType)));
        builder.field(newInputObjectField()
                .name(AVAILABILITY_CONDITIONS)
                .type(new GraphQLList(availabilityConditionInputType)));
    }
}
