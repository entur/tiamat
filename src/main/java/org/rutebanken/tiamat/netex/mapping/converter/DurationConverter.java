package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class DurationConverter extends BidirectionalConverter<Duration, Duration> {

    @Override
    public Duration convertTo(Duration duration, Type<Duration> type) {
        return Duration.from(duration);
    }

    @Override
    public Duration convertFrom(Duration duration, Type<Duration> type) {
        return Duration.from(duration);
    }
}
