package org.rutebanken.tiamat.model;

import org.rutebanken.netex.model.Level;

public class PathLinkEnd {

    private Place place;

    private SiteEntrance entrance;

    private Level level;

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
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
}
