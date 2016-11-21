

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


    "none",
public class Vias_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected String none;
    protected List<Via_VersionedChildStructure> via;

    public String getNone() {
        return none;
    }

    public void setNone(String value) {
        this.none = value;
    }

    public List<Via_VersionedChildStructure> getVia() {
        if (via == null) {
            via = new ArrayList<Via_VersionedChildStructure>();
        }
        return this.via;
    }

}
