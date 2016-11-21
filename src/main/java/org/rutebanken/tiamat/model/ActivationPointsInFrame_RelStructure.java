

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class ActivationPointsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<JAXBElement<? extends Point_VersionStructure>> activationPoint_;

    public List<JAXBElement<? extends Point_VersionStructure>> getActivationPoint_() {
        if (activationPoint_ == null) {
            activationPoint_ = new ArrayList<JAXBElement<? extends Point_VersionStructure>>();
        }
        return this.activationPoint_;
    }

}
