package no.rutebanken.tiamat.netexmapping.converters;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.time.ZonedDateTime;

public class ZonedDateTimeConverter extends BidirectionalConverter<ZonedDateTime, ZonedDateTime> {

    @Override
    public ZonedDateTime convertTo(ZonedDateTime zonedDateTime, Type<ZonedDateTime> type) {
        return ZonedDateTime.from(zonedDateTime);
    }

    @Override
    public ZonedDateTime convertFrom(ZonedDateTime zonedDateTime, Type<ZonedDateTime> type) {
        return ZonedDateTime.from(zonedDateTime);
    }
}
