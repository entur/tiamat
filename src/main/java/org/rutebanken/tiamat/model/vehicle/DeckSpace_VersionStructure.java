package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.MappedSuperclass;
import org.rutebanken.tiamat.model.CoveredEnumeration;

import java.math.BigInteger;

@MappedSuperclass
public abstract class DeckSpace_VersionStructure extends DeckComponent_VersionStructure {
    private CoveredEnumeration covered;
    private Boolean airConditioned;
    private Boolean smokingAllowed;

    // TODO - TBD
//    protected TypeOfDeckSpaceProfileRefStructure typeOfDeckSpaceRef;
//    protected DeckSpaceRefStructure parentDeckSpaceRef;
//    protected DeckEntrances_RelStructure deckEntrances;
//    protected DeckEntranceCouples_RelStructure deckEntranceCouples;
//    protected DeckEntranceUsages_RelStructure deckEntranceUsages;
//    protected DeckWindows_RelStructure deckWindows;
    private BigInteger totalCapacity;
//    protected DeckSpaceCapacities_RelStructure deckSpaceCapacities;

}
