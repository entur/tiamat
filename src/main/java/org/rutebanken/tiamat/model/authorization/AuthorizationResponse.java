package org.rutebanken.tiamat.model.authorization;

import java.util.Set;

public class AuthorizationResponse {

    private String id;

    private final Set<String> roles;

    public AuthorizationResponse(String id, Set<String> roles) {
        this.id = id;
        this.roles = roles;
    }

    public String getId() {
        return id;
    }


    public Set<String> getRoles() {
        return roles;
    }
}
