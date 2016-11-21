

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class EntitiesInVersion_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<JAXBElement<? extends EntityInVersionStructure>> entityInVersion;

    public List<JAXBElement<? extends EntityInVersionStructure>> getEntityInVersion() {
        if (entityInVersion == null) {
            entityInVersion = new ArrayList<JAXBElement<? extends EntityInVersionStructure>>();
        }
        return this.entityInVersion;
    }

}
