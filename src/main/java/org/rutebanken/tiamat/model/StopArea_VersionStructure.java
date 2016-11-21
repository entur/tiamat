

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class StopArea_VersionStructure
    extends Zone_VersionStructure
{

    protected String publicCode;
    protected StopAreaRefStructure parentStopAreaRef;
    protected TopographicPlaceRefStructure topographicPlaceRef;
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

    public TopographicPlaceRefStructure getTopographicPlaceRef() {
        return topographicPlaceRef;
    }

    public void setTopographicPlaceRef(TopographicPlaceRefStructure value) {
        this.topographicPlaceRef = value;
    }

    public TopographicPlaceView getTopographicPlaceView() {
        return topographicPlaceView;
    }

    public void setTopographicPlaceView(TopographicPlaceView value) {
        this.topographicPlaceView = value;
    }

}
