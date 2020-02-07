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

import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import org.locationtech.jts.geom.Coordinate;
import graphql.language.ArrayValue;
import graphql.language.FloatValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomScalars {

    public static GraphQLScalarType GraphQLGeoJSONCoordinatesStandard = new GraphQLScalarType("CoordinatesStandard", null, new Coercing() {
        @Override
        public Object serialize(Object geoJsonFetcherResult) throws CoercingSerializeException {
            final Geometry geometry = (Geometry) geoJsonFetcherResult;
            if (geometry instanceof Point) {
                final Point centroid = geometry.getCentroid();
                return convertPointToJSONArray(centroid);
            } else if (geometry instanceof Polygon) {
                return  convertPolygonToJSONArray(geometry);
            } else if (geometry instanceof MultiPolygon) {
                final MultiPolygon multiPolygon = (MultiPolygon) geometry;
                return convertMultiPolygonToJSONArray(multiPolygon);
            }
            return null;
        }

        @Override
        public Coordinate[] parseValue(Object input) throws CoercingParseValueException {
            //TODO: implement
            return null;
        }

        @Override
        public Object parseLiteral(Object input) throws CoercingParseLiteralException {
            //TODO: implement
            return null;
        }
    });

    private static List<Double> convertPointToJSONArray(Point point) {
        final Coordinate[] coordinates = point.getCoordinates();
        for (Coordinate coordinate : coordinates) {
            List<Double> coordinatePair = new ArrayList<>();
            coordinatePair.add(coordinate.x);
            coordinatePair.add(coordinate.y);
            return coordinatePair;
        }
        return null;
    }

    private static List<List<List<Double>>> convertPolygonToJSONArray(Geometry geometry) {
        List<List<List<Double>>> coordinateList = new ArrayList<>();
        final Polygon polygon = (Polygon) geometry;
        final int numInteriorRing = polygon.getNumInteriorRing();
        if (numInteriorRing > 0) {
            final Coordinate[] exteriorCoordinates = polygon.getExteriorRing().getCoordinates();
            coordinateList.add(convertInnerPolygonToJSONArray(exteriorCoordinates));
            for (int i = 0; i < numInteriorRing; i++) {
                final Coordinate[] interiorCoordinates = polygon.getInteriorRingN(i).getCoordinates();
                coordinateList.add(convertInnerPolygonToJSONArray(interiorCoordinates));
            }
        } else {
            final Coordinate[] coordinates = polygon.getCoordinates();
            coordinateList.add(convertInnerPolygonToJSONArray(coordinates));
        }

        return coordinateList;
    }

    private static List<List<Double>> convertInnerPolygonToJSONArray(Coordinate[] coordinates) {
         return convertCordinateToList(coordinates);
    }

    private static List<List<Double>> convertCordinateToList(Coordinate[] coordinates) {
        List<List<Double>> coordinateList = new ArrayList<>();
        for (Coordinate coordinate : coordinates) {
            List<Double> coordinatePair = new ArrayList<>();
            coordinatePair.add(coordinate.x);
            coordinatePair.add(coordinate.y);
            coordinateList.add(coordinatePair);
        }
        return coordinateList;
    }

    private static List<List<List<List<Double>>>> convertMultiPolygonToJSONArray(MultiPolygon multiPolygon) {
        List<List<List<List<Double>>>> multicoordinates=new ArrayList<>();
        for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
            final Geometry geometry = multiPolygon.getGeometryN(i);
            final List<List<List<Double>>> polygon = convertPolygonToJSONArray(geometry);
            multicoordinates.add(polygon);
        }
        return multicoordinates;

    }

    public static GraphQLScalarType GraphQLGeoJSONCoordinates = new GraphQLScalarType("Coordinates", null, new Coercing() {
        @Override
        public Object serialize(Object input) {
            if (input instanceof Coordinate[]) {
                Coordinate[] coordinates = ((Coordinate[]) input);
                var coordinateList = convertCordinateToList(coordinates);
                return Arrays.asList(coordinateList);
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
            if (input instanceof ArrayValue) {
                ArrayList<ArrayValue> coordinateList = (ArrayList) ((ArrayValue) input).getValues();
                Coordinate[] coordinates = new Coordinate[coordinateList.size()];

                for (int i = 0; i < coordinateList.size(); i++) {
                    ArrayValue v = coordinateList.get(i);

                    FloatValue longitude = (FloatValue) v.getValues().get(0);
                    FloatValue latitude = (FloatValue) v.getValues().get(1);
                    coordinates[i] = new Coordinate(longitude.getValue().doubleValue(), latitude.getValue().doubleValue());

                }
                return coordinates;
            }
            return null;
        }
    });
}
