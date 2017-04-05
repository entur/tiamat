package org.rutebanken.tiamat.rest.graphql.scalars;

import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.rest.graphql.scalars.DateScalar.EXAMPLE_DATE_TIME;

public class DateScalarTest {

    @Test
    public void serializeDateTimeInAnyTimeZoneAndReturnCorrectOffset() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2004, 2, 3, 4, 5, 6, 50 * 1000000, ZoneId.of("Chile/EasterIsland"));

        String actual = (String) DateScalar.GraphQLDateScalar.getCoercing().serialize(zonedDateTime);

        System.out.println(actual);
        assertThat(actual)
                .as("Expecting correct date time with correct timezone offset")
                .isEqualTo("2004-02-03T10:05:06.050+0100");
    }

    @Test
    public void testParseValueDuringDaylightSavingTime() {
        int hour = 4;
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2004, 2, 3, hour, 5, 6, 50 * 1000000, ZoneId.of("Europe/Oslo"));
        String input = "2004-02-03T04:05:06.050+0100";
        ZonedDateTime actual = (ZonedDateTime) DateScalar.GraphQLDateScalar.getCoercing().parseValue(input);
        System.out.println(actual);
        assertThat(actual).isEqualTo(zonedDateTime);
        assertThat(actual.getHour())
                .as("During the daylight saving time in Oslo, the offset is +0100")
                .isEqualTo(hour);
    }

    @Test
    public void testParseValueDuringCentralEuropeanSummerTime() {
        int hour = 4;

        String input = "2017-06-06T0" + hour + ":05:06.050+0100";
        ZonedDateTime actual = (ZonedDateTime) DateScalar.GraphQLDateScalar.getCoercing().parseValue(input);
        System.out.println(actual);

        assertThat(actual.getHour())
                .as("During summer time in Oslo, the offset is +0200")
                .isEqualTo(hour+1);
    }

    /**
     * Test parsing date in different time zone offset.
     * Make sure it's converted to the default time zone and the hour is still correct.
     */
    @Test
    public void convertToAndReturnWithDefaultTimeZone() {
        String input = "2017-04-04T12:43:06.050-0300";
        ZonedDateTime actual = (ZonedDateTime) DateScalar.GraphQLDateScalar.getCoercing().parseValue(input);
        System.out.println(actual);
        assertThat(actual.getZone()).isEqualTo(ZoneId.of("Europe/Oslo"));
        assertThat(actual.getHour()).as("Hour should be correct").isEqualTo(17);
    }

    @Test
    public void parseExampleDate() {
        ZonedDateTime actual = (ZonedDateTime) DateScalar.GraphQLDateScalar.getCoercing().parseValue(EXAMPLE_DATE_TIME);
        System.out.println(actual);
        assertThat(actual.getZone()).isEqualTo(ZoneId.of("Europe/Oslo"));
    }

    /**
     * new Date().toISOString();
     */
    @Test
    public void parseDefaultJavaScriptNewDate() {
        String input = "2017-04-04T11:08:38.398Z";
        ZonedDateTime actual = (ZonedDateTime) DateScalar.GraphQLDateScalar.getCoercing().parseValue(input);
        System.out.println(actual);
        assertThat(actual.getZone()).isEqualTo(ZoneId.of("Europe/Oslo"));
        assertThat(actual.getHour()).as("Hour should be correct").isEqualTo(13);
    }
}