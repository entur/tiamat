package org.rutebanken.tiamat.nvdb.service;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.nvdb.model.Egenskap;
import org.rutebanken.tiamat.nvdb.model.EnumVerdi;
import org.rutebanken.tiamat.nvdb.model.VegObjekt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Read data from NVDB and map fields to Quay.
 */
@Component
public class NvdbStopPlaceTypeMapper {

    private static final Logger logger = LoggerFactory.getLogger(NvdbStopPlaceTypeMapper.class);

    public static final int EGENSKAP_TYPE = 3956;
    public static final int ENUM_ID_HOLDEPLASS_LOMME = 5078;
    public static final int ENUM_ID_ANDRE = 5081;

    public StopPlace augmentFromNvdb(final StopPlace stopPlace, VegObjekt vegObjekt) {


       vegObjekt.getEgenskaper()
                .stream()
                .filter(e -> e.getId() == EGENSKAP_TYPE)
                .map(Egenskap::getEnumVerdi)
                .findFirst()
                .map(this::toStopPlaceType)
                .ifPresent(stopPlace::setStopPlaceType);


        logger.info("stop type: {}", stopPlace.getStopPlaceType());
        return stopPlace;
    }

    public StopTypeEnumeration toStopPlaceType(EnumVerdi enumVerdi) {
        StopTypeEnumeration result;

        if(enumVerdi.getId() == ENUM_ID_HOLDEPLASS_LOMME) result = StopTypeEnumeration.ONSTREET_BUS;
        else if(enumVerdi.getId() == ENUM_ID_ANDRE) result = StopTypeEnumeration.OTHER;
        else result = StopTypeEnumeration.ONSTREET_BUS;

        logger.debug("Mapped enumVerdi {} {} to {}", enumVerdi.getId(), enumVerdi.getVerdi(), result);
        return result;
    }
}
