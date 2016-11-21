package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import java.math.BigInteger;


public class RampEquipment_VersionStructure
        extends AccessEquipment_VersionStructure {

    protected BigDecimal length;
    protected BigInteger gradient;
    protected GradientEnumeration gradientType;
    protected Boolean pedestal;
    protected BigDecimal handrailHeight;
    protected HandrailEnumeration handrailType;
    protected Boolean tactileGuidanceStrips;
    protected Boolean visualGuidanceBands;
    protected Boolean temporary;
    protected Boolean suitableForCycles;

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal value) {
        this.length = value;
    }

    public BigInteger getGradient() {
        return gradient;
    }

    public void setGradient(BigInteger value) {
        this.gradient = value;
    }

    public GradientEnumeration getGradientType() {
        return gradientType;
    }

    public void setGradientType(GradientEnumeration value) {
        this.gradientType = value;
    }

    public Boolean isPedestal() {
        return pedestal;
    }

    public void setPedestal(Boolean value) {
        this.pedestal = value;
    }

    public BigDecimal getHandrailHeight() {
        return handrailHeight;
    }

    public void setHandrailHeight(BigDecimal value) {
        this.handrailHeight = value;
    }

    public HandrailEnumeration getHandrailType() {
        return handrailType;
    }

    public void setHandrailType(HandrailEnumeration value) {
        this.handrailType = value;
    }

    public Boolean isTactileGuidanceStrips() {
        return tactileGuidanceStrips;
    }

    public void setTactileGuidanceStrips(Boolean value) {
        this.tactileGuidanceStrips = value;
    }

    public Boolean isVisualGuidanceBands() {
        return visualGuidanceBands;
    }

    public void setVisualGuidanceBands(Boolean value) {
        this.visualGuidanceBands = value;
    }

    public Boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(Boolean value) {
        this.temporary = value;
    }

    public Boolean isSuitableForCycles() {
        return suitableForCycles;
    }

    public void setSuitableForCycles(Boolean value) {
        this.suitableForCycles = value;
    }

}
