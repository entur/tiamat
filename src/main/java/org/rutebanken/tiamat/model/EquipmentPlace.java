package org.rutebanken.tiamat.model;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;


@Entity
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "seq_equipment_place")
public class EquipmentPlace
        extends EquipmentPlace_VersionStructure {


}
