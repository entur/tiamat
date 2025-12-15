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

package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import org.rutebanken.tiamat.rest.graphql.scalars.TransportModeScalar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.CHILDREN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.INPUT_TYPE_PARENT_STOPPLACE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POSTAL_ADDRESS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.URL;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALID_BETWEEN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VERSION_COMMENT;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.postalAddressInputObjectType;

@Component
public class ParentStopPlaceInputObjectTypeCreator {

    @Autowired
    private TransportModeScalar transportModeScalar;

    public GraphQLInputObjectType create(List<GraphQLInputObjectField> commonInputFieldList,
                                         GraphQLInputObjectType validBetweenInputObjectType,
                                         GraphQLInputObjectType stopPlaceInputObjectType) {
        return newInputObject()
                .name(INPUT_TYPE_PARENT_STOPPLACE)
                .fields(commonInputFieldList)
                .fields(transportModeScalar.createTransportModeInputFieldsList())
                .field(newInputObjectField()
                        .name(VERSION_COMMENT)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(VALID_BETWEEN)
                        .type(validBetweenInputObjectType))
                .field(newInputObjectField()
                        .name(URL)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(POSTAL_ADDRESS)
                        .type(postalAddressInputObjectType))
                .field(newInputObjectField()
                        .name(CHILDREN)
                        .type(new GraphQLList(stopPlaceInputObjectType)))
                .build();
        
    }
}
