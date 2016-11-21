package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TopographicPlaceDescriptors_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<TopographicPlaceDescriptor_VersionedChildStructure> topographicPlaceDescriptor;

    public List<TopographicPlaceDescriptor_VersionedChildStructure> getTopographicPlaceDescriptor() {
        if (topographicPlaceDescriptor == null) {
            topographicPlaceDescriptor = new ArrayList<TopographicPlaceDescriptor_VersionedChildStructure>();
        }
        return this.topographicPlaceDescriptor;
    }

}
