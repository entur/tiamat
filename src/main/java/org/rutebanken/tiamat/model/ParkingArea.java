package org.rutebanken.tiamat.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.math.BigInteger;


@Entity
public class ParkingArea
        extends ParkingComponent_VersionStructure {

    protected BigInteger totalCapacity;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    protected ParkingProperties parkingProperties;

    @Transient
    protected ParkingBays_RelStructure bays;

    @Transient
    protected EntranceRefs_RelStructure entrances;

    public BigInteger getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(BigInteger value) {
        this.totalCapacity = value;
    }

    public ParkingProperties getParkingProperties() {
        return parkingProperties;
    }

    public void setParkingProperties(ParkingProperties value) {
        this.parkingProperties = value;
    }

    public ParkingBays_RelStructure getBays() {
        return bays;
    }

    public void setBays(ParkingBays_RelStructure value) {
        this.bays = value;
    }

    public EntranceRefs_RelStructure getEntrances() {
        return entrances;
    }

    public void setEntrances(EntranceRefs_RelStructure value) {
        this.entrances = value;
    }

}
