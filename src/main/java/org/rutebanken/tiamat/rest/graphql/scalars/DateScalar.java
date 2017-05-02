package org.rutebanken.tiamat.rest.graphql.scalars;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.DATE_SCALAR_DESCRIPTION;

@Component
public class DateScalar {


    @Autowired
    private ExportTimeZone exportTimeZone;

    public static final String EXAMPLE_DATE_TIME = "2017-04-23T18:25:43.511+0100";

    /**
     * Milliseconds and time zone offset is _REQUIRED_ in this scalar.
     * Milliseconds will handle most cases for date and time.
     * Enforcing time zone will avoid issues with making assumptions about time zones.
     * ISO 8601 alone does not require time zone.
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXXX";

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);


    private GraphQLScalarType graphQLDateScalar;

    public GraphQLScalarType getGraphQLDateScalar() {
        if (graphQLDateScalar == null) {
            graphQLDateScalar = createGraphQLDateScalar();
        }
        return graphQLDateScalar;
    }

    private GraphQLScalarType createGraphQLDateScalar() {
        return new GraphQLScalarType("DateTime", DATE_SCALAR_DESCRIPTION, new Coercing() {
            @Override
            public String serialize(Object input) {
                if (input instanceof Instant) {
                    return (((Instant) input)).atZone(exportTimeZone.getDefaultTimeZone()).format(FORMATTER);
                }
                return null;
            }

            @Override
            public Instant parseValue(Object input) {
                return Instant.from(FORMATTER.parse((CharSequence) input));
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

}
