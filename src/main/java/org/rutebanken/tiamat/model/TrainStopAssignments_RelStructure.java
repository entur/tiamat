

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class TrainStopAssignments_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Object> trainStopAssignmentRefOrTrainStopAssignment;

    public List<Object> getTrainStopAssignmentRefOrTrainStopAssignment() {
        if (trainStopAssignmentRefOrTrainStopAssignment == null) {
            trainStopAssignmentRefOrTrainStopAssignment = new ArrayList<Object>();
        }
        return this.trainStopAssignmentRefOrTrainStopAssignment;
    }

}
