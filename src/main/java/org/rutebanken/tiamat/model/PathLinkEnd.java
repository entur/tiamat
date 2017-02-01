package org.rutebanken.tiamat.model;

import org.rutebanken.netex.model.Level;

public class PathLinkEnd {

    private Place_VersionStructure place;

    private SiteEntrance entrance;

    private Level level;

    public Place_VersionStructure getPlace() {
        return place;
    }

    public void setPlace(Place_VersionStructure place) {
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
