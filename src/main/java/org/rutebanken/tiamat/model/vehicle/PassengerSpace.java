package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class PassengerSpace extends DeckSpace_VersionStructure {
    @Enumerated(EnumType.STRING)
    private PassengerSpaceTypeEnumeration passengerSpaceType;
    private Boolean standingAllowed;

    @OneToMany(cascade = CascadeType.ALL)
    protected List<PassengerSpot> passengerSpots;

    //TODO - TBD
//    protected LuggageSpots_RelStructure luggageSpots;
//    protected PassengerVehicleSpots_RelStructure passengerVehicleSpots;
//    protected SpotAffinities_RelStructure spotAffinities;

}
