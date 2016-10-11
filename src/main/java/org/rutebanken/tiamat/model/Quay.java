package org.rutebanken.tiamat.model;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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

}
