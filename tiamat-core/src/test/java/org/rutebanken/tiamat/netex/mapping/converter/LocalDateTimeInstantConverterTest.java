package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.junit.Test;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalDateTimeInstantConverterTest {

    private final LocalDateTimeInstantConverter converter = new LocalDateTimeInstantConverter();
    private final MappingContext mappingContext = new MappingContext(new HashMap<>());
    private final Type<Instant> instantType = new TypeBuilder<Instant>() {}.build();

    public LocalDateTimeInstantConverterTest() {
        ReflectionTestUtils.setField(converter, "exportTimeZone", new ExportTimeZone());
    }

    /** PostgreSQL would round .305742539 up to .305743, after the value being imported. */
    @Test
    public void convertToTruncatesToMicroseconds() {
        LocalDateTime nanos = LocalDateTime.parse("2026-10-09T09:52:07.305742539");

        Instant instant = converter.convertTo(nanos, instantType, mappingContext);

        assertThat(instant).isEqualTo(Instant.parse("2026-10-09T07:52:07.305742Z"));
    }

    @Test
    public void convertToKeepsMicroseconds() {
        LocalDateTime micros = LocalDateTime.parse("2025-11-24T10:34:52.298046");

        Instant instant = converter.convertTo(micros, instantType, mappingContext);

        assertThat(instant).isEqualTo(Instant.parse("2025-11-24T09:34:52.298046Z"));
    }
}
