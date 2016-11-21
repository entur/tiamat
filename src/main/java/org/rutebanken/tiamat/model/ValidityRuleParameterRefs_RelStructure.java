

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ValidityRuleParameterRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<ValidityRuleParameterRefStructure> validityRuleParameterRef;

    public List<ValidityRuleParameterRefStructure> getValidityRuleParameterRef() {
        if (validityRuleParameterRef == null) {
            validityRuleParameterRef = new ArrayList<ValidityRuleParameterRefStructure>();
        }
        return this.validityRuleParameterRef;
    }

}
