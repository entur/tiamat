

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class VehicleTypePreference_VersionedChildStructure
    extends JourneyTiming_VersionedChildStructure
{

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
