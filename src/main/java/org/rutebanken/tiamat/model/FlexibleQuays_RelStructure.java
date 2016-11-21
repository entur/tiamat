package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class FlexibleQuays_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<?>> flexibleQuayRefOrFlexibleQuay;

    public List<JAXBElement<?>> getFlexibleQuayRefOrFlexibleQuay() {
        if (flexibleQuayRefOrFlexibleQuay == null) {
            flexibleQuayRefOrFlexibleQuay = new ArrayList<JAXBElement<?>>();
        }
        return this.flexibleQuayRefOrFlexibleQuay;
    }

}
