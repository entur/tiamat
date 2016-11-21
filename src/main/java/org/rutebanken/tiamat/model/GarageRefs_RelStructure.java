

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class GarageRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<GarageRefStructure> garageRef;

    public List<GarageRefStructure> getGarageRef() {
        if (garageRef == null) {
            garageRef = new ArrayList<GarageRefStructure>();
        }
        return this.garageRef;
    }

}
