package org.rutebanken.tiamat.importer;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

public class PublicationDeliveryParams {

    @QueryParam(value = "onlyImportStopsInCounty")
    public List<String> onlyImportStopsInCounties = new ArrayList<>();

    @QueryParam(value = "importType")
    public ImportType importType = ImportType.MERGE;
}
