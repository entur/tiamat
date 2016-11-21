

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class VehicleStoppingPlace_VersionStructure
    extends StopPlaceSpace_VersionStructure
{

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
