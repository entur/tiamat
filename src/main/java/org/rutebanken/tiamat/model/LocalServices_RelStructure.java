

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


public class LocalServices_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<JAXBElement<?>> localServiceRefOrLocalService;

    public List<JAXBElement<?>> getLocalServiceRefOrLocalService() {
        if (localServiceRefOrLocalService == null) {
            localServiceRefOrLocalService = new ArrayList<JAXBElement<?>>();
        }
        return this.localServiceRefOrLocalService;
    }

}
