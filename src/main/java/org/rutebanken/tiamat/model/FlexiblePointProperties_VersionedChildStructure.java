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

import javax.xml.bind.JAXBElement;


public class FlexiblePointProperties_VersionedChildStructure
        extends VersionedChildStructure {

    protected PointOnRouteRefStructure pointOnRouteRef;
    protected JAXBElement<? extends PointRefStructure> pointRef;
    protected Boolean mayBeSkipped;
    protected Boolean onMainRoute;
    protected Boolean pointStandingForAZone;
    protected Boolean zoneContainingStops;

    public PointOnRouteRefStructure getPointOnRouteRef() {
        return pointOnRouteRef;
    }

    public void setPointOnRouteRef(PointOnRouteRefStructure value) {
        this.pointOnRouteRef = value;
    }

    public JAXBElement<? extends PointRefStructure> getPointRef() {
        return pointRef;
    }

    public void setPointRef(JAXBElement<? extends PointRefStructure> value) {
        this.pointRef = value;
    }

    public Boolean isMayBeSkipped() {
        return mayBeSkipped;
    }

    public void setMayBeSkipped(Boolean value) {
        this.mayBeSkipped = value;
    }

    public Boolean isOnMainRoute() {
        return onMainRoute;
    }

    public void setOnMainRoute(Boolean value) {
        this.onMainRoute = value;
    }

    public Boolean isPointStandingForAZone() {
        return pointStandingForAZone;
    }

    public void setPointStandingForAZone(Boolean value) {
        this.pointStandingForAZone = value;
    }

    public Boolean isZoneContainingStops() {
        return zoneContainingStops;
    }

    public void setZoneContainingStops(Boolean value) {
        this.zoneContainingStops = value;
    }

}
