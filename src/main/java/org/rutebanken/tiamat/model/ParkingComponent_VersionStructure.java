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
