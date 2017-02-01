package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class VehicleModel_VersionStructure
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity description;
    protected MultilingualStringEntity manufacturer;
    protected JAXBElement<? extends VehicleTypeRefStructure> vehicleTypeRef;

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

    public MultilingualStringEntity getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(MultilingualStringEntity value) {
        this.manufacturer = value;
    }

    public JAXBElement<? extends VehicleTypeRefStructure> getVehicleTypeRef() {
        return vehicleTypeRef;
    }

    public void setVehicleTypeRef(JAXBElement<? extends VehicleTypeRefStructure> value) {
        this.vehicleTypeRef = value;
    }

}
