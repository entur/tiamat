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


public class OvertakingPossibility_VersionStructure
        extends NetworkRestriction_VersionStructure {

    protected BigDecimal overtakingWidth;
    protected LinkRefStructure overtakingOnLinkRef;
    protected PointRefStructure overtakingAtPointRef;
    protected VehicleTypeRefStructure overtakingVehicleTypeRef;
    protected VehicleTypeRefStructure overtakenVehicleTypeRef;

    public BigDecimal getOvertakingWidth() {
        return overtakingWidth;
    }

    public void setOvertakingWidth(BigDecimal value) {
        this.overtakingWidth = value;
    }

    public LinkRefStructure getOvertakingOnLinkRef() {
        return overtakingOnLinkRef;
    }

    public void setOvertakingOnLinkRef(LinkRefStructure value) {
        this.overtakingOnLinkRef = value;
    }

    public PointRefStructure getOvertakingAtPointRef() {
        return overtakingAtPointRef;
    }

    public void setOvertakingAtPointRef(PointRefStructure value) {
        this.overtakingAtPointRef = value;
    }

    public VehicleTypeRefStructure getOvertakingVehicleTypeRef() {
        return overtakingVehicleTypeRef;
    }

    public void setOvertakingVehicleTypeRef(VehicleTypeRefStructure value) {
        this.overtakingVehicleTypeRef = value;
    }

    public VehicleTypeRefStructure getOvertakenVehicleTypeRef() {
        return overtakenVehicleTypeRef;
    }

    public void setOvertakenVehicleTypeRef(VehicleTypeRefStructure value) {
        this.overtakenVehicleTypeRef = value;
    }

}
