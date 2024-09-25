package org.rutebanken.tiamat.model.authorization;

import java.util.Collections;
import java.util.Set;

public class EntityPermissions {
    private final Set<String> allowedStopPlaceTypes;
    private final Set<String> bannedStopPlaceTypes;
    private final Set<String> allowedSubmodes;
    private final Set<String> bannedSubmodes;
    private boolean canEdit;
    private boolean canDelete;

    public EntityPermissions(boolean canEdit, boolean canDelete, Set<String> allowedStopPlaceTypes, Set<String> bannedStopPlaceTypes, Set<String> allowedSubmodes, Set<String> bannedSubmodes) {
        this.canEdit = canEdit;
        this.canDelete = canDelete;
        this.allowedStopPlaceTypes = allowedStopPlaceTypes == null ? Collections.emptySet() : allowedStopPlaceTypes;
        this.bannedStopPlaceTypes = bannedStopPlaceTypes == null ? Collections.emptySet() : bannedStopPlaceTypes;
        this.allowedSubmodes = allowedSubmodes == null ? Collections.emptySet() : allowedSubmodes;
        this.bannedSubmodes = bannedSubmodes == null ? Collections.emptySet() : bannedSubmodes;
    }

}
