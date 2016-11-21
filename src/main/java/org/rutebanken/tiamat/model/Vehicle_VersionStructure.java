package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class Vehicle_VersionStructure
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity shortName;
    protected String registrationNumber;
    protected String operationalNumber;
    protected PrivateCodeStructure privateCode;
    protected OperatorRefStructure operatorRef;
    protected JAXBElement<? extends VehicleTypeRefStructure> vehicleTypeRef;
    protected Equipments_RelStructure actualVehicleEquipments;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String value) {
        this.registrationNumber = value;
    }

    public String getOperationalNumber() {
        return operationalNumber;
    }

    public void setOperationalNumber(String value) {
        this.operationalNumber = value;
    }

    public PrivateCodeStructure getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(PrivateCodeStructure value) {
        this.privateCode = value;
    }

    public OperatorRefStructure getOperatorRef() {
        return operatorRef;
    }

    public void setOperatorRef(OperatorRefStructure value) {
        this.operatorRef = value;
    }

    public JAXBElement<? extends VehicleTypeRefStructure> getVehicleTypeRef() {
        return vehicleTypeRef;
    }

    public void setVehicleTypeRef(JAXBElement<? extends VehicleTypeRefStructure> value) {
        this.vehicleTypeRef = value;
    }

    public Equipments_RelStructure getActualVehicleEquipments() {
        return actualVehicleEquipments;
    }

    public void setActualVehicleEquipments(Equipments_RelStructure value) {
        this.actualVehicleEquipments = value;
    }

}
