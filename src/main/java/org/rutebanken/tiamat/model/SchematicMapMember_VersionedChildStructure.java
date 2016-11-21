

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


    "name",
    "versionOfObjectRef",
    "hide",
    "displayAsIcon",
    "infoLink",
    "x",
    "y",
public class SchematicMapMember_VersionedChildStructure
    extends VersionedChildStructure
{

    protected MultilingualStringEntity name;
    protected JAXBElement<? extends VersionOfObjectRefStructure> versionOfObjectRef;
    protected Boolean hide;
    protected Boolean displayAsIcon;
    protected InfoLinkStructure infoLink;
    protected Float x;
    protected Float y;
    protected JAXBElement<? extends Projection_VersionStructure> projection;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public JAXBElement<? extends VersionOfObjectRefStructure> getVersionOfObjectRef() {
        return versionOfObjectRef;
    }

    public void setVersionOfObjectRef(JAXBElement<? extends VersionOfObjectRefStructure> value) {
        this.versionOfObjectRef = value;
    }

    public Boolean isHide() {
        return hide;
    }

    public void setHide(Boolean value) {
        this.hide = value;
    }

    public Boolean isDisplayAsIcon() {
        return displayAsIcon;
    }

    public void setDisplayAsIcon(Boolean value) {
        this.displayAsIcon = value;
    }

    public InfoLinkStructure getInfoLink() {
        return infoLink;
    }

    public void setInfoLink(InfoLinkStructure value) {
        this.infoLink = value;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float value) {
        this.x = value;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float value) {
        this.y = value;
    }

    public JAXBElement<? extends Projection_VersionStructure> getProjection() {
        return projection;
    }

    public void setProjection(JAXBElement<? extends Projection_VersionStructure> value) {
        this.projection = value;
    }

}
