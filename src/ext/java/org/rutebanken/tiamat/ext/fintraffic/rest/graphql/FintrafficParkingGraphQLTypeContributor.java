package org.rutebanken.tiamat.ext.fintraffic.rest.graphql;

import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;
import org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes;
import org.rutebanken.tiamat.rest.graphql.types.ParkingGraphQLTypeContributor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;

/**
 * Contributes Fintraffic-specific fields to the GraphQL parking types:
 * <ul>
 *   <li>{@code paymentMethods} — list of {@link PaymentMethodEnumeration} values,
 *       added to both the output (query) and input (mutation) parking types.</li>
 * </ul>
 * This field is only present in the schema when the Fintraffic ext package is on the classpath.
 */
@Profile("fintraffic")
@Component
public class FintrafficParkingGraphQLTypeContributor implements ParkingGraphQLTypeContributor {

    static final String PAYMENT_METHODS = "paymentMethods";
    static final String PAYMENT_METHOD_ENUM = "PaymentMethodEnum";

    static final GraphQLEnumType paymentMethodEnum =
            CustomGraphQLTypes.createCustomEnumType(PAYMENT_METHOD_ENUM, PaymentMethodEnumeration.class);

    @Override
    public void contributeToOutputType(GraphQLObjectType.Builder builder) {
        builder.field(newFieldDefinition()
                .name(PAYMENT_METHODS)
                .type(new GraphQLList(paymentMethodEnum)));
    }

    @Override
    public void contributeToInputType(GraphQLInputObjectType.Builder builder) {
        builder.field(newInputObjectField()
                .name(PAYMENT_METHODS)
                .type(new GraphQLList(paymentMethodEnum)));
    }
}
