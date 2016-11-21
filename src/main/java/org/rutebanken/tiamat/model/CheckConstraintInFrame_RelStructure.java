

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class CheckConstraintInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<CheckConstraint> checkConstraint;

    public List<CheckConstraint> getCheckConstraint() {
        if (checkConstraint == null) {
            checkConstraint = new ArrayList<CheckConstraint>();
        }
        return this.checkConstraint;
    }

}
