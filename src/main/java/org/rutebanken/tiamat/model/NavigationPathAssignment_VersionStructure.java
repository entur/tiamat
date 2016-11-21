

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "connectionRef",
    "siteRef",
public class NavigationPathAssignment_VersionStructure
    extends StopAssignment_VersionStructure
{

    protected JAXBElement<? extends ConnectionRefStructure> connectionRef;
    protected JAXBElement<? extends SiteRefStructure> siteRef;
    protected NavigationPathRefStructure navigationPathRef;

    public JAXBElement<? extends ConnectionRefStructure> getConnectionRef() {
        return connectionRef;
    }

    public void setConnectionRef(JAXBElement<? extends ConnectionRefStructure> value) {
        this.connectionRef = value;
    }

    public JAXBElement<? extends SiteRefStructure> getSiteRef() {
        return siteRef;
    }

    public void setSiteRef(JAXBElement<? extends SiteRefStructure> value) {
        this.siteRef = value;
    }

    public NavigationPathRefStructure getNavigationPathRef() {
        return navigationPathRef;
    }

    public void setNavigationPathRef(NavigationPathRefStructure value) {
        this.navigationPathRef = value;
    }

}
