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

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.rest.graphql.fetchers.PolygonFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.*;

@Component
public class TopographicPlaceObjectTypeCreator {

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;


    @Autowired
    private PolygonFetcher polygonFetcher;

    public GraphQLObjectType create() {
        return newObject()
                .name(OUTPUT_TYPE_TOPOGRAPHIC_PLACE)
                .field(newFieldDefinition()
                        .name(ID)
                        .type(GraphQLString)
                        .dataFetcher(env -> {
                            TopographicPlace topographicPlace = (TopographicPlace) env.getSource();
                            if (topographicPlace != null) {
                                return topographicPlace.getNetexId();
                            } else {
                                return null;
                            }
                        }))
                .field(newFieldDefinition()
                        .name(TOPOGRAPHIC_PLACE_TYPE)
                        .type(topographicPlaceTypeEnum))
                .field(newFieldDefinition()
                        .name(NAME)
                        .type(embeddableMultilingualStringObjectType))
                .field(newFieldDefinition()
                        .name(PARENT_TOPOGRAPHIC_PLACE)
                        .type(new GraphQLTypeReference(OUTPUT_TYPE_TOPOGRAPHIC_PLACE))
                        .dataFetcher(env -> {
                            if(env.getSource() instanceof  TopographicPlace) {
                                TopographicPlace child = (TopographicPlace) env.getSource();
                                if(child.getParentTopographicPlaceRef() != null) {
                                    return topographicPlaceRepository.findFirstByNetexIdAndVersion(child.getParentTopographicPlaceRef().getRef(), Long.parseLong(child.getParentTopographicPlaceRef().getVersion()));
                                }
                            }
                            return null;
                        })
                )
                .field(newFieldDefinition()
                        .name(POLYGON)
                        .type(geoJsonObjectType)
                        .dataFetcher(polygonFetcher))
                .build();
    }

}
