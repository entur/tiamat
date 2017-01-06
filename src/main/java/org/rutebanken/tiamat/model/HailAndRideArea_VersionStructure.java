package org.rutebanken.tiamat.model;

import java.math.BigInteger;


public class HailAndRideArea_VersionStructure
        extends FlexibleQuay_VersionStructure {

    protected String bearingCompass;
    protected BigInteger bearingDegrees;
    protected DestinationDisplayViews_RelStructure destinations;
    protected PointRefStructure startPointRef;
    protected PointRefStructure endPointRef;

    public String getBearingCompass() {
        return bearingCompass;
    }

    public void setBearingCompass(String value) {
        this.bearingCompass = value;
    }

    public BigInteger getBearingDegrees() {
        return bearingDegrees;
    }

    public void setBearingDegrees(BigInteger value) {
        this.bearingDegrees = value;
    }

    public DestinationDisplayViews_RelStructure getDestinations() {
        return destinations;
    }

    public void setDestinations(DestinationDisplayViews_RelStructure value) {
        this.destinations = value;
    }

    public PointRefStructure getStartPointRef() {
        return startPointRef;
    }

    public void setStartPointRef(PointRefStructure value) {
        this.startPointRef = value;
    }

    public PointRefStructure getEndPointRef() {
        return endPointRef;
    }

    public void setEndPointRef(PointRefStructure value) {
        this.endPointRef = value;
    }

}
