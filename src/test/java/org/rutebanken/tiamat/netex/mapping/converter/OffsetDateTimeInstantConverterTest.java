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

package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.junit.Test;
import org.rutebanken.tiamat.time.ExportTimeZone;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;

public class OffsetDateTimeInstantConverterTest {

    private ExportTimeZone exportTimeZone = new ExportTimeZone();
    private OffsetDateTimeInstantConverter offsetDateTimeInstantConverter = new OffsetDateTimeInstantConverter(exportTimeZone);
    private MappingContext mappingContext = new MappingContext(new HashMap<>());
    private Type<OffsetDateTime> offsetDateTimeType = new TypeBuilder<OffsetDateTime>() {}.build();
    private Type<Instant> instantType = new TypeBuilder<Instant>() {}.build();

    @Test
    public void convertFrom() throws Exception {

        OffsetDateTime offsetDateTime = OffsetDateTime.now(exportTimeZone.getDefaultTimeZoneId());
        System.out.println("offset date time: " + offsetDateTime);
        Instant instant = offsetDateTimeInstantConverter.convertTo(offsetDateTime, instantType, mappingContext);
        System.out.println("converted to instant: " + instant);
        OffsetDateTime actual = offsetDateTimeInstantConverter.convertFrom(instant, offsetDateTimeType, mappingContext);
        System.out.println("converted back offset date time: " + actual);
        assertThat(actual).isEqualTo(offsetDateTime);

    }

}