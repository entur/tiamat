

package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public abstract class StairEquipment_VersionStructure
    extends AccessEquipment_VersionStructure
{

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
