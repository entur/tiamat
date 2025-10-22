package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.rutebanken.tiamat.model.CoveredEnumeration;

import java.math.BigInteger;
import java.util.List;

@MappedSuperclass
@Getter
@Setter
public abstract class DeckSpace_VersionStructure extends DeckComponent_VersionStructure {

    @Enumerated(EnumType.STRING)
    private CoveredEnumeration covered;
    private Boolean airConditioned;
    private Boolean smokingAllowed;
    private BigInteger totalCapacity;

    @OneToMany(cascade = CascadeType.ALL)
    protected List<PassengerEntrance> deckEntrances;

    // TODO - TBD
//    protected TypeOfDeckSpaceProfileRefStructure typeOfDeckSpaceRef;
//    protected DeckSpaceRefStructure parentDeckSpaceRef;
//    protected DeckEntranceCouples_RelStructure deckEntranceCouples;
//    protected DeckEntranceUsages_RelStructure deckEntranceUsages;
//    protected DeckWindows_RelStructure deckWindows;

//    protected DeckSpaceCapacities_RelStructure deckSpaceCapacities;

}
