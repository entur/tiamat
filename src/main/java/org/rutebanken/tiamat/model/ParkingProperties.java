package org.rutebanken.tiamat.model;

import javax.persistence.*;
import javax.xml.datatype.Duration;
import java.util.ArrayList;
import java.util.List;


@Entity
public class ParkingProperties
        extends VersionedChildStructure {

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ParkingUserEnumeration.class, fetch = FetchType.EAGER)
    protected List<ParkingUserEnumeration> parkingUserTypes;

    @Transient
    protected List<ParkingVehicleEnumeration> parkingVehicleTypes;

    @Transient
    protected List<ParkingStayEnumeration> parkingStayList;

    @Transient
    protected Duration maximumStay;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<ParkingCapacity> spaces;

    @Transient
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

    public List<ParkingCapacity> getSpaces() {
        return spaces;
    }

    public void setSpaces(List<ParkingCapacity> value) {
        this.spaces = value;
    }

    public ParkingTariffRefs_RelStructure getCharges() {
        return charges;
    }

    public void setCharges(ParkingTariffRefs_RelStructure value) {
        this.charges = value;
    }

}
