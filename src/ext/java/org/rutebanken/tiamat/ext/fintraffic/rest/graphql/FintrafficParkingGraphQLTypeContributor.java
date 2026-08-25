package org.rutebanken.tiamat.ext.fintraffic.rest.graphql;

import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import org.rutebanken.netex.model.TypeOfInfolinkEnumeration;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;
import org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes;
import org.rutebanken.tiamat.rest.graphql.types.ParkingGraphQLTypeContributor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLInputObjectType.newInputObject;

/**
 * Contributes Fintraffic-specific fields to the GraphQL parking types:
 * <ul>
 *   <li>{@code paymentMethods} — list of {@link PaymentMethodEnumeration} values</li>
 *   <li>{@code infoLinks} — list of info link objects with {@code uri} and {@code typeOfInfoLink}</li>
 * </ul>
 */
@Profile("fintraffic")
@Component
public class FintrafficParkingGraphQLTypeContributor implements ParkingGraphQLTypeContributor {

    static final String PAYMENT_METHODS = "paymentMethods";
    static final String PAYMENT_METHOD_ENUM = "PaymentMethodEnum";
    static final String INFO_LINKS = "infoLinks";
    static final String INFO_LINK_OUTPUT_TYPE = "FintrafficInfoLink";
    static final String INFO_LINK_INPUT_TYPE = "FintrafficInfoLinkInput";
    static final String TYPE_OF_INFO_LINK_ENUM = "TypeOfInfoLinkEnum";
    static final String URI = "uri";
    static final String TYPE_OF_INFO_LINK = "typeOfInfoLink";

    static final GraphQLEnumType paymentMethodEnum =
            CustomGraphQLTypes.createCustomEnumType(PAYMENT_METHOD_ENUM, PaymentMethodEnumeration.class);

    static final GraphQLEnumType typeOfInfoLinkEnum =
            CustomGraphQLTypes.createCustomEnumType(TYPE_OF_INFO_LINK_ENUM, TypeOfInfolinkEnumeration.class);

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

    @Override
    public void contributeToOutputType(GraphQLObjectType.Builder builder) {
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
    }

    @Override
    public void contributeToInputType(GraphQLInputObjectType.Builder builder) {
        builder.field(newInputObjectField()
                .name(PAYMENT_METHODS)
                .type(new GraphQLList(paymentMethodEnum)));
        builder.field(newInputObjectField()
                .name(INFO_LINKS)
                .type(new GraphQLList(infoLinkInputType)));
    }
}

