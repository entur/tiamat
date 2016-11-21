

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TypeOfServiceFeatureRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<TypeOfServiceFeatureRefStructure> typeOfServiceFeatureRef;

    public List<TypeOfServiceFeatureRefStructure> getTypeOfServiceFeatureRef() {
        if (typeOfServiceFeatureRef == null) {
            typeOfServiceFeatureRef = new ArrayList<TypeOfServiceFeatureRefStructure>();
        }
        return this.typeOfServiceFeatureRef;
    }

}
