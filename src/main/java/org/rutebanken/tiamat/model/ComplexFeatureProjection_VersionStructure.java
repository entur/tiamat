

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class ComplexFeatureProjection_VersionStructure
    extends Projection_VersionStructure
{

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
