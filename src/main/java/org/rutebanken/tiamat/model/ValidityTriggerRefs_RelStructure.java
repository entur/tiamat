

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ValidityTriggerRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<ValidityTriggerRefStructure> validityTriggerRef;

    public List<ValidityTriggerRefStructure> getValidityTriggerRef() {
        if (validityTriggerRef == null) {
            validityTriggerRef = new ArrayList<ValidityTriggerRefStructure>();
        }
        return this.validityTriggerRef;
    }

}
