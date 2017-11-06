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

package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.ValidBetween;
import org.rutebanken.tiamat.netex.mapping.NetexMappingContext;
import org.rutebanken.tiamat.netex.mapping.NetexMappingContextThreadLocal;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@Component
public class ValidBetweenConverter extends BidirectionalConverter<List<ValidBetween>, org.rutebanken.tiamat.model.ValidBetween> {

    private static final Logger logger = LoggerFactory.getLogger(ValidBetweenConverter.class);

    @Autowired
    private ExportTimeZone exportTimeZone;

    @Override
    public org.rutebanken.tiamat.model.ValidBetween convertTo(List<ValidBetween> validBetweens, Type<org.rutebanken.tiamat.model.ValidBetween> type, MappingContext mappingContext) {
        if (validBetweens == null || validBetweens.isEmpty()) {
            return null;
        } else {
            if (validBetweens.size() > 1) {
                logger.warn("Received multiple validBetween periods. Ignoring all but first");
            }
        }

        ZoneId mappingTimeZone = NetexMappingContextThreadLocal.get().defaultTimeZone;

        ValidBetween netexValidBetween = validBetweens.get(0);

        org.rutebanken.tiamat.model.ValidBetween tiamatValidBetween = new org.rutebanken.tiamat.model.ValidBetween();
        if (netexValidBetween.getFromDate() != null) {
            tiamatValidBetween.setFromDate(Instant.from(netexValidBetween.getFromDate().atZone(mappingTimeZone)));
        }
        if (netexValidBetween.getToDate() != null) {
            tiamatValidBetween.setToDate(Instant.from(netexValidBetween.getToDate().atZone(mappingTimeZone)));
        }

        return tiamatValidBetween;
    }

    @Override
    public List<ValidBetween> convertFrom(org.rutebanken.tiamat.model.ValidBetween validBetween, Type<List<ValidBetween>> type, MappingContext mappingContext) {
        org.rutebanken.netex.model.ValidBetween netexValidBetween = new org.rutebanken.netex.model.ValidBetween();

        if (validBetween.getFromDate() != null) {
            netexValidBetween.setFromDate(validBetween.getFromDate().atZone(exportTimeZone.getDefaultTimeZoneId()).toLocalDateTime());
        }
        if (validBetween.getToDate() != null) {
            netexValidBetween.setToDate(validBetween.getToDate().atZone(exportTimeZone.getDefaultTimeZoneId()).toLocalDateTime());
        }

        return Arrays.asList(netexValidBetween);
    }
}
