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
import graphql.language.IntValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;



public class CustomScalars {

    private static double graphQlNumericValueToDouble(Object value) {
        if (value instanceof FloatValue gqlValue) {
            return gqlValue.getValue().doubleValue();
        }

        if (value instanceof IntValue gqlValue) {
            return gqlValue.getValue().doubleValue();
        }

        throw new IllegalStateException("Expected graphql.language.FloatValue or graphql.language.IntValue but got value of type %s with value:  %s".formatted(value.getClass().getCanonicalName(), value.toString()));
    }

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

                            double longitude = graphQlNumericValueToDouble(v.get(0));
                            double latitude = graphQlNumericValueToDouble(v.get(1));
                            coordinates[i] = new Coordinate(longitude, latitude);

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
                        List<Value> coordinatePair = arrayValue.getValues();
                        Coordinate[] coordinates = new Coordinate[coordinatePair.size()];
                        double longitude = graphQlNumericValueToDouble(coordinatePair.getFirst());
                        double latitude = graphQlNumericValueToDouble(coordinatePair.getLast());
                        coordinates[0] = new Coordinate(longitude, latitude);
                        return coordinates;
                    }
                    if (input instanceof List list) {
                        final ArrayValue arrayValue = (ArrayValue) list.getFirst();
                        List<Value> coordinateList = arrayValue.getValues();
                        Coordinate[] coordinates = new Coordinate[coordinateList.size()];

                        for (int i = 0; i < coordinateList.size(); i++) {
                            List v = coordinateList.get(i).getChildren();

                            double longitude = graphQlNumericValueToDouble(v.get(0));
                            double latitude = graphQlNumericValueToDouble(v.get(1));
                            coordinates[i] = new Coordinate(longitude, latitude);

                        }
                        return coordinates;
                    }
                    return null;
                }
            }).build();

}
