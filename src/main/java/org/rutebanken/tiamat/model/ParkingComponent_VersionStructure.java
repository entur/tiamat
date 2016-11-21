

package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


    "parkingPaymentCode",
    "label",
    "maximumLength",
    "maximumWidth",
    "maximumHeight",
public class ParkingComponent_VersionStructure
    extends SiteComponent_VersionStructure
{

    protected String parkingPaymentCode;
    protected MultilingualStringEntity label;
    protected BigDecimal maximumLength;
    protected BigDecimal maximumWidth;
    protected BigDecimal maximumHeight;
    protected BigDecimal maximumWeight;

    public String getParkingPaymentCode() {
        return parkingPaymentCode;
    }

    public void setParkingPaymentCode(String value) {
        this.parkingPaymentCode = value;
    }

    public MultilingualStringEntity getLabel() {
        return label;
    }

    public void setLabel(MultilingualStringEntity value) {
        this.label = value;
    }

    public BigDecimal getMaximumLength() {
        return maximumLength;
    }

    public void setMaximumLength(BigDecimal value) {
        this.maximumLength = value;
    }

    public BigDecimal getMaximumWidth() {
        return maximumWidth;
    }

    public void setMaximumWidth(BigDecimal value) {
        this.maximumWidth = value;
    }

    public BigDecimal getMaximumHeight() {
        return maximumHeight;
    }

    public void setMaximumHeight(BigDecimal value) {
        this.maximumHeight = value;
    }

    public BigDecimal getMaximumWeight() {
        return maximumWeight;
    }

    public void setMaximumWeight(BigDecimal value) {
        this.maximumWeight = value;
    }

}
