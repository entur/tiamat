package org.rutebanken.tiamat.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GroupOfStopPlaces extends GroupOfEntities_VersionStructure {

    private String publicCode;

    private StopPlaceRefs_RelStructure members;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AlternativeName> alternativeNames = new ArrayList<>();


    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public StopPlaceRefs_RelStructure getMembers() {
        return members;
    }

    public void setMembers(StopPlaceRefs_RelStructure value) {
        this.members = value;
    }


    public List<AlternativeName> getAlternativeNames() {
        return alternativeNames;
    }
}
