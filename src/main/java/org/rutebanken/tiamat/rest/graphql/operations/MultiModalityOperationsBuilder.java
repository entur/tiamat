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

import com.vividsolutions.jts.geom.Point;
import graphql.schema.*;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.rest.graphql.mappers.GeometryMapper;
import org.rutebanken.tiamat.rest.graphql.mappers.ValidBetweenMapper;
import org.rutebanken.tiamat.service.stopplace.MultiModalStopPlaceEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.mappers.EmbeddableMultilingualStringMapper.getEmbeddableString;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.embeddableMultiLingualStringInputObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.geoJsonInputType;

@Component
public class MultiModalityOperationsBuilder {

    public static final String CREATE_MULTI_MODAL_STOP_PLACE_INPUT = "createMultiModalStopPlaceInput";

    public static final String ADD_TO_MULTI_MODAL_STOP_PLACE_INPUT = "addToMultiModalStopPlaceInput";

    private static final String INPUT = "input";

    @Autowired
    private MultiModalStopPlaceEditor parentStopPlaceEditor;

    @Autowired
    private ValidBetweenMapper validBetweenMapper;

    @Autowired
    private GeometryMapper geometryMapper;

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
                .dataFetcher(environment -> {
                    Map input = environment.getArgument(INPUT);

                    if(input == null) {
                        throw new IllegalArgumentException(INPUT + " is not specified");
                    }

                    ValidBetween validBetween = validBetweenMapper.map((Map) input.get(VALID_BETWEEN));
                    String versionComment = (String) input.get(VERSION_COMMENT);
                    Point geoJsonPoint = geometryMapper.createGeoJsonPoint((Map) input.get(GEOMETRY));
                    EmbeddableMultilingualString name = getEmbeddableString((Map) input.get(NAME));

                    @SuppressWarnings("unchecked")
                    List<String> stopPlaceIds = (List<String>) input.get(STOP_PLACE_IDS);

                    return parentStopPlaceEditor.createMultiModalParentStopPlace(stopPlaceIds, name, validBetween, versionComment, geoJsonPoint);
                })
                .build());

        List<GraphQLInputObjectField> addOrRemoveChildMultiModalStopPlaceFields = new ArrayList<>();
        addOrRemoveChildMultiModalStopPlaceFields.add(newInputObjectField().name(PARENT_SITE_REF).type(GraphQLString).build());
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
                .dataFetcher(environment -> {
                    Map input = environment.getArgument(INPUT);

                    if(input == null) {
                        throw new IllegalArgumentException(INPUT + " is not specified");
                    }

                    if(input.get(PARENT_SITE_REF) == null) {
                        throw new IllegalArgumentException("Parent site ref cannot be null for this operation" + ADD_TO_MULTI_MODAL_STOP_PLACE_INPUT);
                    }

                    String parentSiteRef = (String) input.get(PARENT_SITE_REF);

                    ValidBetween validBetween = validBetweenMapper.map((Map) input.get(VALID_BETWEEN));
                    String versionComment = (String) input.get(VERSION_COMMENT);

                    if(input.get(STOP_PLACE_IDS) == null) {
                        throw new IllegalArgumentException("List of " + STOP_PLACE_IDS + "cannot be null");
                    }
                    @SuppressWarnings("unchecked")
                    List<String> stopPlaceIds = (List<String>) input.get(STOP_PLACE_IDS);

                    return parentStopPlaceEditor.addToMultiModalParentStopPlace(parentSiteRef, stopPlaceIds, validBetween, versionComment);
                })
                .build());

        operations.add(newFieldDefinition()
                .type(parentStopPlaceObjectType)
                .name(REMOVE_FROM_MULTIMODAL_STOPPLACE)
                .description("Removes a StopPlace from an existing ParentStopPlace")
                .argument(newArgument().name(PARENT_SITE_REF).type(GraphQLString))
                .argument(newArgument().name(STOP_PLACE_ID).type(new GraphQLList(GraphQLString)))
                .dataFetcher(environment -> parentStopPlaceEditor.removeFromMultiModalStopPlace(environment.getArgument(PARENT_SITE_REF), environment.getArgument(STOP_PLACE_ID)))
                .build());

        return operations;
    }


}
