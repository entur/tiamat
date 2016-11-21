package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class ExplicitLocalServices_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<?>> localServiceRefOrLocalService;

    public List<JAXBElement<?>> getLocalServiceRefOrLocalService() {
        if (localServiceRefOrLocalService == null) {
            localServiceRefOrLocalService = new ArrayList<JAXBElement<?>>();
        }
        return this.localServiceRefOrLocalService;
    }

}
