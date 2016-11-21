

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class DataSourcesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<DataSource> dataSource;

    public List<DataSource> getDataSource() {
        if (dataSource == null) {
            dataSource = new ArrayList<DataSource>();
        }
        return this.dataSource;
    }

}
