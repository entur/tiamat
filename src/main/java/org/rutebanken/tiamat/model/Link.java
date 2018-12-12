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

import org.locationtech.jts.geom.LineString;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.math.BigDecimal;

@MappedSuperclass
public abstract class Link extends DataManagedObjectStructure {

    @Transient
    protected MultilingualStringEntity name;
    @Transient
    protected BigDecimal distance;
    @Transient
    protected LinkTypeRefs_RelStructure types;
    @Transient
    protected Projections_RelStructure projections;
    @Transient
    protected PointsOnLink_RelStructure passingThrough;
    private LineString lineString;

    public Link() {
    }

    public Link(LineString lineString) {
        this.lineString = lineString;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal value) {
        this.distance = value;
    }

    public LinkTypeRefs_RelStructure getTypes() {
        return types;
    }

    public void setTypes(LinkTypeRefs_RelStructure value) {
        this.types = value;
    }

    public LineString getLineString() {
        return lineString;
    }

    public void setLineString(LineString lineString) {
        this.lineString = lineString;
    }

    public Projections_RelStructure getProjections() {
        return projections;
    }

    public void setProjections(Projections_RelStructure value) {
        this.projections = value;
    }

    public PointsOnLink_RelStructure getPassingThrough() {
        return passingThrough;
    }

    public void setPassingThrough(PointsOnLink_RelStructure value) {
        this.passingThrough = value;
    }

}
