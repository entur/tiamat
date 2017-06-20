package org.rutebanken.tiamat.dtoassembling.dto;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Data transfer object for export parameters
 * Parameters specific for search related to a certain type like StopPlace does not necesarry belong here.
 * See also {@link StopPlaceSearchDto}
 */
public class ExportParamsDto {

    @QueryParam(value = "includeTopographicPlaces")
    public boolean includeTopographicPlaces = false;

    public ExportParamsDto() {
    }

    public ExportParamsDto(boolean includeTopographicPlaces) {
        this.includeTopographicPlaces = includeTopographicPlaces;
    }
}
