package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.rutebanken.tiamat.model.indentification.IdentifiedEntity;

import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PathLinkEnd implements IdentifiedEntity {

    @Id
    @GeneratedValue(generator = "sequence_per_table_generator")
    private Long id;

    @ManyToOne
    private StopPlace stopPlace;

    @ManyToOne
    private Quay quay;

    @Transient
    @OneToOne
    private PointOfInterest pointOfInterest;

    @Transient
    @OneToOne
    private AccessSpace accessSpace;

    @ManyToOne
    private PathJunction pathJunction;

    @Transient
    private SiteEntrance entrance;

    @Transient
    private Level level;

    public PathLinkEnd(Quay quay) {
        this.quay = quay;
    }

    public PathLinkEnd() {
    }

    public PathLinkEnd(StopPlace stopPlace) {
        this.stopPlace = stopPlace;
    }

    public PathLinkEnd(AccessSpace accessSpace) {
        this.accessSpace = accessSpace;
    }

    public PathLinkEnd(SiteEntrance entrance) {
        this.entrance = entrance;
    }

    public PathLinkEnd(PathJunction pathJunction) {
        this.pathJunction = pathJunction;
    }

    public PathLinkEnd(Level level) {
        this.level = level;
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

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
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

    public PathJunction getPathJunction() {
        return pathJunction;
    }

    public void setPathJunction(PathJunction pathJunction) {
        this.pathJunction = pathJunction;
    }

    public void setPointOfInterest(PointOfInterest pointOfInterest) {
        this.pointOfInterest = pointOfInterest;
    }

    public void setAccessSpace(AccessSpace accessSpace) {
        this.accessSpace = accessSpace;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", id)
                .add("quay", quay)
                .add("stopPlace", stopPlace)
                .add("pathJunction", pathJunction)
                .add("level", level)
                .add("entrace", entrance)
                .toString();
    }
}
