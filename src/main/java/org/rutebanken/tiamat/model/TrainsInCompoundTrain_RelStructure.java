

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TrainsInCompoundTrain_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<TrainInCompoundTrain_VersionedChildStructure> trainInCompoundTrain;

    public List<TrainInCompoundTrain_VersionedChildStructure> getTrainInCompoundTrain() {
        if (trainInCompoundTrain == null) {
            trainInCompoundTrain = new ArrayList<TrainInCompoundTrain_VersionedChildStructure>();
        }
        return this.trainInCompoundTrain;
    }

}
