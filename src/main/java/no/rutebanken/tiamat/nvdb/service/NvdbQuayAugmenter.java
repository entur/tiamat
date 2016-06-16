package no.rutebanken.tiamat.nvdb.service;

import no.rutebanken.tiamat.nvdb.model.Egenskap;
import no.rutebanken.tiamat.nvdb.model.EnumVerdi;
import no.rutebanken.tiamat.nvdb.model.VegObjekt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import no.rutebanken.tiamat.model.Quay;
import no.rutebanken.tiamat.model.QuayTypeEnumeration;

/**
 * Read data from NVDB and map fields to Quay.
 */
@Component
public class NvdbQuayAugmenter {

    private static final Logger logger = LoggerFactory.getLogger(NvdbQuayAugmenter.class);

    public static final int EGENSKAP_TYPE = 3956;
    public static final int ENUM_ID_HOLDEPLASS_LOMME = 5078;
    public static final int ENUM_ID_ANDRE = 5081;

    public Quay augmentFromNvdb(final Quay quay, VegObjekt vegObjekt) {


       vegObjekt.getEgenskaper()
                .stream()
                .filter(e -> e.getId() == EGENSKAP_TYPE)
                .map(Egenskap::getEnumVerdi)
                .findFirst()
                .map(this::toQuayType)
                .ifPresent(quay::setQuayType);


        logger.info("quay type: {}", quay.getQuayType());
        return quay;
    }

    public QuayTypeEnumeration toQuayType(EnumVerdi enumVerdi) {
        QuayTypeEnumeration result;

        if(enumVerdi.getId() == ENUM_ID_HOLDEPLASS_LOMME) result = QuayTypeEnumeration.BUS_BAY;
        else if(enumVerdi.getId() == ENUM_ID_ANDRE) result = QuayTypeEnumeration.OTHER;
        else result = QuayTypeEnumeration.BUS_STOP;

        logger.debug("Mapped enumVerdi {} {} to {}", enumVerdi.getId(), enumVerdi.getVerdi(), result);
        return result;
    }
}
