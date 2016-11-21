

package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;


    "accessibilityAssessment",
    "heightFromGround",
    "phone",
    "inductionLoop",
    "inductionLoopSign",
    "stopRequestButton",
public class HelpPointEquipment_VersionStructure
    extends PassengerEquipment_VersionStructure
{

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
