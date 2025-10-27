package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class LocatableSpot_VersionStructure extends OnboardSpace_VersionStructure {
    @Enumerated(EnumType.STRING)
    private TypeOfLocatableSpotEnumeration locatableSpotType;
    private String spotRowRef;

//    private TypeOfLocatableSpotRefStructure typeOfLocatableSpotRef;
//    private SpotColumnRefStructure spotColumnRef;
//    private SensorsInSpot_RelStructure sensorsInSpot;

}
