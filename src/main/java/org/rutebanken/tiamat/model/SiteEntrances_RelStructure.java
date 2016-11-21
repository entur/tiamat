package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class SiteEntrances_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<?>> entranceRefOrEntrance;

    public List<JAXBElement<?>> getEntranceRefOrEntrance() {
        if (entranceRefOrEntrance == null) {
            entranceRefOrEntrance = new ArrayList<JAXBElement<?>>();
        }
        return this.entranceRefOrEntrance;
    }

}
