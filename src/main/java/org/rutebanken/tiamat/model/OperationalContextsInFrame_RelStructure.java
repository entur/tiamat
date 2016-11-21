

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class OperationalContextsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<OperationalContext> operationalContext;

    public List<OperationalContext> getOperationalContext() {
        if (operationalContext == null) {
            operationalContext = new ArrayList<OperationalContext>();
        }
        return this.operationalContext;
    }

}
