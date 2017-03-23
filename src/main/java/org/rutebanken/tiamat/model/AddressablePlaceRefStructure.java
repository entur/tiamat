package org.rutebanken.tiamat.model;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;


@Embeddable
public class AddressablePlaceRefStructure extends PlaceRefStructure {

    public AddressablePlaceRefStructure() {}

    public AddressablePlaceRefStructure(AddressablePlace addressablePlace) {
        super(addressablePlace.getNetexId(), String.valueOf(addressablePlace.getVersion()), addressablePlace.getClass().getSimpleName());
    }

    public AddressablePlaceRefStructure(String ref, String version, String nameOfRefClass) {
        super(ref, version, nameOfRefClass);
    }
}
