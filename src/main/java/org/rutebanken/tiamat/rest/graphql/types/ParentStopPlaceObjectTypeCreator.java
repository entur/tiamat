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

import graphql.schema.*;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.rest.graphql.scalars.TransportModeScalar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.interchangeWeightingEnum;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.stopPlaceTypeEnum;

@Component
public class ParentStopPlaceObjectTypeCreator {

    public GraphQLObjectType create(GraphQLInterfaceType stopPlaceInterface,
                                    List<GraphQLFieldDefinition> stopPlaceInterfaceFields,
                                    List<GraphQLFieldDefinition> commonFieldsList,
                                    GraphQLObjectType stopPlaceObjectType) {
        return newObject()
                .name(OUTPUT_TYPE_PARENT_STOPPLACE)
                .withInterface(stopPlaceInterface)
                .fields(stopPlaceInterfaceFields)
                .fields(commonFieldsList)
                .field(newFieldDefinition()
                        .name(CHILDREN)
                        .type(new GraphQLList(stopPlaceObjectType)))
                .build();
    }
}
