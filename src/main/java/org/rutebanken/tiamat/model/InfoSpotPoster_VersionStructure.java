package org.rutebanken.tiamat.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class InfoSpotPoster_VersionStructure extends DataManagedObjectStructure {
    private String label;
    private String lines;

    @Enumerated(EnumType.STRING)
    private PosterSizeEnumeration posterSize;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLines() {
        return lines;
    }

    public void setLines(String lines) {
        this.lines = lines;
    }

    public PosterSizeEnumeration getPosterSize() {
        return posterSize;
    }

    public void setPosterSize(PosterSizeEnumeration posterSize) {
        this.posterSize = posterSize;
    }
}
