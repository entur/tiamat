package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.ValidBetween;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidBetweenConverter extends BidirectionalConverter<ValidBetween, org.rutebanken.tiamat.model.ValidBetween> {

    private static final Logger logger = LoggerFactory.getLogger(ValidBetweenConverter.class);

    @Autowired
    private ExportTimeZone exportTimeZone;

    @Override
    public org.rutebanken.tiamat.model.ValidBetween convertTo(ValidBetween validBetween, Type<org.rutebanken.tiamat.model.ValidBetween> type) {
        logger.debug("Ignoring incoming availability conditions {}", validBetween);
        return null;
    }

    @Override
    public ValidBetween convertFrom(org.rutebanken.tiamat.model.ValidBetween validBetween, Type<ValidBetween> type) {
        org.rutebanken.netex.model.ValidBetween netexValidBetween = new org.rutebanken.netex.model.ValidBetween();

        if(validBetween.getFromDate() != null) {
            netexValidBetween.setFromDate(validBetween.getFromDate().atZone(exportTimeZone.getDefaultTimeZone()).toOffsetDateTime());
        }
        if(validBetween.getToDate() != null) {
            netexValidBetween.setToDate(validBetween.getToDate().atZone(exportTimeZone.getDefaultTimeZone()).toOffsetDateTime());
        }

        return netexValidBetween;
    }
}
