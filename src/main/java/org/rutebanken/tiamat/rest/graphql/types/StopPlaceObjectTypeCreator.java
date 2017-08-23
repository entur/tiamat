package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.rest.graphql.fetchers.TagFetcher;
import org.rutebanken.tiamat.rest.graphql.scalars.TransportModeScalar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.QUAYS;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.interchangeWeightingEnum;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.stopPlaceTypeEnum;

@Component
public class StopPlaceObjectTypeCreator {


    @Autowired
    private TransportModeScalar transportModeScalar;

    public GraphQLObjectType create(GraphQLInterfaceType stopPlaceInterface, List<GraphQLFieldDefinition> stopPlaceInterfaceFields, List<GraphQLFieldDefinition> commonFieldsList, GraphQLObjectType quayObjectType) {
        return newObject()
                .name(OUTPUT_TYPE_STOPPLACE)
                .withInterface(stopPlaceInterface)
                .fields(stopPlaceInterfaceFields)
                .fields(commonFieldsList)
                .fields(transportModeScalar.createTransportModeFieldsList())
                .field(newFieldDefinition()
                        .name(STOP_PLACE_TYPE)
                        .type(stopPlaceTypeEnum))
                .field(newFieldDefinition()
                        .name(WEIGHTING)
                        .type(interchangeWeightingEnum))
                .field(newFieldDefinition()
                        .name(PARENT_SITE_REF)
                        .type(GraphQLString)
                        .dataFetcher(env -> {
                            SiteRefStructure parentSiteRef = ((StopPlace) env.getSource()).getParentSiteRef();
                            if (parentSiteRef != null) {
                                return parentSiteRef.getRef();
                            }
                            return null;
                        }))
                .field(newFieldDefinition()
                        .name(QUAYS)
                        .type(new GraphQLList(quayObjectType)))
                .build();
    }
}
