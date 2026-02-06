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

package org.rutebanken.tiamat.rest.graphql.scalars;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.ArrayValue;
import graphql.language.FloatValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;



public class CustomScalars {

    public static GraphQLScalarType GraphQLLegacyGeoJSONCoordinates = new GraphQLScalarType.Builder()
            .name("legacyCoordinates")
            .description("Legacy GeoJSON Coordinates")
            .coercing(new Coercing() {
                @Override
                public List<List<Double>> serialize(Object input, GraphQLContext graphQLContext, Locale locale) {
                    if (input instanceof Coordinate[] coordinates) {
                        List<List<Double>> coordinateList = new ArrayList<>();
                        for (Coordinate coordinate : coordinates) {
                            List<Double> coordinatePair = new ArrayList<>();
                            coordinatePair.add(coordinate.x);
                            coordinatePair.add(coordinate.y);

                            coordinateList.add(coordinatePair);
                        }
                        return coordinateList;
                    }
                    return null;
                }

        @Override
        public Coordinate[] parseValue(Object input, GraphQLContext graphQLContext, Locale locale) {
            List<List<Double>> coordinateList = (List<List<Double>>) input;

                    Coordinate[] coordinates = new Coordinate[coordinateList.size()];

                    for (int i = 0; i < coordinateList.size(); i++) {
                        coordinates[i] = new Coordinate(coordinateList.get(i).get(0), coordinateList.get(i).get(1));
                    }

                    return coordinates;
                }

                @Override
                public Object parseLiteral(Value input, CoercedVariables variables, GraphQLContext graphQLContext, Locale locale) {
                    if (input instanceof ArrayValue arrayValue) {
                        List<Value> coordinateList = arrayValue.getValues();
                        Coordinate[] coordinates = new Coordinate[coordinateList.size()];

                        for (int i = 0; i < coordinateList.size(); i++) {
                            List v = coordinateList.get(i).getChildren();

                            FloatValue longitude = (FloatValue) v.get(0);
                            FloatValue latitude = (FloatValue) v.get(1);
                            coordinates[i] = new Coordinate(longitude.getValue().doubleValue(), latitude.getValue().doubleValue());

                }
                return coordinates;
            }
            return null;
        }
    }).build();


    public static GraphQLScalarType GraphQLGeoJSONCoordinates = new GraphQLScalarType.Builder()
            .name("Coordinates")
            .description("GeoJSON Coordinates")
            .coercing(new Coercing() {
                @Override
                public Object serialize(Object input, GraphQLContext graphQLContext, Locale locale) {
                    // If already structured as a List (from GeoJSONCoordinatesFetcher), pass through
                    if (input instanceof List) {
                        return input;
                    }
                    // Legacy handling for Coordinate[] (simple geometries)
                    if (input instanceof Coordinate[] coordinates) {
                        List<List<Double>> coordinateList = new ArrayList<>();
                        for (Coordinate coordinate : coordinates) {
                            List<Double> coordinatePair = new ArrayList<>();
                            coordinatePair.add(coordinate.x);
                            coordinatePair.add(coordinate.y);

                            coordinateList.add(coordinatePair);

                        }
                        if (coordinateList.size() == 1){
                            return coordinateList.getFirst();
                        }
                        if(coordinateList.size() > 1) {
                            return Collections.singletonList(coordinateList);
                        }
                        return coordinateList;

                    }
                    return null;
                }

                @Override
                public Coordinate[] parseValue(Object input, GraphQLContext graphQLContext, Locale locale) {
                    if(input instanceof List<?> list && list.size() == 2){
                        Coordinate[] coordinates = new Coordinate[1];
                        coordinates[0] = new Coordinate((Double) list.get(0),(Double) list.get(1));
                        return coordinates;
                    }

                    List<List<Double>> coordinateList = ((List<List<List<Double>>>) input).getFirst();

                    Coordinate[] coordinates = new Coordinate[coordinateList.size()];

                    for (int i = 0; i < coordinateList.size(); i++) {
                        coordinates[i] = new Coordinate(coordinateList.get(i).get(0), coordinateList.get(i).get(1));
                    }

                    return coordinates;
                }

                @Override
                public Object parseLiteral(Value input, CoercedVariables variables, GraphQLContext graphQLContext, Locale locale) {
                    if (input instanceof ArrayValue arrayValue) {
                        List<Value> values = arrayValue.getValues();

                        // Check if this is a single coordinate pair [lon, lat] or array of coordinates [[lon, lat], ...]
                        if (!values.isEmpty() && values.getFirst() instanceof FloatValue) {
                            // Single coordinate pair: [10.3, 59.9]
                            Coordinate[] coordinates = new Coordinate[1];
                            var longitude = (FloatValue) values.getFirst();
                            var latitude = (FloatValue) values.getLast();
                            coordinates[0] = new Coordinate(longitude.getValue().doubleValue(), latitude.getValue().doubleValue());
                            return coordinates;
                        } else if (!values.isEmpty() && values.getFirst() instanceof ArrayValue) {
                            // Array of coordinate pairs: [[10.3, 59.9], [10.3, 59.9], ...]
                            Coordinate[] coordinates = new Coordinate[values.size()];
                            for (int i = 0; i < values.size(); i++) {
                                ArrayValue coordPair = (ArrayValue) values.get(i);
                                List<Value> pair = coordPair.getValues();
                                FloatValue longitude = (FloatValue) pair.get(0);
                                FloatValue latitude = (FloatValue) pair.get(1);
                                coordinates[i] = new Coordinate(longitude.getValue().doubleValue(), latitude.getValue().doubleValue());
                            }
                            return coordinates;
                        }
                    }
                    if (input instanceof List list) {
                        final ArrayValue arrayValue = (ArrayValue) list.getFirst();
                        List<Value> coordinateList = arrayValue.getValues();
                        Coordinate[] coordinates = new Coordinate[coordinateList.size()];

                        for (int i = 0; i < coordinateList.size(); i++) {
                            List v = coordinateList.get(i).getChildren();

                            FloatValue longitude = (FloatValue) v.get(0);
                            FloatValue latitude = (FloatValue) v.get(1);
                            coordinates[i] = new Coordinate(longitude.getValue().doubleValue(), latitude.getValue().doubleValue());

                        }
                        return coordinates;
                    }
                    return null;
                }
            }).build();

}
