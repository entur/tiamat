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

public class JbvCodeMappingDto {
    public String originalId;
    public String platform;
    public String netexId;

    private static final String SEPARATOR = ",";
    private static final String CODE_PLATFORM_SEPARATOR = ":";

    public JbvCodeMappingDto(String originalId, String platform, String netexId) {
        this.originalId = originalId;
        this.netexId = netexId;
        this.platform = platform;
    }

    public String toCsvString() {
        return originalId + CODE_PLATFORM_SEPARATOR + platform + SEPARATOR + netexId;
    }

}
