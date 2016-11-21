

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class LinesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<JAXBElement<? extends DataManagedObjectStructure>> line_;

    public List<JAXBElement<? extends DataManagedObjectStructure>> getLine_() {
        if (line_ == null) {
            line_ = new ArrayList<JAXBElement<? extends DataManagedObjectStructure>>();
        }
        return this.line_;
    }

}
