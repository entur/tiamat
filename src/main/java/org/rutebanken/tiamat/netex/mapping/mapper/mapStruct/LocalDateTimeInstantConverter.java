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
import org.rutebanken.tiamat.time.ExportTimeZone;

import java.time.Instant;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public abstract class LocalDateTimeInstantConverter {

    private final ExportTimeZone exportTimeZone;

    public LocalDateTimeInstantConverter(ExportTimeZone exportTimeZone) {
        this.exportTimeZone = exportTimeZone;
    }

    public Instant convertTo(LocalDateTime localDateTime) {
        return localDateTime.atZone(exportTimeZone.getDefaultTimeZoneId()).toInstant();
    }

    public LocalDateTime convertFrom(Instant instant) {
        return instant.atZone(exportTimeZone.getDefaultTimeZoneId()).toLocalDateTime();
    }
}
