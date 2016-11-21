package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class NetworkRestrictionsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<? extends NetworkRestriction_VersionStructure>> networkRestriction;

    public List<JAXBElement<? extends NetworkRestriction_VersionStructure>> getNetworkRestriction() {
        if (networkRestriction == null) {
            networkRestriction = new ArrayList<JAXBElement<? extends NetworkRestriction_VersionStructure>>();
        }
        return this.networkRestriction;
    }

}
