package org.rutebanken.tiamat.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class InfoSpotPosterRef extends VersionOfObjectRefStructure {

    public InfoSpotPosterRef() {

    }

    public InfoSpotPosterRef(InfoSpotPoster poster) {
        this.setRef(poster.getNetexId());
        this.setVersion(String.valueOf(poster.getVersion()));
    }

    public InfoSpotPosterRef(String netexId) {
        this.setRef(netexId);
    }

    public InfoSpotPosterRef(String netexId, String version) {
        this.setRef(netexId);
        this.setVersion(version);
    }
}
