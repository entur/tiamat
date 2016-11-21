

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class FlexibleArea_VersionStructure
    extends FlexibleQuay_VersionStructure
{

    protected DestinationDisplayViews_RelStructure destinations;

    public DestinationDisplayViews_RelStructure getDestinations() {
        return destinations;
    }

    public void setDestinations(DestinationDisplayViews_RelStructure value) {
        this.destinations = value;
    }

}
