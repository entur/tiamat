

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


public class ValidityConditions_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Object> validityConditionRefOrValidBetweenOrValidityCondition_;

    public List<Object> getValidityConditionRefOrValidBetweenOrValidityCondition_() {
        if (validityConditionRefOrValidBetweenOrValidityCondition_ == null) {
            validityConditionRefOrValidBetweenOrValidityCondition_ = new ArrayList<Object>();
        }
        return this.validityConditionRefOrValidBetweenOrValidityCondition_;
    }

}
