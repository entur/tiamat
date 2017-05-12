package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZonedDateTime;

@Component
public class ZonedDateTimeInstantConverter extends BidirectionalConverter<ZonedDateTime, Instant> {

    @Autowired
    private ExportTimeZone exportTimeZone;

    @Override
    public Instant convertTo(ZonedDateTime zonedDateTime, Type<Instant> type) {
        return Instant.from(zonedDateTime);
    }

    @Override
    public ZonedDateTime convertFrom(Instant instant, Type<ZonedDateTime> type) {
        return instant.atZone(exportTimeZone.getDefaultTimeZone());
    }
}
