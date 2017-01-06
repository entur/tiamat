package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class TypesOfValueInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<? extends DataManagedObjectStructure>> valueSetOrTypeOfValue;

    public List<JAXBElement<? extends DataManagedObjectStructure>> getValueSetOrTypeOfValue() {
        if (valueSetOrTypeOfValue == null) {
            valueSetOrTypeOfValue = new ArrayList<JAXBElement<? extends DataManagedObjectStructure>>();
        }
        return this.valueSetOrTypeOfValue;
    }

}
