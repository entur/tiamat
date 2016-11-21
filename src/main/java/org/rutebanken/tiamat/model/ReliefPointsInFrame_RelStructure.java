package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class ReliefPointsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<? extends TimingPoint_VersionStructure>> reliefPoint_;

    public List<JAXBElement<? extends TimingPoint_VersionStructure>> getReliefPoint_() {
        if (reliefPoint_ == null) {
            reliefPoint_ = new ArrayList<JAXBElement<? extends TimingPoint_VersionStructure>>();
        }
        return this.reliefPoint_;
    }

}
