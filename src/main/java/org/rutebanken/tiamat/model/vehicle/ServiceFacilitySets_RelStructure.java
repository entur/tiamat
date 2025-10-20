package org.rutebanken.tiamat.model.vehicle;

import jakarta.xml.bind.JAXBElement;
import org.rutebanken.tiamat.model.ContainmentAggregationStructure;

import java.util.ArrayList;
import java.util.List;

public class ServiceFacilitySets_RelStructure extends ContainmentAggregationStructure {

    private List<JAXBElement<?>> serviceFacilitySetRefOrServiceFacilitySet;

    public List<JAXBElement<?>> getServiceFacilitySetRefOrServiceFacilitySet() {
        if (this.serviceFacilitySetRefOrServiceFacilitySet == null) {
            this.serviceFacilitySetRefOrServiceFacilitySet = new ArrayList();
        }

        return this.serviceFacilitySetRefOrServiceFacilitySet;
    }
}
