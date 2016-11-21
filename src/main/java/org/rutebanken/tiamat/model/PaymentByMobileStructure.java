

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class PaymentByMobileStructure {

    protected String phoneNumberToPay;
    protected String supportPhoneNumber;
    protected String paymentUrl;
    protected String paymentAanyURLppDOownloadUrl;

    public String getPhoneNumberToPay() {
        return phoneNumberToPay;
    }

    public void setPhoneNumberToPay(String value) {
        this.phoneNumberToPay = value;
    }

    public String getSupportPhoneNumber() {
        return supportPhoneNumber;
    }

    public void setSupportPhoneNumber(String value) {
        this.supportPhoneNumber = value;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String value) {
        this.paymentUrl = value;
    }

    public String getPaymentAanyURLppDOownloadUrl() {
        return paymentAanyURLppDOownloadUrl;
    }

    public void setPaymentAanyURLppDOownloadUrl(String value) {
        this.paymentAanyURLppDOownloadUrl = value;
    }

}
