/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.netex.mapping.mapper.mapStruct;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.rutebanken.netex.model.ValidBetween;
import org.rutebanken.tiamat.netex.mapping.NetexMappingContextThreadLocal;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public class ValidBetweenConverter {

    private static final Logger logger = LoggerFactory.getLogger(ValidBetweenConverter.class);

    private final ExportTimeZone exportTimeZone;

    public ValidBetweenConverter(ExportTimeZone exportTimeZone) {
        this.exportTimeZone = exportTimeZone;
    }

    public org.rutebanken.tiamat.model.ValidBetween convertTo(ValidBetween netexValidBetween) {
        ZoneId mappingTimeZone = NetexMappingContextThreadLocal.get().defaultTimeZone;

        org.rutebanken.tiamat.model.ValidBetween tiamatValidBetween = new org.rutebanken.tiamat.model.ValidBetween();
        if (netexValidBetween.getFromDate() != null) {
            tiamatValidBetween.setFromDate(Instant.from(netexValidBetween.getFromDate().atZone(mappingTimeZone)));
        }
        if (netexValidBetween.getToDate() != null) {
            tiamatValidBetween.setToDate(Instant.from(netexValidBetween.getToDate().atZone(mappingTimeZone)));
        }

        return tiamatValidBetween;
    }

    public ValidBetween convertFrom(org.rutebanken.tiamat.model.ValidBetween validBetween) {
        ValidBetween netexValidBetween = new ValidBetween();

        if (validBetween.getFromDate() != null) {
            netexValidBetween.setFromDate(validBetween.getFromDate().atZone(exportTimeZone.getDefaultTimeZoneId()).toLocalDateTime());
        }
        if (validBetween.getToDate() != null) {
            netexValidBetween.setToDate(validBetween.getToDate().atZone(exportTimeZone.getDefaultTimeZoneId()).toLocalDateTime());
        }

        return List.of(netexValidBetween);
    }
}
