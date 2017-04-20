package org.rutebanken.tiamat.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public class SanitaryEquipment_VersionStructure
        extends PassengerEquipment_VersionStructure {

    protected GenderLimitationEnumeration gender;
    protected BigInteger numberOfToilets;

    @Transient
    protected AccessibilityAssessment accessibilityAssessment;
    @Transient
    protected List<SanitaryFacilityEnumeration> sanitaryFacilityList;
    @Transient
    protected Boolean freeToUse;
    @Transient
    protected BigDecimal charge;
    @Transient
    protected String currency;
    @Transient
    protected List<PaymentMethodEnumeration> paymentMethods;
    @Transient
    protected BigDecimal wheelchairTurningCircle;
    @Transient
    protected Boolean sharpsDisposal;
    @Transient
    protected StaffingEnumeration staffing;
    @Transient
    protected String keySCheme;

    public AccessibilityAssessment getAccessibilityAssessment() {
        return accessibilityAssessment;
    }

    public void setAccessibilityAssessment(AccessibilityAssessment value) {
        this.accessibilityAssessment = value;
    }

    public GenderLimitationEnumeration getGender() {
        return gender;
    }

    public void setGender(GenderLimitationEnumeration value) {
        this.gender = value;
    }

    public List<SanitaryFacilityEnumeration> getSanitaryFacilityList() {
        if (sanitaryFacilityList == null) {
            sanitaryFacilityList = new ArrayList<SanitaryFacilityEnumeration>();
        }
        return this.sanitaryFacilityList;
    }

    public BigInteger getNumberOfToilets() {
        return numberOfToilets;
    }

    public void setNumberOfToilets(BigInteger value) {
        this.numberOfToilets = value;
    }

    public Boolean isFreeToUse() {
        return freeToUse;
    }

    public void setFreeToUse(Boolean value) {
        this.freeToUse = value;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal value) {
        this.charge = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String value) {
        this.currency = value;
    }

    public List<PaymentMethodEnumeration> getPaymentMethods() {
        if (paymentMethods == null) {
            paymentMethods = new ArrayList<PaymentMethodEnumeration>();
        }
        return this.paymentMethods;
    }

    public BigDecimal getWheelchairTurningCircle() {
        return wheelchairTurningCircle;
    }

    public void setWheelchairTurningCircle(BigDecimal value) {
        this.wheelchairTurningCircle = value;
    }

    public Boolean isSharpsDisposal() {
        return sharpsDisposal;
    }

    public void setSharpsDisposal(Boolean value) {
        this.sharpsDisposal = value;
    }

    public StaffingEnumeration getStaffing() {
        return staffing;
    }

    public void setStaffing(StaffingEnumeration value) {
        this.staffing = value;
    }

    public String getKeySCheme() {
        return keySCheme;
    }

    public void setKeySCheme(String value) {
        this.keySCheme = value;
    }

}
