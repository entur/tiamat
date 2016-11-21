package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class DataSources_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> dataSourceRefOrDataSource;

    public List<Object> getDataSourceRefOrDataSource() {
        if (dataSourceRefOrDataSource == null) {
            dataSourceRefOrDataSource = new ArrayList<Object>();
        }
        return this.dataSourceRefOrDataSource;
    }

}
