

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


public class DayTypes_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<JAXBElement<?>> dayTypeRefOrDayType_;

    public List<JAXBElement<?>> getDayTypeRefOrDayType_() {
        if (dayTypeRefOrDayType_ == null) {
            dayTypeRefOrDayType_ = new ArrayList<JAXBElement<?>>();
        }
        return this.dayTypeRefOrDayType_;
    }

}
