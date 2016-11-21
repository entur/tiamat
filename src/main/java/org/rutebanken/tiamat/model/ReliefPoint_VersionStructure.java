

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class ReliefPoint_VersionStructure
    extends TimingPoint_VersionStructure
{

    protected CrewBaseRefStructure crewBaseRef;

    public CrewBaseRefStructure getCrewBaseRef() {
        return crewBaseRef;
    }

    public void setCrewBaseRef(CrewBaseRefStructure value) {
        this.crewBaseRef = value;
    }

}
