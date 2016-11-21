

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


    "pointOfInterestClassificationRef",
public class PointOfInterestClassification_DerivedViewStructure
    extends DerivedViewStructure
{

    protected PointOfInterestClassificationRefStructure pointOfInterestClassificationRef;
    protected MultilingualStringEntity name;

    public PointOfInterestClassificationRefStructure getPointOfInterestClassificationRef() {
        return pointOfInterestClassificationRef;
    }

    public void setPointOfInterestClassificationRef(PointOfInterestClassificationRefStructure value) {
        this.pointOfInterestClassificationRef = value;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

}
