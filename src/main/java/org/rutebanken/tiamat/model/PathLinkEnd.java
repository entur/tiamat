/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;

import javax.persistence.*;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

    public PathLinkEnd() {
    }

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
