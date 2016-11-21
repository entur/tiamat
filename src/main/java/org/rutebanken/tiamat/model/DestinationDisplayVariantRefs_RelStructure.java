

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class DestinationDisplayVariantRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<DestinationDisplayVariantRefStructure> destinationDisplayVariantRef;

    public List<DestinationDisplayVariantRefStructure> getDestinationDisplayVariantRef() {
        if (destinationDisplayVariantRef == null) {
            destinationDisplayVariantRef = new ArrayList<DestinationDisplayVariantRefStructure>();
        }
        return this.destinationDisplayVariantRef;
    }

}
