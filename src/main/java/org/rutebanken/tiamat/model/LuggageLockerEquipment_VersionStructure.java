

package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "numberOfLockers",
    "lockerWidth",
    "lockerHeight",
    "lockerDepth",
public class LuggageLockerEquipment_VersionStructure
    extends SiteEquipment_VersionStructure
{

    protected BigInteger numberOfLockers;
    protected BigDecimal lockerWidth;
    protected BigDecimal lockerHeight;
    protected BigDecimal lockerDepth;
    protected LockerTypeEnumeration lockerType;

    public BigInteger getNumberOfLockers() {
        return numberOfLockers;
    }

    public void setNumberOfLockers(BigInteger value) {
        this.numberOfLockers = value;
    }

    public BigDecimal getLockerWidth() {
        return lockerWidth;
    }

    public void setLockerWidth(BigDecimal value) {
        this.lockerWidth = value;
    }

    public BigDecimal getLockerHeight() {
        return lockerHeight;
    }

    public void setLockerHeight(BigDecimal value) {
        this.lockerHeight = value;
    }

    public BigDecimal getLockerDepth() {
        return lockerDepth;
    }

    public void setLockerDepth(BigDecimal value) {
        this.lockerDepth = value;
    }

    public LockerTypeEnumeration getLockerType() {
        return lockerType;
    }

    public void setLockerType(LockerTypeEnumeration value) {
        this.lockerType = value;
    }

}
