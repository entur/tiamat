

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "from",
    "to",
public class SiteConnection_VersionStructure
    extends Transfer_VersionStructure
{

    protected SiteConnectionEndStructure from;
    protected SiteConnectionEndStructure to;
    protected NavigationPaths_RelStructure navigationPaths;

    public SiteConnectionEndStructure getFrom() {
        return from;
    }

    public void setFrom(SiteConnectionEndStructure value) {
        this.from = value;
    }

    public SiteConnectionEndStructure getTo() {
        return to;
    }

    public void setTo(SiteConnectionEndStructure value) {
        this.to = value;
    }

    public NavigationPaths_RelStructure getNavigationPaths() {
        return navigationPaths;
    }

    public void setNavigationPaths(NavigationPaths_RelStructure value) {
        this.navigationPaths = value;
    }

}
