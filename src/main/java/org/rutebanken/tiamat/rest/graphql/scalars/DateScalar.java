package org.rutebanken.tiamat.rest.graphql.scalars;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.DATE_SCALAR_DESCRIPTION;

public class DateScalar {

    public static final String EXAMPLE_DATE_TIME = "2012-04-23T18:25:43.511Z";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSz";

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    public static GraphQLScalarType GraphQLDateScalar = new GraphQLScalarType("Date", DATE_SCALAR_DESCRIPTION, new Coercing() {
        @Override
        public String serialize(Object input) {
            if (input instanceof ZonedDateTime) {
                return ((ZonedDateTime) input).format(FORMATTER);
            }
            return null;
        }

        @Override
        public ZonedDateTime parseValue(Object input) {
            return ZonedDateTime.from(ZonedDateTime.parse((CharSequence) input, FORMATTER));
        }

        @Override
        public Object parseLiteral(Object input) {
            if (input instanceof StringValue) {
                return parseValue(((StringValue) input).getValue());
            }
            return null;
        }
    });

}
