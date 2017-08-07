package org.rutebanken.tiamat.rest.graphql.scalars;

import com.vividsolutions.jts.geom.Coordinate;
import graphql.language.ArrayValue;
import graphql.language.FloatValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

import java.util.ArrayList;
import java.util.List;

public class CustomScalars {

    public static GraphQLScalarType GraphQLTrimmedString = new GraphQLScalarType("Trimmed-String", "String without whitespaces", new Coercing() {
        @Override
        public Object serialize(Object input) {
            return getTrimmedString(input);
        }

        @Override
        public Object parseValue(Object input) {
            return serialize(input);
        }

        @Override
        public Object parseLiteral(Object input) {
            if (!(input instanceof StringValue)) return null;
            return serialize(((StringValue) input).getValue());
        }
    });

    private static String getTrimmedString(Object object) {
        if (object != null &&
                object instanceof String) {
            return ((String) object).trim();
        }
        return null;
    }


    public static GraphQLScalarType GraphQLGeoJSONCoordinates = new GraphQLScalarType("Coordinates", null, new Coercing() {
        @Override
        public List<List<Double>> serialize(Object input) {
            if (input instanceof Coordinate[]) {
                Coordinate[] coordinates = ((Coordinate[]) input);
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
