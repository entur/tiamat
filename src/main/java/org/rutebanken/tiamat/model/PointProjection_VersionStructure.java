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

import java.math.BigDecimal;


public class PointProjection_VersionStructure
        extends Projection_VersionStructure {

    protected PointRefStructure projectedPointRef;
    protected PointRefStructure projectToPointRef;
    protected LinkRefStructure projectToLinkRef;
    protected BigDecimal distance;

    public PointRefStructure getProjectedPointRef() {
        return projectedPointRef;
    }

    public void setProjectedPointRef(PointRefStructure value) {
        this.projectedPointRef = value;
    }

    public PointRefStructure getProjectToPointRef() {
        return projectToPointRef;
    }

    public void setProjectToPointRef(PointRefStructure value) {
        this.projectToPointRef = value;
    }

    public LinkRefStructure getProjectToLinkRef() {
        return projectToLinkRef;
    }

    public void setProjectToLinkRef(LinkRefStructure value) {
        this.projectToLinkRef = value;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal value) {
        this.distance = value;
    }

}
