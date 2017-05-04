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

    @Autowired
    private ExportTimeZone exportTimeZone;

    @Override
    public org.rutebanken.tiamat.model.ValidBetween convertTo(ValidBetween netexValidBetween, Type<org.rutebanken.tiamat.model.ValidBetween> type) {
        org.rutebanken.tiamat.model.ValidBetween validBetween = new org.rutebanken.tiamat.model.ValidBetween();

        if (netexValidBetween.getFromDate() != null) {
            validBetween.setFromDate(netexValidBetween.getFromDate().toInstant());
        }

        if (netexValidBetween.getToDate() != null) {
            validBetween.setToDate(netexValidBetween.getToDate().toInstant());
        }

        return validBetween;
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
