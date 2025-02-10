package org.rutebanken.tiamat.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class StopPlaceOrganisationRef implements Serializable {
    private String organisationRef;
    private StopPlaceOrganisationRelationshipEnumeration relationshipType;

    public StopPlaceOrganisationRef() {
    }

    public StopPlaceOrganisationRef(Organisation organisation, StopPlaceOrganisationRelationshipEnumeration relationshipType) {
        this.organisationRef = organisation.getNetexId();
        this.relationshipType = relationshipType;
    }

    public StopPlaceOrganisationRef(String organisationRef, StopPlaceOrganisationRelationshipEnumeration relationshipType) {
        this.organisationRef = organisationRef;
        this.relationshipType = relationshipType;
    }

    public StopPlaceOrganisationRelationshipEnumeration getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(StopPlaceOrganisationRelationshipEnumeration relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getOrganisationRef() {
        return organisationRef;
    }

    public void setOrganisationRef(String organisationId) {
        this.organisationRef = organisationId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StopPlaceOrganisationRef that)) {
            return false;
        }
        return Objects.equals(organisationRef, that.organisationRef) && relationshipType == that.relationshipType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(organisationRef, relationshipType);
    }
}
