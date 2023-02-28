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

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.rutebanken.tiamat.model.AlternativeName;

@Mapper(uses = {LocalDateTimeInstantConverter.class, ValidBetweenConverter.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlternativeNameMapper {

    org.rutebanken.netex.model.AlternativeName convertTo(AlternativeName alternativeName);

    AlternativeName convertFrom(org.rutebanken.netex.model.AlternativeName alternativeName);
}
