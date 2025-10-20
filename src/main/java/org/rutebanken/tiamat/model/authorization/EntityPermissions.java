package org.rutebanken.tiamat.model.authorization;

import org.rutebanken.tiamat.diff.generic.SubmodeEnumuration;
import org.rutebanken.tiamat.model.StopTypeEnumeration;

import java.util.Collections;
import java.util.Set;

public class EntityPermissions {
    private final Set<StopTypeEnumeration> allowedStopPlaceTypes;
    private final Set<StopTypeEnumeration> bannedStopPlaceTypes;
    private final Set<SubmodeEnumuration> allowedSubmodes;
    private final Set<SubmodeEnumuration> bannedSubmodes;
    private boolean canEdit;
    private boolean canDelete;

    private EntityPermissions(Builder builder) {
        this.canEdit = true;
        this.canDelete = true;
        this.allowedStopPlaceTypes = builder.allowedStopPlaceTypes == null ? Collections.emptySet() : builder.allowedStopPlaceTypes;
        this.bannedStopPlaceTypes = builder.bannedStopPlaceTypes == null ? Collections.emptySet() : builder.bannedStopPlaceTypes;
        this.allowedSubmodes = builder.allowedSubmodes == null ? Collections.emptySet() : builder.allowedSubmodes;
        this.bannedSubmodes = builder.bannedSubmodes == null ? Collections.emptySet() : builder.bannedSubmodes;
    }

    public static class Builder {
        private Set<StopTypeEnumeration> allowedStopPlaceTypes;
        private Set<StopTypeEnumeration> bannedStopPlaceTypes;
        private Set<SubmodeEnumuration> allowedSubmodes;
        private Set<SubmodeEnumuration> bannedSubmodes;
        private boolean canEdit;
        private boolean canDelete;

        public Builder canEdit(boolean canEdit) {
            this.canEdit = canEdit;
            return this;
        }

        public Builder canDelete(boolean canDelete) {
            this.canDelete = canDelete;
            return this;
        }

        public Builder allowedStopPlaceTypes(Set<StopTypeEnumeration> allowedStopPlaceTypes) {
            this.allowedStopPlaceTypes = allowedStopPlaceTypes;
            return this;
        }

        public Builder bannedStopPlaceTypes(Set<StopTypeEnumeration> bannedStopPlaceTypes) {
            this.bannedStopPlaceTypes = bannedStopPlaceTypes;
            return this;
        }

        public Builder allowedSubmodes(Set<SubmodeEnumuration> allowedSubmodes) {
            this.allowedSubmodes = allowedSubmodes;
            return this;
        }

        public Builder bannedSubmodes(Set<SubmodeEnumuration> bannedSubmodes) {
            this.bannedSubmodes = bannedSubmodes;
            return this;
        }

        public EntityPermissions build() {
            return new EntityPermissions(this);
        }
    }

    public Set<StopTypeEnumeration> getAllowedStopPlaceTypes() {
        return allowedStopPlaceTypes;
    }

    public Set<StopTypeEnumeration> getBannedStopPlaceTypes() {
        return bannedStopPlaceTypes;
    }

    public Set<SubmodeEnumuration> getAllowedSubmodes() {
        return allowedSubmodes;
    }

    public Set<SubmodeEnumuration> getBannedSubmodes() {
        return bannedSubmodes;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

}
