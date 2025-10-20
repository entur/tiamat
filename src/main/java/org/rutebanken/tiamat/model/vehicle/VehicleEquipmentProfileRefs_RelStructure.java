package org.rutebanken.tiamat.model.vehicle;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.rutebanken.netex.OmitNullsToStringStyle;
import org.rutebanken.tiamat.model.OneToManyRelationshipStructure;

public class VehicleEquipmentProfileRefs_RelStructure extends OneToManyRelationshipStructure {

    private List<JAXBElement<? extends VehicleEquipmentProfileRefStructure>> vehicleEquipmentProfileRef;

    public List<JAXBElement<? extends VehicleEquipmentProfileRefStructure>> getVehicleEquipmentProfileRef() {
        if (this.vehicleEquipmentProfileRef == null) {
            this.vehicleEquipmentProfileRef = new ArrayList();
        }

        return this.vehicleEquipmentProfileRef;
    }
}