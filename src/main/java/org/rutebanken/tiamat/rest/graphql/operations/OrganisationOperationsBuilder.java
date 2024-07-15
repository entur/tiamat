package org.rutebanken.tiamat.rest.graphql.operations;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLNonNull;
import org.rutebanken.tiamat.service.organisation.OrganisationDeleter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.DELETE_ORGANISATION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ORGANISATION_ID;

@Component
public class OrganisationOperationsBuilder {

    @Autowired
    private OrganisationDeleter organisationDeleter;

    public List<GraphQLFieldDefinition> getOrganisationOperations() {
        List<GraphQLFieldDefinition> operations = new ArrayList<>();

        //Delete Organisation
        operations.add(newFieldDefinition()
                .type(GraphQLBoolean)
                .name(DELETE_ORGANISATION)
                .description("!!! Deletes all versions of Organisation from database - use with caution !!!")
                .argument(newArgument().name(ORGANISATION_ID).type(new GraphQLNonNull(GraphQLString)))
                .dataFetcher(environment -> organisationDeleter.deleteOrganisation(environment.getArgument(ORGANISATION_ID)))
                .build());

        return operations;
    }

}
