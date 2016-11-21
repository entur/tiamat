

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ValidityTriggers_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<ValidityTrigger_VersionStructure> validityTrigger;

    public List<ValidityTrigger_VersionStructure> getValidityTrigger() {
        if (validityTrigger == null) {
            validityTrigger = new ArrayList<ValidityTrigger_VersionStructure>();
        }
        return this.validityTrigger;
    }

}
