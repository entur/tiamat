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

package org.rutebanken.tiamat.rest.graphql.operations;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ADD_TO_MULTIMODAL_STOPPLACE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.CREATE_MULTI_MODAL_STOPPLACE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.GEOMETRY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NAME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PARENT_SITE_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POSTAL_ADDRESS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.REMOVE_FROM_MULTIMODAL_STOPPLACE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STOP_PLACE_ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STOP_PLACE_IDS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.URL;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALID_BETWEEN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VERSION_COMMENT;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.embeddableMultiLingualStringInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.geoJsonInputType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.postalAddressInputObjectType;

@Component
public class MultiModalityOperationsBuilder {

    public static final String CREATE_MULTI_MODAL_STOP_PLACE_INPUT = "createMultiModalStopPlaceInput";

    public static final String ADD_TO_MULTI_MODAL_STOP_PLACE_INPUT = "addToMultiModalStopPlaceInput";

    private static final String INPUT = "input";

    public List<GraphQLFieldDefinition> getMultiModalityOperations(GraphQLObjectType parentStopPlaceObjectType,
                                                                   GraphQLInputObjectType validBetweenInputObjectType) {
        List<GraphQLFieldDefinition> operations = new ArrayList<>();

        List<GraphQLInputObjectField> createMultiModalStopPlaceFields = new ArrayList<>();

        createMultiModalStopPlaceFields.add(newInputObjectField().name(NAME).type(new GraphQLNonNull(embeddableMultiLingualStringInputObjectType)).build());
        createMultiModalStopPlaceFields.add(newInputObjectField().name(DESCRIPTION).type(embeddableMultiLingualStringInputObjectType).build());
        createMultiModalStopPlaceFields.add(newInputObjectField().name(VERSION_COMMENT).type(GraphQLString).build());
        createMultiModalStopPlaceFields.add(newInputObjectField().name(GEOMETRY).type(geoJsonInputType).build());
        createMultiModalStopPlaceFields.add(newInputObjectField().name(VALID_BETWEEN).type(validBetweenInputObjectType).build());
        createMultiModalStopPlaceFields.add(newInputObjectField().name(STOP_PLACE_IDS).type(new GraphQLNonNull(new GraphQLList(GraphQLString))).build());
        createMultiModalStopPlaceFields.add(newInputObjectField().name(URL).type(GraphQLString).build());
        createMultiModalStopPlaceFields.add(newInputObjectField().name(POSTAL_ADDRESS).type(postalAddressInputObjectType).build());

        operations.add(newFieldDefinition()
                .type(parentStopPlaceObjectType)
                .name(CREATE_MULTI_MODAL_STOPPLACE)
                .description("Creates a new multimodal parent StopPlace")
                .argument(newArgument()
                        .name(INPUT)
                        .type(newInputObject()
                                .name(CREATE_MULTI_MODAL_STOP_PLACE_INPUT)
                                .fields(createMultiModalStopPlaceFields)
                                .build())
                        .build())
                .build());

        List<GraphQLInputObjectField> addOrRemoveChildMultiModalStopPlaceFields = new ArrayList<>();
        addOrRemoveChildMultiModalStopPlaceFields.add(newInputObjectField().name(PARENT_SITE_REF).type(new GraphQLNonNull(GraphQLString)).build());
        addOrRemoveChildMultiModalStopPlaceFields.add(newInputObjectField().name(VERSION_COMMENT).type(GraphQLString).build());
        addOrRemoveChildMultiModalStopPlaceFields.add(newInputObjectField().name(VALID_BETWEEN).type(validBetweenInputObjectType).build());
        addOrRemoveChildMultiModalStopPlaceFields.add(newInputObjectField().name(STOP_PLACE_IDS).type(new GraphQLNonNull(new GraphQLList(GraphQLString))).build());

        operations.add(newFieldDefinition()
                .type(parentStopPlaceObjectType)
                .name(ADD_TO_MULTIMODAL_STOPPLACE)
                .description("Adds a StopPlace to an existing ParentStopPlace")
                .argument(newArgument()
                        .name(INPUT)
                        .type(newInputObject()
                                .name(ADD_TO_MULTI_MODAL_STOP_PLACE_INPUT)
                                .fields(addOrRemoveChildMultiModalStopPlaceFields)
                                .build())
                        .build())
                .build());

        operations.add(newFieldDefinition()
                .type(parentStopPlaceObjectType)
                .name(REMOVE_FROM_MULTIMODAL_STOPPLACE)
                .description("Removes a StopPlace from an existing ParentStopPlace")
                .argument(newArgument().name(PARENT_SITE_REF).type(new GraphQLNonNull(GraphQLString)))
                .argument(newArgument().name(STOP_PLACE_ID).type(new GraphQLList(GraphQLString)))
                .build());

        return operations;
    }


}
