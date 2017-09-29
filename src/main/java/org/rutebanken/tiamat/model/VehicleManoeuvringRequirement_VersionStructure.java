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


public class VehicleManoeuvringRequirement_VersionStructure
        extends VehicleRequirement_VersionStructure {

    protected Boolean reversible;
    protected BigDecimal minimumTurningCircle;
    protected BigDecimal minimumOvertakingWidth;
    protected BigDecimal minimumLength;

    public Boolean isReversible() {
        return reversible;
    }

    public void setReversible(Boolean value) {
        this.reversible = value;
    }

    public BigDecimal getMinimumTurningCircle() {
        return minimumTurningCircle;
    }

    public void setMinimumTurningCircle(BigDecimal value) {
        this.minimumTurningCircle = value;
    }

    public BigDecimal getMinimumOvertakingWidth() {
        return minimumOvertakingWidth;
    }

    public void setMinimumOvertakingWidth(BigDecimal value) {
        this.minimumOvertakingWidth = value;
    }

    public BigDecimal getMinimumLength() {
        return minimumLength;
    }

    public void setMinimumLength(BigDecimal value) {
        this.minimumLength = value;
    }

}
