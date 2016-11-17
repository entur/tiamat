package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Quay extends StopPlaceSpace_VersionStructure {

    protected String publicCode;

    protected String plateCode;

    protected BigInteger shortCode;

    @OneToMany(cascade = CascadeType.ALL)
    private List<DestinationDisplayView> destinations;

    protected Float compassBearing;

    @Transient
    protected CompassBearing8Enumeration compassOctant;

    @Enumerated(EnumType.STRING)
    protected QuayTypeEnumeration quayType;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    protected QuayReference parentQuayRef;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<BoardingPosition> boardingPositions = new ArrayList<>();

    public Quay(EmbeddableMultilingualString name) {
        super(name);
    }

    public Quay() {
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public String getPlateCode() {
        return plateCode;
    }

    public void setPlateCode(String value) {
        this.plateCode = value;
    }

    public BigInteger getShortCode() {
        return shortCode;
    }

    public void setShortCode(BigInteger value) {
        this.shortCode = value;
    }

    public Float getCompassBearing() {
        return compassBearing;
    }

    public void setCompassBearing(Float value) {
        this.compassBearing = value;
    }

    public CompassBearing8Enumeration getCompassOctant() {
        return compassOctant;
    }

    public void setCompassOctant(CompassBearing8Enumeration value) {
        this.compassOctant = value;
    }

    public QuayTypeEnumeration getQuayType() {
        return quayType;
    }

    public void setQuayType(QuayTypeEnumeration value) {
        this.quayType = value;
    }

    public QuayReference getParentQuayRef() {
        return parentQuayRef;
    }

    public void setParentQuayRef(QuayReference value) {
        this.parentQuayRef = value;
    }

    public List<DestinationDisplayView> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<DestinationDisplayView> destinations) {
        this.destinations = destinations;
    }

    public List<BoardingPosition> getBoardingPositions() {
        return boardingPositions;
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) {
            return true;
        } else if (!(object instanceof Quay)) {
            return false;
        }

        Quay other = (Quay) object;

        return Objects.equals(this.name, other.name)
                && Objects.equals(this.centroid, other.centroid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, centroid);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", id)
                .add("name", name)
                .add("keyValues", getKeyValues())
                .add("centroid", centroid)
                .toString();
    }
}
