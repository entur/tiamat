

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ComplexFeatureRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected ComplexFeatureRefStructure complexFeatureRef;

    public ComplexFeatureRefStructure getComplexFeatureRef() {
        return complexFeatureRef;
    }

    public void setComplexFeatureRef(ComplexFeatureRefStructure value) {
        this.complexFeatureRef = value;
    }

}
