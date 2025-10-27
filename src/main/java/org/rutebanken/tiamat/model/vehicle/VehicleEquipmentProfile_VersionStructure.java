package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;

import java.math.BigInteger;
import java.util.List;

@MappedSuperclass
@Getter
@Setter
public class VehicleEquipmentProfile_VersionStructure extends DataManagedObjectStructure {
    @Transient
    private EmbeddableMultilingualString name;
    @Transient
    private EmbeddableMultilingualString description;

//    private JAXBElement<? extends EquipmentRefStructure> equipmentRef;

    @Transient
    private BigInteger units;
    @Transient
    private EmbeddableMultilingualString manufacturer;
//    private TypeOfEquipmentRefStructure typeOfEquipmentRef;
//    private PurposeOfEquipmentProfileRefStructure purposeOfEquipmentProfileRef;

    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL)
    private List<VehicleEquipmentProfileMember> vehicleEquipmentProfileMembers;

}
