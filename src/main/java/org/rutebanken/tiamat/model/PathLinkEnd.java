package org.rutebanken.tiamat.model;

import org.rutebanken.netex.model.*;
import org.rutebanken.netex.model.Level;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class PathLinkEnd {

    @Id
    private long id;

    private AddressablePlace place;

    private SiteEntrance entrance;

    @Transient
    private Level level;

    public AddressablePlace getPlace() {
        return place;
    }

    public void setPlace(AddressablePlace place) {
        this.place = place;
    }

    public SiteEntrance getEntrance() {
        return entrance;
    }

    public void setEntrance(SiteEntrance entrance) {
        this.entrance = entrance;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
