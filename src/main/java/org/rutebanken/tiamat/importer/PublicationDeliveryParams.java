package org.rutebanken.tiamat.importer;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

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
    
    
    
}
