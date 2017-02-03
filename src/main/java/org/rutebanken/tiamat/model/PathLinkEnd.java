package org.rutebanken.tiamat.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity
public class PathLinkEnd {

    @Id
    private long id;

    @OneToOne
    private StopPlace stopPlace;

    @Transient
    @OneToOne
    private Quay quay;

    @Transient
    @OneToOne
    private PointOfInterest pointOfInterest;

    @Transient
    @OneToOne
    private AccessSpace accessSpace;

    @Transient
    @OneToOne
    private PathJunction pathJunction;

    @Transient
    private SiteEntrance entrance;

    @Transient
    private Level level;

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

    public StopPlace getStopPlace() {
        return stopPlace;
    }

    public void setStopPlace(StopPlace stopPlace) {
        this.stopPlace = stopPlace;
    }

    public Quay getQuay() {
        return quay;
    }

    public void setQuay(Quay quay) {
        this.quay = quay;
    }
}
