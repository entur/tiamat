package org.rutebanken.tiamat.model;

import javax.persistence.Embeddable;

@Embeddable
public class TopographicPlaceRefStructure
        extends PlaceRefStructure {

    public TopographicPlaceRefStructure() {
    }

    public TopographicPlaceRefStructure(String ref, String version) {
        super(ref, version);
    }

    public TopographicPlaceRefStructure(TopographicPlace topographicPlace) {
        super(topographicPlace.getNetexId(), String.valueOf(topographicPlace.getVersion()));
    }
}
