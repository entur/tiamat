package org.rutebanken.tiamat.model;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;


@Embeddable
public class AddressablePlaceRefStructure extends PlaceRefStructure {

    public AddressablePlaceRefStructure() {}

    public AddressablePlaceRefStructure(AddressablePlace addressablePlace) {
        super(addressablePlace.getNetexId(), String.valueOf(addressablePlace.getVersion()));
    }

    public AddressablePlaceRefStructure(String ref, String version) {
        super(ref, version);
    }
}
