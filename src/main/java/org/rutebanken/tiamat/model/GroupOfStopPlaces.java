package org.rutebanken.tiamat.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class GroupOfStopPlaces extends GroupOfEntities_VersionStructure {

    private String publicCode;

    @OneToMany
    private Set<StopPlace> members = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AlternativeName> alternativeNames = new ArrayList<>();

    public GroupOfStopPlaces(EmbeddableMultilingualString embeddableMultilingualString) {
        super(embeddableMultilingualString);
    }

    public GroupOfStopPlaces() {
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public Set<StopPlace> getMembers() {
        return members;
    }

    public List<AlternativeName> getAlternativeNames() {
        return alternativeNames;
    }
}
