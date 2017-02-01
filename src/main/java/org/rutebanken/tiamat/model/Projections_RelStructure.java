package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class Projections_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<?>> projectionRefOrProjection;

    public List<JAXBElement<?>> getProjectionRefOrProjection() {
        if (projectionRefOrProjection == null) {
            projectionRefOrProjection = new ArrayList<JAXBElement<?>>();
        }
        return this.projectionRefOrProjection;
    }

}
