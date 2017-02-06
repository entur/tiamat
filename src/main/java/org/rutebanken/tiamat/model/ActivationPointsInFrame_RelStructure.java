package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class ActivationPointsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<? extends Point>> activationPoint_;

    public List<JAXBElement<? extends Point>> getActivationPoint_() {
        if (activationPoint_ == null) {
            activationPoint_ = new ArrayList<JAXBElement<? extends Point>>();
        }
        return this.activationPoint_;
    }

}
