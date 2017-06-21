package org.rutebanken.tiamat.exporter.params;

import javax.ws.rs.BeanParam;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Export parameters.
 * Parameters specific for search related to a certain type like StopPlace does not necessary belong here.
 */
public class ExportParams {

    @QueryParam(value = "includeTopographicPlaces")
    public boolean includeTopographicPlaces = false;

    @QueryParam(value = "municipalityReference")
    public List<String> municipalityReferences;

    @QueryParam(value = "countyReference")
    public List<String> countyReferences;

    @BeanParam
    public StopPlaceSearch stopPlaceSearch;

}
