

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class FeatureTypeRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<TypeOfFeatureRefStructure> typeOfFeatureRef;

    public List<TypeOfFeatureRefStructure> getTypeOfFeatureRef() {
        if (typeOfFeatureRef == null) {
            typeOfFeatureRef = new ArrayList<TypeOfFeatureRefStructure>();
        }
        return this.typeOfFeatureRef;
    }

}
