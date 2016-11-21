

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class DestinationDisplayVariants_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<DestinationDisplayVariant> destinationDisplayVariant;

    public List<DestinationDisplayVariant> getDestinationDisplayVariant() {
        if (destinationDisplayVariant == null) {
            destinationDisplayVariant = new ArrayList<DestinationDisplayVariant>();
        }
        return this.destinationDisplayVariant;
    }

}
