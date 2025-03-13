package org.rutebanken.tiamat.ext.fintraffic.auth;

public enum TrivorePermission {
    ADMINISTER("administer"),
    MANAGE("manage"),
    EDIT("edit"),
    VIEW("view");

    private final String value;

    TrivorePermission(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
