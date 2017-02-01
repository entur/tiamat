package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class EntitiesInVersion_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<? extends EntityInVersionStructure>> entityInVersion;

    public List<JAXBElement<? extends EntityInVersionStructure>> getEntityInVersion() {
        if (entityInVersion == null) {
            entityInVersion = new ArrayList<JAXBElement<? extends EntityInVersionStructure>>();
        }
        return this.entityInVersion;
    }

}
