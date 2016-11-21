package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ClassificationDescriptors_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<ClassificationDescriptor_VersionStructure> classificationDescriptor;

    public List<ClassificationDescriptor_VersionStructure> getClassificationDescriptor() {
        if (classificationDescriptor == null) {
            classificationDescriptor = new ArrayList<ClassificationDescriptor_VersionStructure>();
        }
        return classificationDescriptor;
    }

}
