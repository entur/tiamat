package org.rutebanken.tiamat.model;

import java.math.BigDecimal;


public class TrainElement_VersionStructure
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity description;
    protected TrainElementTypeEnumeration trainElementType;

    protected PassengerCapacityStructure passengerCapacity;
    protected PassengerCapacities_RelStructure capacities;

    protected BigDecimal length;
    protected ServiceFacilitySets_RelStructure facilities;
    protected Equipments_RelStructure equipments;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public TrainElementTypeEnumeration getTrainElementType() {
        return trainElementType;
    }

    public void setTrainElementType(TrainElementTypeEnumeration value) {
        this.trainElementType = value;
    }

    public PassengerCapacityStructure getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setPassengerCapacity(PassengerCapacityStructure value) {
        this.passengerCapacity = value;
    }

    public PassengerCapacities_RelStructure getCapacities() {
        return capacities;
    }

    public void setCapacities(PassengerCapacities_RelStructure value) {
        this.capacities = value;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal value) {
        this.length = value;
    }

    public ServiceFacilitySets_RelStructure getFacilities() {
        return facilities;
    }

    public void setFacilities(ServiceFacilitySets_RelStructure value) {
        this.facilities = value;
    }

    public Equipments_RelStructure getEquipments() {
        return equipments;
    }

    public void setEquipments(Equipments_RelStructure value) {
        this.equipments = value;
    }

}
