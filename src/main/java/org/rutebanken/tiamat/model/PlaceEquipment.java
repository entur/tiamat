package org.rutebanken.tiamat.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;


@Entity
public class PlaceEquipment
        extends PlaceEquipment_VersionStructure {

    @OneToMany(cascade = CascadeType.ALL)
    protected List<InstalledEquipment_VersionStructure> installedEquipment;

    public List<InstalledEquipment_VersionStructure> getInstalledEquipment() {
        if (installedEquipment == null) {
            installedEquipment = new ArrayList<>();
        }
        return this.installedEquipment;
    }
}
