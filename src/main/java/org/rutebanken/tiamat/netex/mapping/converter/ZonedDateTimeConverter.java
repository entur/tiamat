package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
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
