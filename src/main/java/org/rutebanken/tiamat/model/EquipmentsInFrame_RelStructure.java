

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class EquipmentsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<JAXBElement<? extends Equipment_VersionStructure>> equipment;

    public List<JAXBElement<? extends Equipment_VersionStructure>> getEquipment() {
        if (equipment == null) {
            equipment = new ArrayList<JAXBElement<? extends Equipment_VersionStructure>>();
        }
        return this.equipment;
    }

}
