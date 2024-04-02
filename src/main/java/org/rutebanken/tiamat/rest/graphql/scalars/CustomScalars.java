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

import graphql.language.ArrayValue;
import graphql.language.FloatValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class CustomScalars {

    public static GraphQLScalarType GraphQLGeoJSONCoordinates = new GraphQLScalarType.Builder()
            .name("Coordinates")
            .description("GeoJSON Coordinates")
            .coercing(new Coercing() {
                @Override
                public List<List<Double>> serialize(Object input) {
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
        public Coordinate[] parseValue(Object input) {
            List<List<Double>> coordinateList = (List<List<Double>>) input;

                    Coordinate[] coordinates = new Coordinate[coordinateList.size()];

                    for (int i = 0; i < coordinateList.size(); i++) {
                        coordinates[i] = new Coordinate(coordinateList.get(i).get(0), coordinateList.get(i).get(1));
                    }

                    return coordinates;
                }

                @Override
                public Object parseLiteral(Object input) {
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
}
