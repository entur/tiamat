

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class OnboardStays_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<OnboardStay> onboardStay;

    public List<OnboardStay> getOnboardStay() {
        if (onboardStay == null) {
            onboardStay = new ArrayList<OnboardStay>();
        }
        return this.onboardStay;
    }

}
