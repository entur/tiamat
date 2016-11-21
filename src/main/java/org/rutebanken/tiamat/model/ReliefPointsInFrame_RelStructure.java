

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class ReliefPointsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<JAXBElement<? extends TimingPoint_VersionStructure>> reliefPoint_;

    public List<JAXBElement<? extends TimingPoint_VersionStructure>> getReliefPoint_() {
        if (reliefPoint_ == null) {
            reliefPoint_ = new ArrayList<JAXBElement<? extends TimingPoint_VersionStructure>>();
        }
        return this.reliefPoint_;
    }

}
