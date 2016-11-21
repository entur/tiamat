

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class CheckConstraintThroughputs_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<Object> checkConstraintThroughputRefOrCheckConstraintThroughput;

    public List<Object> getCheckConstraintThroughputRefOrCheckConstraintThroughput() {
        if (checkConstraintThroughputRefOrCheckConstraintThroughput == null) {
            checkConstraintThroughputRefOrCheckConstraintThroughput = new ArrayList<Object>();
        }
        return this.checkConstraintThroughputRefOrCheckConstraintThroughput;
    }

}
