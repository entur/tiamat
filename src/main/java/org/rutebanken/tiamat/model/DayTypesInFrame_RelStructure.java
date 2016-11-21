

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class DayTypesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<JAXBElement<? extends DataManagedObjectStructure>> dayType_;

    public List<JAXBElement<? extends DataManagedObjectStructure>> getDayType_() {
        if (dayType_ == null) {
            dayType_ = new ArrayList<JAXBElement<? extends DataManagedObjectStructure>>();
        }
        return this.dayType_;
    }

}
