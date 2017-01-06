package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ActivationLinksInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<ActivationLink> activationLink;

    public List<ActivationLink> getActivationLink() {
        if (activationLink == null) {
            activationLink = new ArrayList<ActivationLink>();
        }
        return this.activationLink;
    }

}
