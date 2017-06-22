package org.rutebanken.tiamat.model.authorization;

public class AuthorizationResponse {

    private String id;

    private boolean authorized;
    private final String role;

    public AuthorizationResponse(String id, boolean authorized, String role) {
        this.id = id;
        this.authorized = authorized;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public String getRole() {
        return role;
    }

}
