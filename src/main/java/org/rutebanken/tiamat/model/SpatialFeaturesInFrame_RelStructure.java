package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class SpatialFeaturesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<GroupOfPoints_VersionStructure> simpleFeatureOrComplexFeature;

    public List<GroupOfPoints_VersionStructure> getSimpleFeatureOrComplexFeature() {
        if (simpleFeatureOrComplexFeature == null) {
            simpleFeatureOrComplexFeature = new ArrayList<GroupOfPoints_VersionStructure>();
        }
        return this.simpleFeatureOrComplexFeature;
    }

}
