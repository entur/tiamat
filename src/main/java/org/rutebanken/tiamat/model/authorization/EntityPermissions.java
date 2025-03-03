package org.rutebanken.tiamat.model.authorization;

import org.rutebanken.tiamat.diff.generic.StopPlaceTypeSubmodeEnumuration;

import java.util.Collections;
import java.util.Set;

public class EntityPermissions {
    private final Set<StopPlaceTypeSubmodeEnumuration> allowedStopPlaceTypes;
    private final Set<StopPlaceTypeSubmodeEnumuration> bannedStopPlaceTypes;
    private final Set<StopPlaceTypeSubmodeEnumuration> allowedSubmodes;
    private final Set<StopPlaceTypeSubmodeEnumuration> bannedSubmodes;
    private boolean canEdit;
    private boolean canDelete;

    public EntityPermissions(boolean canEdit, boolean canDelete, Set<StopPlaceTypeSubmodeEnumuration> allowedStopPlaceTypes, Set<StopPlaceTypeSubmodeEnumuration> bannedStopPlaceTypes, Set<StopPlaceTypeSubmodeEnumuration> allowedSubmodes, Set<StopPlaceTypeSubmodeEnumuration> bannedSubmodes) {
        this.canEdit = canEdit;
        this.canDelete = canDelete;
        this.allowedStopPlaceTypes = allowedStopPlaceTypes == null ? Collections.emptySet() : allowedStopPlaceTypes;
        this.bannedStopPlaceTypes = bannedStopPlaceTypes == null ? Collections.emptySet() : bannedStopPlaceTypes;
        this.allowedSubmodes = allowedSubmodes == null ? Collections.emptySet() : allowedSubmodes;
        this.bannedSubmodes = bannedSubmodes == null ? Collections.emptySet() : bannedSubmodes;
    }

}
