package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.ValidBetween;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class ValidBetweenConverter extends BidirectionalConverter<ValidBetween, org.rutebanken.tiamat.model.ValidBetween> {

    private static final Logger logger = LoggerFactory.getLogger(ValidBetweenConverter.class);

    @Override
    public org.rutebanken.tiamat.model.ValidBetween convertTo(ValidBetween validBetween, Type<org.rutebanken.tiamat.model.ValidBetween> type) {
        logger.debug("Ignoring incoming availability conditions {}", validBetween);

        org.rutebanken.tiamat.model.ValidBetween tiamatValidBetween = new org.rutebanken.tiamat.model.ValidBetween();
        if (validBetween.getFromDate() != null) {
            tiamatValidBetween.setFromDate(ZonedDateTime.from(validBetween.getFromDate()));
        }
        if (validBetween.getToDate() != null) {
            tiamatValidBetween.setToDate(ZonedDateTime.from(validBetween.getToDate()));
        }

        return tiamatValidBetween;
    }

    @Override
    public ValidBetween convertFrom(org.rutebanken.tiamat.model.ValidBetween validBetween, Type<ValidBetween> type) {
        org.rutebanken.netex.model.ValidBetween netexValidBetween = new org.rutebanken.netex.model.ValidBetween();

        if(validBetween.getFromDate() != null) {
            netexValidBetween.setFromDate(validBetween.getFromDate().toOffsetDateTime());
        }
        if(validBetween.getToDate() != null) {
            netexValidBetween.setToDate(validBetween.getToDate().toOffsetDateTime());
        }

        return netexValidBetween;
    }
}
