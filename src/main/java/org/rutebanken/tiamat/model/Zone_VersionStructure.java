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
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import javax.persistence.*;


@MappedSuperclass
public class Zone_VersionStructure
        extends GroupOfPoints_VersionStructure {

    @Transient
    protected TypeOfZoneRefs_RelStructure types;

    protected Point centroid;

    /**
     * Polygon is wrapped in PersistablePolygon.
     * Because we want to fetch polygons lazily and using lazy property fetching with byte code enhancement breaks tests.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    protected PersistablePolygon polygon;

    @Transient
    protected Projections_RelStructure projections;

    @Transient
    protected ZoneRefStructure parentZoneRef;

    public Zone_VersionStructure() {
    }

    public Polygon getPolygon() {
        if (polygon != null) {
            return polygon.getPolygon();
        }
        return null;
    }

    public void setPolygon(Polygon polygon) {
        if (this.polygon == null) {
            this.polygon = new PersistablePolygon();
        }
        this.polygon.setPolygon(polygon);
    }

    public Zone_VersionStructure(EmbeddableMultilingualString name) {
        super(name);
    }

    public TypeOfZoneRefs_RelStructure getTypes() {
        return types;
    }

    public void setTypes(TypeOfZoneRefs_RelStructure value) {
        this.types = value;
    }

    public Point getCentroid() {
        return centroid;
    }

    public void setCentroid(Point value) {
        this.centroid = value;
    }

    public Projections_RelStructure getProjections() {
        return projections;
    }

    public void setProjections(Projections_RelStructure value) {
        this.projections = value;
    }

    public ZoneRefStructure getParentZoneRef() {
        return parentZoneRef;
    }

    public void setParentZoneRef(ZoneRefStructure value) {
        this.parentZoneRef = value;
    }

    public boolean hasCoordinates() {
        return centroid != null;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("netexId", netexId)
                .add("version", version)
                .add("name", name)
                .toString();
    }
}
