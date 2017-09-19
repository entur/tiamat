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

import javax.xml.datatype.Duration;
import java.math.BigDecimal;


public class HelpPointEquipment_VersionStructure
        extends PassengerEquipment_VersionStructure {

    protected AccessibilityAssessment accessibilityAssessment;
    protected BigDecimal heightFromGround;
    protected Boolean phone;
    protected Boolean inductionLoop;
    protected Boolean inductionLoopSign;
    protected Boolean stopRequestButton;
    protected Duration stopRequestTimeout;

    public AccessibilityAssessment getAccessibilityAssessment() {
        return accessibilityAssessment;
    }

    public void setAccessibilityAssessment(AccessibilityAssessment value) {
        this.accessibilityAssessment = value;
    }

    public BigDecimal getHeightFromGround() {
        return heightFromGround;
    }

    public void setHeightFromGround(BigDecimal value) {
        this.heightFromGround = value;
    }

    public Boolean isPhone() {
        return phone;
    }

    public void setPhone(Boolean value) {
        this.phone = value;
    }

    public Boolean isInductionLoop() {
        return inductionLoop;
    }

    public void setInductionLoop(Boolean value) {
        this.inductionLoop = value;
    }

    public Boolean isInductionLoopSign() {
        return inductionLoopSign;
    }

    public void setInductionLoopSign(Boolean value) {
        this.inductionLoopSign = value;
    }

    public Boolean isStopRequestButton() {
        return stopRequestButton;
    }

    public void setStopRequestButton(Boolean value) {
        this.stopRequestButton = value;
    }

    public Duration getStopRequestTimeout() {
        return stopRequestTimeout;
    }

    public void setStopRequestTimeout(Duration value) {
        this.stopRequestTimeout = value;
    }

}
