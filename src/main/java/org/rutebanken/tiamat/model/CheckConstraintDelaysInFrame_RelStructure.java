

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class CheckConstraintDelaysInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<CheckConstraintDelay> checkConstraintDelay;

    public List<CheckConstraintDelay> getCheckConstraintDelay() {
        if (checkConstraintDelay == null) {
            checkConstraintDelay = new ArrayList<CheckConstraintDelay>();
        }
        return this.checkConstraintDelay;
    }

}
