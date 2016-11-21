

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class NetworkRestrictionsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<JAXBElement<? extends NetworkRestriction_VersionStructure>> networkRestriction;

    public List<JAXBElement<? extends NetworkRestriction_VersionStructure>> getNetworkRestriction() {
        if (networkRestriction == null) {
            networkRestriction = new ArrayList<JAXBElement<? extends NetworkRestriction_VersionStructure>>();
        }
        return this.networkRestriction;
    }

}
