

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class AlternativeNames_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<AlternativeName> alternativeName;

    public List<AlternativeName> getAlternativeName() {
        if (alternativeName == null) {
            alternativeName = new ArrayList<AlternativeName>();
        }
        return this.alternativeName;
    }

}
