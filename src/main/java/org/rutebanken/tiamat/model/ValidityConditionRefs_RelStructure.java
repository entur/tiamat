

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class ValidityConditionRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<JAXBElement<? extends ValidityConditionRefStructure>> validityConditionRef;

    public List<JAXBElement<? extends ValidityConditionRefStructure>> getValidityConditionRef() {
        if (validityConditionRef == null) {
            validityConditionRef = new ArrayList<JAXBElement<? extends ValidityConditionRefStructure>>();
        }
        return this.validityConditionRef;
    }

}
