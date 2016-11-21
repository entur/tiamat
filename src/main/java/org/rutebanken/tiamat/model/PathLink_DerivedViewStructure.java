

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "hideLink",
    "hideDestination",
    "showEntranceSeparately",
    "showExitSeparately",
public class PathLink_DerivedViewStructure
    extends DerivedViewStructure
{

    protected Boolean hideLink;
    protected Boolean hideDestination;
    protected Boolean showEntranceSeparately;
    protected Boolean showExitSeparately;
    protected Boolean showHeadingSeparately;

    public Boolean isHideLink() {
        return hideLink;
    }

    public void setHideLink(Boolean value) {
        this.hideLink = value;
    }

    public Boolean isHideDestination() {
        return hideDestination;
    }

    public void setHideDestination(Boolean value) {
        this.hideDestination = value;
    }

    public Boolean isShowEntranceSeparately() {
        return showEntranceSeparately;
    }

    public void setShowEntranceSeparately(Boolean value) {
        this.showEntranceSeparately = value;
    }

    public Boolean isShowExitSeparately() {
        return showExitSeparately;
    }

    public void setShowExitSeparately(Boolean value) {
        this.showExitSeparately = value;
    }

    public Boolean isShowHeadingSeparately() {
        return showHeadingSeparately;
    }

    public void setShowHeadingSeparately(Boolean value) {
        this.showHeadingSeparately = value;
    }

}
