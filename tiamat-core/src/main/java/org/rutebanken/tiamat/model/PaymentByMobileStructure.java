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
