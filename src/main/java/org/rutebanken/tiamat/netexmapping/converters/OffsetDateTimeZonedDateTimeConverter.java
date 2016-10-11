package org.rutebanken.tiamat.netexmapping.converters;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

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
