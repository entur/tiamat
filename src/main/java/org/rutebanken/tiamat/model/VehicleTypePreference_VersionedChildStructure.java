package org.rutebanken.tiamat.model;

import java.math.BigInteger;


public class VehicleTypePreference_VersionedChildStructure
        extends JourneyTiming_VersionedChildStructure {

    protected BigInteger rank;
    protected DayTypeRefStructure dayTypeRef;
    protected VehicleTypePreferenceRef vehicleTypePreferenceRef;

    public BigInteger getRank() {
        return rank;
    }

    public void setRank(BigInteger value) {
        this.rank = value;
    }

    public DayTypeRefStructure getDayTypeRef() {
        return dayTypeRef;
    }

    public void setDayTypeRef(DayTypeRefStructure value) {
        this.dayTypeRef = value;
    }

    public VehicleTypePreferenceRef getVehicleTypePreferenceRef() {
        return vehicleTypePreferenceRef;
    }

    public void setVehicleTypePreferenceRef(VehicleTypePreferenceRef value) {
        this.vehicleTypePreferenceRef = value;
    }

}
