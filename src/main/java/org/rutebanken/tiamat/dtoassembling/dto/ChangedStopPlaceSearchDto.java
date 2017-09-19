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

import javax.ws.rs.QueryParam;

/**
 * DTO for search for stop places with effective change in period.
 */
public class ChangedStopPlaceSearchDto {
    @QueryParam(value = "from")
    public String from;
    @QueryParam(value = "to")
    public String to;
    @QueryParam(value = "page")
    public int page = 0;
    @QueryParam(value = "per_page")
    public int perPage;

    public ChangedStopPlaceSearchDto() {
    }

    public ChangedStopPlaceSearchDto(String from, String to, int page, int perPage) {
        this.from = from;
        this.to = to;
        this.page = page;
        this.perPage = perPage;
    }
}
