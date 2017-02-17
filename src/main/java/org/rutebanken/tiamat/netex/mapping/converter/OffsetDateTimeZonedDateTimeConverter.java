package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Component
public class OffsetDateTimeZonedDateTimeConverter extends BidirectionalConverter<OffsetDateTime, ZonedDateTime> {

    @Override
    public ZonedDateTime convertTo(OffsetDateTime offsetDateTime, Type<ZonedDateTime> type) {
        return ZonedDateTime.from(offsetDateTime);
    }

    @Override
    public OffsetDateTime convertFrom(ZonedDateTime zonedDateTime, Type<OffsetDateTime> type) {
        return zonedDateTime.toOffsetDateTime();
    }
}
