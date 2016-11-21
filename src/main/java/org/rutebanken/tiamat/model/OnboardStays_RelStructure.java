package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class OnboardStays_RelStructure
        extends ContainmentAggregationStructure {

    protected List<OnboardStay> onboardStay;

    public List<OnboardStay> getOnboardStay() {
        if (onboardStay == null) {
            onboardStay = new ArrayList<OnboardStay>();
        }
        return this.onboardStay;
    }

}
