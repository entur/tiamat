

package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "height",
    "width",
    "heightFromFloor",
    "placement",
    "brandGraphic",
    "signGraphic",
public abstract class SignEquipment_VersionStructure
    extends InstalledEquipment_VersionStructure
{

    protected BigDecimal height;
    protected BigDecimal width;
    protected BigDecimal heightFromFloor;
    protected MultilingualStringEntity placement;
    protected String brandGraphic;
    protected String signGraphic;
    protected Boolean machineReadable;

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal value) {
        this.height = value;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal value) {
        this.width = value;
    }

    public BigDecimal getHeightFromFloor() {
        return heightFromFloor;
    }

    public void setHeightFromFloor(BigDecimal value) {
        this.heightFromFloor = value;
    }

    public MultilingualStringEntity getPlacement() {
        return placement;
    }

    public void setPlacement(MultilingualStringEntity value) {
        this.placement = value;
    }

    public String getBrandGraphic() {
        return brandGraphic;
    }

    public void setBrandGraphic(String value) {
        this.brandGraphic = value;
    }

    public String getSignGraphic() {
        return signGraphic;
    }

    public void setSignGraphic(String value) {
        this.signGraphic = value;
    }

    public Boolean isMachineReadable() {
        return machineReadable;
    }

    public void setMachineReadable(Boolean value) {
        this.machineReadable = value;
    }

}
