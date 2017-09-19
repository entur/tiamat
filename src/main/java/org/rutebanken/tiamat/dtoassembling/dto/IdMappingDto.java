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

package org.rutebanken.tiamat.dtoassembling.dto;

import org.rutebanken.tiamat.model.StopTypeEnumeration;

public class IdMappingDto {
    public String originalId;
    public String netexId;
    public StopTypeEnumeration stopType;

    private static final String SEPARATOR = ",";

    public IdMappingDto(String originalId, String netexId) {
        this(null, null, null);
    }

    public IdMappingDto(String originalId, String netexId, StopTypeEnumeration stopType) {
        this.originalId = originalId;
        this.netexId = netexId;
        this.stopType = stopType;
    }

    public String toCsvString(boolean includeStopType) {
        if (includeStopType) {
            return originalId + SEPARATOR + (stopType == null ? "" : stopType.value()) + SEPARATOR + netexId;
        }
        return originalId + SEPARATOR + netexId;
    }

}
