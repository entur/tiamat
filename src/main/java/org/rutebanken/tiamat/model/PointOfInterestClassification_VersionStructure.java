package org.rutebanken.tiamat.model;

public class PointOfInterestClassification_VersionStructure
        extends TypeOfValue_VersionStructure {

    protected ClassificationDescriptors_RelStructure alternativeDescriptors;

    public ClassificationDescriptors_RelStructure getAlternativeDescriptors() {
        return alternativeDescriptors;
    }

    public void setAlternativeDescriptors(ClassificationDescriptors_RelStructure value) {
        this.alternativeDescriptors = value;
    }

}
