

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class DataSources_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Object> dataSourceRefOrDataSource;

    public List<Object> getDataSourceRefOrDataSource() {
        if (dataSourceRefOrDataSource == null) {
            dataSourceRefOrDataSource = new ArrayList<Object>();
        }
        return this.dataSourceRefOrDataSource;
    }

}
