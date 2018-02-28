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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class TrolleyStandEquipment_VersionStructure
        extends SiteEquipment_VersionStructure {

    protected Boolean freeToUse;
    protected BigDecimal charge;
    protected String currency;
    protected List<PaymentMethodEnumeration> paymentMethods;

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

}
