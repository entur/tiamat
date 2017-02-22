package org.rutebanken.tiamat.model;

import javax.persistence.Transient;
import java.time.Duration;


public class TransferDuration {

    protected Duration defaultDuration;

    @Transient
    protected Duration frequentTravellerDuration;

    @Transient
    protected Duration occasionalTravellerDuration;

    @Transient
    protected Duration mobilityRestrictedTravellerDuration;

    public Duration getDefaultDuration() {
        return defaultDuration;
    }

    public void setDefaultDuration(Duration value) {
        this.defaultDuration = value;
    }

    public Duration getFrequentTravellerDuration() {
        return frequentTravellerDuration;
    }

    public void setFrequentTravellerDuration(Duration value) {
        this.frequentTravellerDuration = value;
    }

    public Duration getOccasionalTravellerDuration() {
        return occasionalTravellerDuration;
    }

    public void setOccasionalTravellerDuration(Duration value) {
        this.occasionalTravellerDuration = value;
    }

    public Duration getMobilityRestrictedTravellerDuration() {
        return mobilityRestrictedTravellerDuration;
    }

    public void setMobilityRestrictedTravellerDuration(Duration value) {
        this.mobilityRestrictedTravellerDuration = value;
    }

}
