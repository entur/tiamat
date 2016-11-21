

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class VehicleTypesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<VehicleType_VersionStructure> compoundTrainOrTrainOrVehicleType;

    public List<VehicleType_VersionStructure> getCompoundTrainOrTrainOrVehicleType() {
        if (compoundTrainOrTrainOrVehicleType == null) {
            compoundTrainOrTrainOrVehicleType = new ArrayList<VehicleType_VersionStructure>();
        }
        return this.compoundTrainOrTrainOrVehicleType;
    }

}
