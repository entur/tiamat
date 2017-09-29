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

public class VehicleStoppingPlace_VersionStructure
        extends StopPlaceSpace_VersionStructure {

    protected RelationToVehicleEnumeration relationToVehicle;
    protected InfrastructureLinkRefStructure infrastructureElementRef;
    protected VehicleStoppingPositions_RelStructure vehicleStoppingPositions;
    protected VehicleQuayAlignments_RelStructure quayAlignments;

    public RelationToVehicleEnumeration getRelationToVehicle() {
        return relationToVehicle;
    }

    public void setRelationToVehicle(RelationToVehicleEnumeration value) {
        this.relationToVehicle = value;
    }

    public InfrastructureLinkRefStructure getInfrastructureElementRef() {
        return infrastructureElementRef;
    }

    public void setInfrastructureElementRef(InfrastructureLinkRefStructure value) {
        this.infrastructureElementRef = value;
    }

    public VehicleStoppingPositions_RelStructure getVehicleStoppingPositions() {
        return vehicleStoppingPositions;
    }

    public void setVehicleStoppingPositions(VehicleStoppingPositions_RelStructure value) {
        this.vehicleStoppingPositions = value;
    }

    public VehicleQuayAlignments_RelStructure getQuayAlignments() {
        return quayAlignments;
    }

    public void setQuayAlignments(VehicleQuayAlignments_RelStructure value) {
        this.quayAlignments = value;
    }

}
