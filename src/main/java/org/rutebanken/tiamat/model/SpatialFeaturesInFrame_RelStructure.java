

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class SpatialFeaturesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<GroupOfPoints_VersionStructure> simpleFeatureOrComplexFeature;

    public List<GroupOfPoints_VersionStructure> getSimpleFeatureOrComplexFeature() {
        if (simpleFeatureOrComplexFeature == null) {
            simpleFeatureOrComplexFeature = new ArrayList<GroupOfPoints_VersionStructure>();
        }
        return this.simpleFeatureOrComplexFeature;
    }

}
