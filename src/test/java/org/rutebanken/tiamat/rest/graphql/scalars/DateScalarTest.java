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

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.rest.graphql.scalars.DateScalar.EXAMPLE_DATE_TIME;

public class DateScalarTest extends TiamatIntegrationTest {

    @Autowired
    private DateScalar dateScalar;

    @Test
    public void serializeDateTimeInAnyTimeZoneAndReturnCorrectOffset() {
        Instant instant = ZonedDateTime.of(2004, 2, 3, 4, 5, 6, 50 * 1000000, ZoneId.of("Chile/EasterIsland")).toInstant();

        String actual = (String) dateScalar.getGraphQLDateScalar().getCoercing().serialize(instant);

        System.out.println(actual);
        assertThat(actual)
                .as("Expecting correct date time with correct timezone offset")
                .isEqualTo("2004-02-03T10:05:06.050+0100");
    }

    /**
     * Test parsing date in different time zone offset.
     * Make sure it's converted to UTC and the hour is still correct.
     */
    @Test
    public void convertToAndReturnWithUTCTimeZone() {
        String input = "2017-04-04T12:43:06.050-0300";
        Instant actual = (Instant) dateScalar.getGraphQLDateScalar().getCoercing().parseValue(input);
        System.out.println(actual);

        assertThat(LocalDateTime.ofInstant(actual, ZoneOffset.UTC).getHour()).as("Hour should be correct").isEqualTo(15);
    }

    @Test
    public void parseExampleDate() {
        Instant actual = (Instant) dateScalar.getGraphQLDateScalar().getCoercing().parseValue(EXAMPLE_DATE_TIME);
        System.out.println(actual);
        assertThat(LocalDateTime.ofInstant(actual, ZoneOffset.UTC).getHour()).as("Hour should be correct").isEqualTo(17);
    }

    @Test
    public void parseDateTimeWithoutMilliseconds() {
        Instant actual = (Instant) dateScalar.getGraphQLDateScalar().getCoercing().parseValue( "2017-04-23T18:25:43Z");
        System.out.println(actual);
        assertThat(LocalDateTime.ofInstant(actual, ZoneOffset.UTC).getHour()).as("Hour should be correct").isEqualTo(18);
    }

    @Test
    public void parseDateTimeWithMicroseconds() {
        Instant actual = (Instant) dateScalar.getGraphQLDateScalar().getCoercing().parseValue( "2017-04-23T18:25:43.123456+0100");
        System.out.println(actual);
        assertThat(LocalDateTime.ofInstant(actual, ZoneOffset.UTC).getHour()).as("Hour should be correct").isEqualTo(17);
    }
    /**
     * new Date().toISOString();
     */
    @Test
    public void parseDefaultJavaScriptNewDate() {
        String input = "2017-04-04T11:08:38.398Z";
        Instant actual = (Instant) dateScalar.getGraphQLDateScalar().getCoercing().parseValue(input);
        System.out.println(actual);
        assertThat(LocalDateTime.ofInstant(actual, ZoneOffset.UTC).getHour()).as("Hour should be correct").isEqualTo(11);
    }
}