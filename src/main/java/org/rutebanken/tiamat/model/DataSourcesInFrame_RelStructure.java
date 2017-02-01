package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class DataSourcesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<DataSource> dataSource;

    public List<DataSource> getDataSource() {
        if (dataSource == null) {
            dataSource = new ArrayList<DataSource>();
        }
        return this.dataSource;
    }

}
