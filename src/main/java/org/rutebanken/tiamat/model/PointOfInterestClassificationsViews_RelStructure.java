

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class PointOfInterestClassificationsViews_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<Object> pointOfInterestClassificationRefOrPointOfInterestClassificationView;

    public List<Object> getPointOfInterestClassificationRefOrPointOfInterestClassificationView() {
        if (pointOfInterestClassificationRefOrPointOfInterestClassificationView == null) {
            pointOfInterestClassificationRefOrPointOfInterestClassificationView = new ArrayList<Object>();
        }
        return this.pointOfInterestClassificationRefOrPointOfInterestClassificationView;
    }

}
