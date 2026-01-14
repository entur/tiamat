/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public class SanitaryEquipment_VersionStructure
        extends PassengerEquipment_VersionStructure {

    protected GenderLimitationEnumeration gender;
    protected BigInteger numberOfToilets;

    @ElementCollection(targetClass = SanitaryFacilityEnumeration.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    @Enumerated(EnumType.STRING)
    protected List<SanitaryFacilityEnumeration> sanitaryFacilityList;

    @Transient
    protected AccessibilityAssessment accessibilityAssessment;
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
            sanitaryFacilityList = new ArrayList<>();
        }
        return this.sanitaryFacilityList;
    }

    public void setSanitaryFacilityList(List<SanitaryFacilityEnumeration> sanitaryFacilityList) {
        this.sanitaryFacilityList = sanitaryFacilityList;
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
            paymentMethods = new ArrayList<>();
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
