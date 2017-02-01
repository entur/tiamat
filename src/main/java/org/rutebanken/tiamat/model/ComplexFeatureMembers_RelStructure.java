package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ComplexFeatureMembers_RelStructure
        extends ContainmentAggregationStructure {

    protected List<ComplexFeatureMember_VersionedChildStructure> complexFeatureMember;

    public List<ComplexFeatureMember_VersionedChildStructure> getComplexFeatureMember() {
        if (complexFeatureMember == null) {
            complexFeatureMember = new ArrayList<ComplexFeatureMember_VersionedChildStructure>();
        }
        return this.complexFeatureMember;
    }

}
