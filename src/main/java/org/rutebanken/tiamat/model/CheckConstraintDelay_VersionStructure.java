package org.rutebanken.tiamat.model;

import javax.xml.datatype.Duration;


public class CheckConstraintDelay_VersionStructure
        extends Assignment_VersionStructure {

    protected CheckConstraintRefStructure checkConstraintRef;
    protected ClassOfUseRef classOfUseRef;
    protected Duration minimumLikelyDelay;
    protected Duration averageDelay;
    protected Duration maximumLikelyDelay;

    public CheckConstraintRefStructure getCheckConstraintRef() {
        return checkConstraintRef;
    }

    public void setCheckConstraintRef(CheckConstraintRefStructure value) {
        this.checkConstraintRef = value;
    }

    public ClassOfUseRef getClassOfUseRef() {
        return classOfUseRef;
    }

    public void setClassOfUseRef(ClassOfUseRef value) {
    }

    public Duration getMinimumLikelyDelay() {
        return minimumLikelyDelay;
    }

    public void setMinimumLikelyDelay(Duration value) {
        this.minimumLikelyDelay = value;
    }

    public Duration getAverageDelay() {
        return averageDelay;
    }

    public void setAverageDelay(Duration value) {
        this.averageDelay = value;
    }

    public Duration getMaximumLikelyDelay() {
        return maximumLikelyDelay;
    }

    public void setMaximumLikelyDelay(Duration value) {
        this.maximumLikelyDelay = value;
    }

}
