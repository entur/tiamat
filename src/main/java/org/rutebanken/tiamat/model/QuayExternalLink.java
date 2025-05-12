package org.rutebanken.tiamat.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class QuayExternalLink {
    private String name;
    private String location;

    @Column(insertable=false, updatable=false)
    private long quayId;
    @Column(insertable=false, updatable=false)
    private int orderNum;

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

    public long getQuayId() {
        return quayId;
    }

    public int getOrderNum() {
        return orderNum;
    }
}
