package org.rutebanken.tiamat.rest.graphql.scalars;

import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

public class DateScalarTest {

    @Test
    public void testSerialize() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2004, 2, 3, 4, 5, 6, 50 * 1000000, ZoneId.of("Z"));

        String actual = (String) DateScalar.GraphQLDateScalar.getCoercing().serialize(zonedDateTime);

        System.out.println(actual);
        assertThat(actual).isEqualTo("2004-02-03T04:05:06.050Z");
    }

    @Test
    public void testParseValue() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2004, 2, 3, 4, 5, 6, 50 * 1000000, ZoneId.of("Z"));
        String input = "2004-02-03T04:05:06.050Z";
        ZonedDateTime actual = (ZonedDateTime) DateScalar.GraphQLDateScalar.getCoercing().parseValue(input);
        assertThat(actual).isEqualTo(zonedDateTime);
    }

}