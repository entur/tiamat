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
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class LocalDateTimeInstantConverter extends BidirectionalConverter<LocalDateTime, Instant> {

    @Autowired
    private ExportTimeZone exportTimeZone;

    /**
     * Truncated to microseconds, the precision PostgreSQL stores. PostgreSQL rounds a finer value, so an
     * imported nanosecond date could be stored later than itself and fail the version validity check on re-import.
     */
    @Override
    public Instant convertTo(LocalDateTime localDateTime, Type<Instant> type, MappingContext mappingContext) {
        return localDateTime.atZone(exportTimeZone.getDefaultTimeZoneId()).toInstant().truncatedTo(ChronoUnit.MICROS);
    }

    @Override
    public LocalDateTime convertFrom(Instant instant, Type<LocalDateTime> type, MappingContext mappingContext) {
        return instant.atZone(exportTimeZone.getDefaultTimeZoneId()).toLocalDateTime();
    }
}
