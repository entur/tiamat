

package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class LeftLuggageService_VersionStructure
    extends CustomerService_VersionStructure
{

    protected Boolean counterService;
    protected Boolean selfServiceLockers;
    protected Boolean feePerBag;
    protected Boolean lockerFee;
    protected BigDecimal maximumBagWidth;
    protected BigDecimal maximumBagHeight;
    protected BigDecimal maximumBagDepth;

    public Boolean isCounterService() {
        return counterService;
    }

    public void setCounterService(Boolean value) {
        this.counterService = value;
    }

    public Boolean isSelfServiceLockers() {
        return selfServiceLockers;
    }

    public void setSelfServiceLockers(Boolean value) {
        this.selfServiceLockers = value;
    }

    public Boolean isFeePerBag() {
        return feePerBag;
    }

    public void setFeePerBag(Boolean value) {
        this.feePerBag = value;
    }

    public Boolean isLockerFee() {
        return lockerFee;
    }

    public void setLockerFee(Boolean value) {
        this.lockerFee = value;
    }

    public BigDecimal getMaximumBagWidth() {
        return maximumBagWidth;
    }

    public void setMaximumBagWidth(BigDecimal value) {
        this.maximumBagWidth = value;
    }

    public BigDecimal getMaximumBagHeight() {
        return maximumBagHeight;
    }

    public void setMaximumBagHeight(BigDecimal value) {
        this.maximumBagHeight = value;
    }

    public BigDecimal getMaximumBagDepth() {
        return maximumBagDepth;
    }

    public void setMaximumBagDepth(BigDecimal value) {
        this.maximumBagDepth = value;
    }

}
