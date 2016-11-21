

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class StopAssignmentsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<JAXBElement<? extends StopAssignment_VersionStructure>> stopAssignment;

    public List<JAXBElement<? extends StopAssignment_VersionStructure>> getStopAssignment() {
        if (stopAssignment == null) {
            stopAssignment = new ArrayList<JAXBElement<? extends StopAssignment_VersionStructure>>();
        }
        return this.stopAssignment;
    }

}
