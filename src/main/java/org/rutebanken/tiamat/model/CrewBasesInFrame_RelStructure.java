

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class CrewBasesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<CrewBase> crewBase;

    public List<CrewBase> getCrewBase() {
        if (crewBase == null) {
            crewBase = new ArrayList<CrewBase>();
        }
        return this.crewBase;
    }

}
