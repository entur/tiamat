package org.rutebanken.tiamat.rest.graphql.scalars;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.DATE_SCALAR_DESCRIPTION;

public class DateScalar {

    /**
     * Default time zone for dates.
     * It does not make any assumtions for incoming time zones.
     * It should be able to run in any timezone and parse dates from any timezone.
     **/
    public static final ZoneId DEFAULT_ZONE = ZoneId.of("Europe/Oslo");

    public static final String EXAMPLE_DATE_TIME = "2017-04-23T18:25:43.511+0100";

    /**
     * Milliseconds and time zone offset is _REQUIRED_ in this scalar.
     * Milliseconds will handle most cases for date and time.
     * Enforcing time zone will avoid issues with making assumptions about time zones.
     * ISO 8601 alone does not require time zone.
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXXX";

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    public static GraphQLScalarType GraphQLDateScalar = new GraphQLScalarType("DateTime", DATE_SCALAR_DESCRIPTION, new Coercing() {
        @Override
        public String serialize(Object input) {
            if (input instanceof ZonedDateTime) {
                return (((ZonedDateTime) input)).withZoneSameInstant(DEFAULT_ZONE).format(FORMATTER);
            }
            return null;
        }

        @Override
        public ZonedDateTime parseValue(Object input) {
            return ZonedDateTime.from(ZonedDateTime.parse((CharSequence) input, FORMATTER)).withZoneSameInstant(DEFAULT_ZONE);
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
