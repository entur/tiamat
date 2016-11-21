

package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


    "accessibilityAssessment",
    "gender",
    "sanitaryFacilityList",
    "numberOfToilets",
    "freeToUse",
    "charge",
    "currency",
    "paymentMethods",
    "wheelchairTurningCircle",
    "sharpsDisposal",
    "staffing",
public class SanitaryEquipment_VersionStructure
    extends PassengerEquipment_VersionStructure
{

    protected AccessibilityAssessment accessibilityAssessment;
    protected GenderLimitationEnumeration gender;
    protected List<SanitaryFacilityEnumeration> sanitaryFacilityList;
    protected BigInteger numberOfToilets;
    protected Boolean freeToUse;
    protected BigDecimal charge;
    protected String currency;
    protected List<PaymentMethodEnumeration> paymentMethods;
    protected BigDecimal wheelchairTurningCircle;
    protected Boolean sharpsDisposal;
    protected StaffingEnumeration staffing;
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
