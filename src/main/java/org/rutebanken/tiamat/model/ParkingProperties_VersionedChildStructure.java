

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;


    "parkingUserTypes",
    "parkingVehicleTypes",
    "parkingStayList",
    "maximumStay",
    "spaces",
public class ParkingProperties_VersionedChildStructure
    extends VersionedChildStructure
{

    protected List<ParkingUserEnumeration> parkingUserTypes;
    protected List<ParkingVehicleEnumeration> parkingVehicleTypes;
    protected List<ParkingStayEnumeration> parkingStayList;
    protected Duration maximumStay;
    protected ParkingCapacities_RelStructure spaces;
    protected ParkingTariffRefs_RelStructure charges;

    public List<ParkingUserEnumeration> getParkingUserTypes() {
        if (parkingUserTypes == null) {
            parkingUserTypes = new ArrayList<ParkingUserEnumeration>();
        }
        return this.parkingUserTypes;
    }

    public List<ParkingVehicleEnumeration> getParkingVehicleTypes() {
        if (parkingVehicleTypes == null) {
            parkingVehicleTypes = new ArrayList<ParkingVehicleEnumeration>();
        }
        return this.parkingVehicleTypes;
    }

    public List<ParkingStayEnumeration> getParkingStayList() {
        if (parkingStayList == null) {
            parkingStayList = new ArrayList<ParkingStayEnumeration>();
        }
        return this.parkingStayList;
    }

    public Duration getMaximumStay() {
        return maximumStay;
    }

    public void setMaximumStay(Duration value) {
        this.maximumStay = value;
    }

    public ParkingCapacities_RelStructure getSpaces() {
        return spaces;
    }

    public void setSpaces(ParkingCapacities_RelStructure value) {
        this.spaces = value;
    }

    public ParkingTariffRefs_RelStructure getCharges() {
        return charges;
    }

    public void setCharges(ParkingTariffRefs_RelStructure value) {
        this.charges = value;
    }

}
