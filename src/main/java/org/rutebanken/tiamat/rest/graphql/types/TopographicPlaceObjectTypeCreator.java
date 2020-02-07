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
import org.rutebanken.tiamat.rest.graphql.fetchers.GeojsonFetcher;
import org.rutebanken.tiamat.rest.graphql.fetchers.PolygonFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NAME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_TOPOGRAPHIC_PLACE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PARENT_TOPOGRAPHIC_PLACE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POLYGON;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TOPOGRAPHIC_PLACE_TYPE;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.embeddableMultilingualStringObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.geoJsonObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.standardGeoJsonObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.topographicPlaceTypeEnum;

@Component
public class TopographicPlaceObjectTypeCreator {

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;


    @Autowired
    private PolygonFetcher polygonFetcher;

    @Autowired
    private GeojsonFetcher geojsonFetcher;

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
                        .deprecate("Non standard geojson implementation,instead use geojson field")
                        .type(geoJsonObjectType)
                        .dataFetcher(polygonFetcher))
                .field(newFieldDefinition()
                        .name("geojson")
                        .type(standardGeoJsonObjectType)
                        .dataFetcher(geojsonFetcher)
                )
                .build();
    }

}
