package org.rutebanken.tiamat.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class QuayExternalLink {
    private String name;
    private String location;

    public QuayExternalLink() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
