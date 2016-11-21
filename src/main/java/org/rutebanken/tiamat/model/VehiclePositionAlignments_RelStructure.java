

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class VehiclePositionAlignments_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<VehiclePositionAlignment> vehiclePositionAlignment;

    public List<VehiclePositionAlignment> getVehiclePositionAlignment() {
        if (vehiclePositionAlignment == null) {
            vehiclePositionAlignment = new ArrayList<VehiclePositionAlignment>();
        }
        return this.vehiclePositionAlignment;
    }

}
