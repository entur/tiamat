package org.rutebanken.tiamat.model;

import org.rutebanken.netex.model.*;
import org.rutebanken.netex.model.Level;

public class PathLinkEnd {

    private Place_VersionStructure place;

    private SiteEntrance_VersionStructure entrance;

    private Level level;

    public Place_VersionStructure getPlace() {
        return place;
    }

    public void setPlace(Place_VersionStructure place) {
        this.place = place;
    }

    public SiteEntrance_VersionStructure getEntrance() {
        return entrance;
    }

    public void setEntrance(SiteEntrance_VersionStructure entrance) {
        this.entrance = entrance;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
