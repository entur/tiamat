package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class ComplexFeatureProjection_VersionStructure
        extends Projection_VersionStructure {

    protected ComplexFeatureRefStructure projectedFeartureRef;
    protected ComplexFeatureRefStructure ontoFeatureRef;
    protected List<JAXBElement<? extends PointRefStructure>> pointRef;
    protected ComplexFeatureRefs_RelStructure features;

    public ComplexFeatureRefStructure getProjectedFeartureRef() {
        return projectedFeartureRef;
    }

    public void setProjectedFeartureRef(ComplexFeatureRefStructure value) {
        this.projectedFeartureRef = value;
    }

    public ComplexFeatureRefStructure getOntoFeatureRef() {
        return ontoFeatureRef;
    }

    public void setOntoFeatureRef(ComplexFeatureRefStructure value) {
        this.ontoFeatureRef = value;
    }

    public List<JAXBElement<? extends PointRefStructure>> getPointRef() {
        if (pointRef == null) {
            pointRef = new ArrayList<JAXBElement<? extends PointRefStructure>>();
        }
        return this.pointRef;
    }

    public ComplexFeatureRefs_RelStructure getFeatures() {
        return features;
    }

    public void setFeatures(ComplexFeatureRefs_RelStructure value) {
        this.features = value;
    }

}
