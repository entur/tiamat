package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class LinesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<? extends DataManagedObjectStructure>> line_;

    public List<JAXBElement<? extends DataManagedObjectStructure>> getLine_() {
        if (line_ == null) {
            line_ = new ArrayList<JAXBElement<? extends DataManagedObjectStructure>>();
        }
        return this.line_;
    }

}
