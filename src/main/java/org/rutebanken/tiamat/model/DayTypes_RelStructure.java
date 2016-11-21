package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class DayTypes_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<?>> dayTypeRefOrDayType_;

    public List<JAXBElement<?>> getDayTypeRefOrDayType_() {
        if (dayTypeRefOrDayType_ == null) {
            dayTypeRefOrDayType_ = new ArrayList<JAXBElement<?>>();
        }
        return this.dayTypeRefOrDayType_;
    }

}
