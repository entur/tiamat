

package org.rutebanken.tiamat.model;

import javax.persistence.CascadeType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.util.List;


@MappedSuperclass
public class EquipmentPlace_VersionStructure extends Place_VersionStructure
{
    @OneToMany(cascade = CascadeType.ALL)
    protected List<EquipmentPosition> equipmentPositions;

    @Transient
    protected Equipments_RelStructure placeEquipments;

    public List<EquipmentPosition> getEquipmentPositions() {
        return equipmentPositions;
    }

    public void setEquipmentPositions(List<EquipmentPosition> equipmentPositions) {
        this.equipmentPositions = equipmentPositions;
    }

    public Equipments_RelStructure getPlaceEquipments() {
        return placeEquipments;
    }

    public void setPlaceEquipments(Equipments_RelStructure value) {
        this.placeEquipments = value;
    }

}
