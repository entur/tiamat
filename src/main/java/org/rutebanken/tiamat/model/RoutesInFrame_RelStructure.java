

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class RoutesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<JAXBElement<? extends LinkSequence_VersionStructure>> route_;

    public List<JAXBElement<? extends LinkSequence_VersionStructure>> getRoute_() {
        if (route_ == null) {
            route_ = new ArrayList<JAXBElement<? extends LinkSequence_VersionStructure>>();
        }
        return this.route_;
    }

}
