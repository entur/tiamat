package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class DefaultConnection_VersionStructure
        extends Transfer_VersionStructure {

    protected DefaultConnectionEndStructure from;
    protected DefaultConnectionEndStructure to;
    protected TopographicPlaceView topographicPlaceView;
    protected StopAreaRefStructure stopAreaRef;
    protected JAXBElement<? extends SiteElementRefStructure> siteElementRef;

    public DefaultConnectionEndStructure getFrom() {
        return from;
    }

    public void setFrom(DefaultConnectionEndStructure value) {
        this.from = value;
    }

    public DefaultConnectionEndStructure getTo() {
        return to;
    }

    public void setTo(DefaultConnectionEndStructure value) {
        this.to = value;
    }

    public TopographicPlaceView getTopographicPlaceView() {
        return topographicPlaceView;
    }

    public void setTopographicPlaceView(TopographicPlaceView value) {
        this.topographicPlaceView = value;
    }

    public StopAreaRefStructure getStopAreaRef() {
        return stopAreaRef;
    }

    public void setStopAreaRef(StopAreaRefStructure value) {
        this.stopAreaRef = value;
    }

    public JAXBElement<? extends SiteElementRefStructure> getSiteElementRef() {
        return siteElementRef;
    }

    public void setSiteElementRef(JAXBElement<? extends SiteElementRefStructure> value) {
        this.siteElementRef = value;
    }

}
