

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class VehicleQuayAlignments_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<VehicleQuayAlignment> vehicleQuayAlignment;

    public List<VehicleQuayAlignment> getVehicleQuayAlignment() {
        if (vehicleQuayAlignment == null) {
            vehicleQuayAlignment = new ArrayList<VehicleQuayAlignment>();
        }
        return this.vehicleQuayAlignment;
    }

}
