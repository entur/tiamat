

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class StairEndStructure {

    protected Boolean continuingHandrail;
    protected Boolean texturedSurface;
    protected Boolean visualContrast;

    public Boolean isContinuingHandrail() {
        return continuingHandrail;
    }

    public void setContinuingHandrail(Boolean value) {
        this.continuingHandrail = value;
    }

    public Boolean isTexturedSurface() {
        return texturedSurface;
    }

    public void setTexturedSurface(Boolean value) {
        this.texturedSurface = value;
    }

    public Boolean isVisualContrast() {
        return visualContrast;
    }

    public void setVisualContrast(Boolean value) {
        this.visualContrast = value;
    }

}
