

package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class PassengerSafetyEquipment_VersionStructure
    extends PassengerEquipment_VersionStructure
{

    protected Boolean cctv;
    protected Boolean mobilePhoneCoverage;
    protected Boolean panicButton;
    protected Boolean sosPhones;
    protected BigDecimal heightOfSosPanel;
    protected LightingEnumeration lighting;
    protected Boolean acousticAnnouncements;

    public Boolean isCctv() {
        return cctv;
    }

    public void setCctv(Boolean value) {
        this.cctv = value;
    }

    public Boolean isMobilePhoneCoverage() {
        return mobilePhoneCoverage;
    }

    public void setMobilePhoneCoverage(Boolean value) {
        this.mobilePhoneCoverage = value;
    }

    public Boolean isPanicButton() {
        return panicButton;
    }

    public void setPanicButton(Boolean value) {
        this.panicButton = value;
    }

    public Boolean isSosPhones() {
        return sosPhones;
    }

    public void setSosPhones(Boolean value) {
        this.sosPhones = value;
    }

    public BigDecimal getHeightOfSosPanel() {
        return heightOfSosPanel;
    }

    public void setHeightOfSosPanel(BigDecimal value) {
        this.heightOfSosPanel = value;
    }

    public LightingEnumeration getLighting() {
        return lighting;
    }

    public void setLighting(LightingEnumeration value) {
        this.lighting = value;
    }

    public Boolean isAcousticAnnouncements() {
        return acousticAnnouncements;
    }

    public void setAcousticAnnouncements(Boolean value) {
        this.acousticAnnouncements = value;
    }

}
