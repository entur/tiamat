package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLObjectType;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Component
public class TariffZoneObjectTypeCreator {

    public GraphQLObjectType create(List<GraphQLFieldDefinition> zoneCommonFieldList) {
        return newObject()
                .name(OUTPUT_TYPE_TARIFF_ZONE)
                .fields(zoneCommonFieldList)
                .build();
    }

}
