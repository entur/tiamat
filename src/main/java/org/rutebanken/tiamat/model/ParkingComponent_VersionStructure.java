package org.rutebanken.tiamat.model;

import java.math.BigDecimal;


public class ParkingComponent_VersionStructure
        extends SiteComponent_VersionStructure {

    protected String parkingPaymentCode;
    protected EmbeddableMultilingualString label;
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

    public EmbeddableMultilingualString getLabel() {
        return label;
    }

    public void setLabel(EmbeddableMultilingualString value) {
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
