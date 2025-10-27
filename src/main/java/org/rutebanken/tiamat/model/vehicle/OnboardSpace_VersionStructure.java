package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Zone_VersionStructure;

import java.math.BigDecimal;

@MappedSuperclass
@Getter
@Setter
public abstract class OnboardSpace_VersionStructure extends Zone_VersionStructure {
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "label_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "label_lang", length = 5))
    })
    @Embedded
    private EmbeddableMultilingualString label;

    @Enumerated(EnumType.STRING)
    private ComponentOrientationEnumeration orientation;
    private BigDecimal width;
    private BigDecimal length;
    private BigDecimal height;

//    private JAXBElement<? extends FacilitySetRefStructure> facilitySetRef;
//    private ActualVehicleEquipments_RelStructure actualVehicleEquipments;

}
