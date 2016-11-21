

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


public class TypesOfValueInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<JAXBElement<? extends DataManagedObjectStructure>> valueSetOrTypeOfValue;

    public List<JAXBElement<? extends DataManagedObjectStructure>> getValueSetOrTypeOfValue() {
        if (valueSetOrTypeOfValue == null) {
            valueSetOrTypeOfValue = new ArrayList<JAXBElement<? extends DataManagedObjectStructure>>();
        }
        return this.valueSetOrTypeOfValue;
    }

}
