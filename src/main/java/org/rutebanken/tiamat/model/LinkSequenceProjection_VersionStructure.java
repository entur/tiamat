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


public class LinkSequenceProjection_VersionStructure
        extends Projection_VersionStructure {

    protected LinkSequenceRefStructure projectedLinkSequenceRef;
    protected BigDecimal distance;
    protected PointRefs_RelStructure points;

    public LinkSequenceRefStructure getProjectedLinkSequenceRef() {
        return projectedLinkSequenceRef;
    }

    public void setProjectedLinkSequenceRef(LinkSequenceRefStructure value) {
        this.projectedLinkSequenceRef = value;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal value) {
        this.distance = value;
    }

    public PointRefs_RelStructure getPoints() {
        return points;
    }

    public void setPoints(PointRefs_RelStructure value) {
        this.points = value;
    }
}
