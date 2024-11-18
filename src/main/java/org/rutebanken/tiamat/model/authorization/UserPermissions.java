package org.rutebanken.tiamat.model.authorization;

public record UserPermissions(boolean isGuest, boolean allowNewStopEverywhere) {
}
