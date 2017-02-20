package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

@Component
public class DurationConverter extends BidirectionalConverter<Duration, java.time.Duration> {


    private DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();

    public DurationConverter() throws DatatypeConfigurationException {
    }

    @Override
    public java.time.Duration convertTo(Duration duration, Type<java.time.Duration> type) {
        return java.time.Duration.ofSeconds(duration.getSeconds());
    }

    @Override
    public Duration convertFrom(java.time.Duration duration, Type<Duration> type) {
        return datatypeFactory.newDuration(duration.toMillis());
    }
}
