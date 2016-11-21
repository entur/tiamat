

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class TopographicPlaceDescriptors_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<TopographicPlaceDescriptor_VersionedChildStructure> topographicPlaceDescriptor;

    public List<TopographicPlaceDescriptor_VersionedChildStructure> getTopographicPlaceDescriptor() {
        if (topographicPlaceDescriptor == null) {
            topographicPlaceDescriptor = new ArrayList<TopographicPlaceDescriptor_VersionedChildStructure>();
        }
        return this.topographicPlaceDescriptor;
    }

}
