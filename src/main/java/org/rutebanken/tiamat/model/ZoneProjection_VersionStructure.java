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

public class ZoneProjection_VersionStructure
        extends Projection_VersionStructure {

    protected ZoneRefStructure projectedZoneRef;
    protected ZoneRefStructure projectToZoneRef;
    protected PointRefStructure projectToPointRef;
    protected PointRefs_RelStructure points;

    public ZoneRefStructure getProjectedZoneRef() {
        return projectedZoneRef;
    }

    public void setProjectedZoneRef(ZoneRefStructure value) {
        this.projectedZoneRef = value;
    }

    public ZoneRefStructure getProjectToZoneRef() {
        return projectToZoneRef;
    }

    public void setProjectToZoneRef(ZoneRefStructure value) {
        this.projectToZoneRef = value;
    }

    public PointRefStructure getProjectToPointRef() {
        return projectToPointRef;
    }

    public void setProjectToPointRef(PointRefStructure value) {
        this.projectToPointRef = value;
    }

    public PointRefs_RelStructure getPoints() {
        return points;
    }

    public void setPoints(PointRefs_RelStructure value) {
        this.points = value;
    }

}
