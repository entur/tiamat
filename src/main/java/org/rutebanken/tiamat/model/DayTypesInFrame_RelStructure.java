package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class DayTypesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<? extends DataManagedObjectStructure>> dayType_;

    public List<JAXBElement<? extends DataManagedObjectStructure>> getDayType_() {
        if (dayType_ == null) {
            dayType_ = new ArrayList<JAXBElement<? extends DataManagedObjectStructure>>();
        }
        return this.dayType_;
    }

}
