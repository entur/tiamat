package org.rutebanken.tiamat.model;

public class StopArea_VersionStructure
        extends Zone_VersionStructure {

    protected String publicCode;
    protected StopAreaRefStructure parentStopAreaRef;
    protected TopographicPlace topographicPlace;
    protected TopographicPlaceView topographicPlaceView;

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public StopAreaRefStructure getParentStopAreaRef() {
        return parentStopAreaRef;
    }

    public void setParentStopAreaRef(StopAreaRefStructure value) {
        this.parentStopAreaRef = value;
    }

    public TopographicPlaceView getTopographicPlaceView() {
        return topographicPlaceView;
    }

    public void setTopographicPlaceView(TopographicPlaceView value) {
        this.topographicPlaceView = value;
    }

}
