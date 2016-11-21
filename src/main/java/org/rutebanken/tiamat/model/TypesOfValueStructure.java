

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class TypesOfValueStructure
    extends StrictContainmentAggregationStructure
{

    protected List<JAXBElement<? extends TypeOfValue_VersionStructure>> typeOfValue;

    public List<JAXBElement<? extends TypeOfValue_VersionStructure>> getTypeOfValue() {
        if (typeOfValue == null) {
            typeOfValue = new ArrayList<JAXBElement<? extends TypeOfValue_VersionStructure>>();
        }
        return this.typeOfValue;
    }

}
