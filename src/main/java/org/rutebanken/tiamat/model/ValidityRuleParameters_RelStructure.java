

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ValidityRuleParameters_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<ValidityRuleParameter_VersionStructure> validityRuleParameter;

    public List<ValidityRuleParameter_VersionStructure> getValidityRuleParameter() {
        if (validityRuleParameter == null) {
            validityRuleParameter = new ArrayList<ValidityRuleParameter_VersionStructure>();
        }
        return this.validityRuleParameter;
    }

}
