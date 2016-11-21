

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class PointOfInterestClassification_VersionStructure
    extends TypeOfValue_VersionStructure
{

    protected ClassificationDescriptors_RelStructure alternativeDescriptors;

    public ClassificationDescriptors_RelStructure getAlternativeDescriptors() {
        return alternativeDescriptors;
    }

    public void setAlternativeDescriptors(ClassificationDescriptors_RelStructure value) {
        this.alternativeDescriptors = value;
    }

}
