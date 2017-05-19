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
