package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;

import javax.persistence.*;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "tiamatEntityCacheRegion")
public class PathLinkEnd extends IdentifiedEntity {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "ref", column = @Column(name = "place_ref")),
            @AttributeOverride(name = "version", column = @Column(name = "place_version"))
    })
    private AddressablePlaceRefStructure placeRef;

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

    public PathLinkEnd() {}

    public PathLinkEnd(AddressablePlaceRefStructure placeRef) {
        this.placeRef = placeRef;
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


    public AddressablePlaceRefStructure getPlaceRef() {
        return placeRef;
    }

    public void setPlaceRef(AddressablePlaceRefStructure placeRef) {
        this.placeRef = placeRef;
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
                .add("placeRef", placeRef)
                .add("pathJunction", pathJunction)
                .add("level", level)
                .add("entrace", entrance)
                .toString();
    }

}
