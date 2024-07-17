package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import org.rutebanken.tiamat.rest.graphql.fetchers.OrganisationFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ORGANISATION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ORGANISATION_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_STOP_PLACE_ORGANISATION_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.RELATIONSHIP_TYPE;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.stopPlaceOrganisationRelationshipTypeEnum;

@Component
public class StopPlaceOrganisationRefObjectTypeCreator {
    @Autowired
    OrganisationFetcher organisationFetcher;

    public GraphQLObjectType create(GraphQLObjectType organisationObjectType) {
         return GraphQLObjectType.newObject()
                .name(OUTPUT_TYPE_STOP_PLACE_ORGANISATION_REF)
                .field(newFieldDefinition()
                        .name(ORGANISATION_REF)
                        .type(new GraphQLNonNull(GraphQLString)))
                .field(newFieldDefinition()
                        .name(RELATIONSHIP_TYPE)
                        .type(stopPlaceOrganisationRelationshipTypeEnum))
                .field(newFieldDefinition()
                        .name(ORGANISATION)
                        .type(organisationObjectType)
                        .dataFetcher(organisationFetcher::getForStopPlace)
                )
                .build();
    }
}
