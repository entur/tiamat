

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ClassificationDescriptors_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<ClassificationDescriptor_VersionStructure> classificationDescriptor;

    public List<ClassificationDescriptor_VersionStructure> getClassificationDescriptor() {
        if (classificationDescriptor == null) {
            classificationDescriptor = new ArrayList<ClassificationDescriptor_VersionStructure>();
        }
    }

}
