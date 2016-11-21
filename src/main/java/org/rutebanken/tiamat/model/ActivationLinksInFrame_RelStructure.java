

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ActivationLinksInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<ActivationLink> activationLink;

    public List<ActivationLink> getActivationLink() {
        if (activationLink == null) {
            activationLink = new ArrayList<ActivationLink>();
        }
        return this.activationLink;
    }

}
