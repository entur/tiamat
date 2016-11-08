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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DestinationDisplayView> destinations;
    //protected DestinationDisplayViews_RelStructure destinations;

    @Transient
    protected Float compassBearing;

    @Enumerated(EnumType.STRING)
    protected CompassBearing8Enumeration compassOctant;

    @Enumerated(EnumType.STRING)
    protected QuayTypeEnumeration quayType;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    protected QuayReference parentQuayRef;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private final List<BoardingPosition> boardingPositions = new ArrayList<>();
    //protected BoardingPositions_RelStructure boardingPositions;

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

     /*   public DestinationDisplayViews_RelStructure getDestinations() {
        return destinations;
    }
*/
    //   public void setDestinations(DestinationDisplayViews_RelStructure value) {
    //       this.destinations = value;
    //   }

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


       /* public BoardingPositions_RelStructure getBoardingPositions() {
        return boardingPositions;
    }*

       /* public void setBoardingPositions(BoardingPositions_RelStructure value) {
        this.boardingPositions = value;
    }*/
//
//    @Override
//    public boolean equals(Object other) {
//        if(this == other) {
//            return true;
//        } else if (!(other instanceof Quay)) {
//            return false;
//        }
//
//        Quay otherQuay = (Quay) other;
//        boolean nameEquals = false;
//        if (this.getName() == null && otherQuay.getName() == null) {
//            nameEquals = true;
//        } else if (this.getName() != null
//                && otherQuay.getName() != null
//                && this.getName().getValue().equals(otherQuay.getName().getValue())) {
//            nameEquals = true;
//        }
//
////        )
////
////        boolean coordinatesEquals = this.getCentroid() != null && otherQuay.getCentroid() != null
////                && this.centroid.getLocation() != null && otherQuay.getCentroid().getLocation() != null
////                && this.centroid.getLocation().getLatitude().equals(otherQuay.getCentroid().getLocation().getLatitude())
////                && this.centroid.getLocation().getLongitude().equals(otherQuay.getCentroid().getLocation().getLongitude());
////
//        return nameEquals;
//
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(quayType, name.getValue());
//    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", id)
                .add("name", name)
                .add("keyValues", getKeyValues())
                .toString();
    }

}
