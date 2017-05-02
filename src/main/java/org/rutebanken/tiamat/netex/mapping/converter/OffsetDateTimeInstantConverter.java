package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;

@Component
public class OffsetDateTimeInstantConverter extends BidirectionalConverter<OffsetDateTime, Instant> {


    @Autowired
    private ExportTimeZone exportTimeZone;

    @Override
    public Instant convertTo(OffsetDateTime offsetDateTime, Type<Instant> type) {
        return Instant.from(offsetDateTime);
    }

    @Override
    public OffsetDateTime convertFrom(Instant instant, Type<OffsetDateTime> type) {
        return instant.atZone(exportTimeZone.getDefaultTimeZone()).toOffsetDateTime();
    }
}
