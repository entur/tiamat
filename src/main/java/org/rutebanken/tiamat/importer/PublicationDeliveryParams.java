package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.model.StopTypeEnumeration;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PublicationDeliveryParams {

    /**
     * Only import stops that matches the list of counties.
     */
    @QueryParam(value = "targetTopographicPlaces")
    public List<String> targetTopographicPlaces = new ArrayList<>();

    /**
     * Look for matching stops outside the given counties.
     * If there is a match, only append the original ID.
     */
    @QueryParam(value = "onlyMatchOutsideTopographicPlaces")
    public List<String> onlyMatchOutsideTopographicPlaces = new ArrayList<>();

    @QueryParam(value = "importType")
    public ImportType importType = ImportType.MERGE;
    
    @QueryParam(value = "skipOutput")
    public boolean skipOutput = false;

    @QueryParam(value = "ignoreStopTypes")
    public Set<StopTypeEnumeration> ignoreStopTypes;

    @QueryParam(value = "allowOnlyStopTypes")
    public Set<StopTypeEnumeration> allowOnlyStopTypes;
}
