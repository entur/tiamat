package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class TypesOfValueStructure
        extends StrictContainmentAggregationStructure {

    protected List<JAXBElement<? extends TypeOfValue_VersionStructure>> typeOfValue;

    public List<JAXBElement<? extends TypeOfValue_VersionStructure>> getTypeOfValue() {
        if (typeOfValue == null) {
            typeOfValue = new ArrayList<JAXBElement<? extends TypeOfValue_VersionStructure>>();
        }
        return this.typeOfValue;
    }

}
