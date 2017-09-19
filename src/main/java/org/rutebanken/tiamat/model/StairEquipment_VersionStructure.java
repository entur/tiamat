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
import java.math.BigInteger;


public abstract class StairEquipment_VersionStructure
        extends AccessEquipment_VersionStructure {

    protected BigDecimal depth;
    protected BigInteger numberOfSteps;
    protected BigDecimal stepHeight;
    protected Boolean stepColourContrast;
    protected HandrailEnumeration handrailType;
    protected BigDecimal handrailHeight;
    protected BigDecimal lowerHandrailHeight;
    protected StairEndStructure topEnd;
    protected StairEndStructure bottomEnd;

    public BigDecimal getDepth() {
        return depth;
    }

    public void setDepth(BigDecimal value) {
        this.depth = value;
    }

    public BigInteger getNumberOfSteps() {
        return numberOfSteps;
    }

    public void setNumberOfSteps(BigInteger value) {
        this.numberOfSteps = value;
    }

    public BigDecimal getStepHeight() {
        return stepHeight;
    }

    public void setStepHeight(BigDecimal value) {
        this.stepHeight = value;
    }

    public Boolean isStepColourContrast() {
        return stepColourContrast;
    }

    public void setStepColourContrast(Boolean value) {
        this.stepColourContrast = value;
    }

    public HandrailEnumeration getHandrailType() {
        return handrailType;
    }

    public void setHandrailType(HandrailEnumeration value) {
        this.handrailType = value;
    }

    public BigDecimal getHandrailHeight() {
        return handrailHeight;
    }

    public void setHandrailHeight(BigDecimal value) {
        this.handrailHeight = value;
    }

    public BigDecimal getLowerHandrailHeight() {
        return lowerHandrailHeight;
    }

    public void setLowerHandrailHeight(BigDecimal value) {
        this.lowerHandrailHeight = value;
    }

    public StairEndStructure getTopEnd() {
        return topEnd;
    }

    public void setTopEnd(StairEndStructure value) {
        this.topEnd = value;
    }

    public StairEndStructure getBottomEnd() {
        return bottomEnd;
    }

    public void setBottomEnd(StairEndStructure value) {
        this.bottomEnd = value;
    }

}
