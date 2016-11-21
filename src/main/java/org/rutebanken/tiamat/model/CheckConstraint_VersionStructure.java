

package org.rutebanken.tiamat.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "placeRef",
    "checkDirection",
    "checkProcess",
    "checkService",
    "accessFeatureType",
    "congestion",
    "classOfUseRef",
    "typeOfEquipmentRef",
    "facilityRef",
    "equipmentRef",
    "delays",
@MappedSuperclass
public class CheckConstraint_VersionStructure
    extends Assignment_VersionStructure
{

    @Transient
    protected PlaceRef placeRef;

    @Transient
    protected CheckDirectionEnumeration checkDirection;

    @Transient
    protected CheckProcessTypeEnumeration checkProcess;

    @Transient
    protected CheckServiceEnumeration checkService;

    @Transient
    protected AccessFeatureEnumeration accessFeatureType;

    @Transient
    protected CongestionEnumeration congestion;

    @Transient
    protected ClassOfUseRef classOfUseRef;

    @Transient
    protected TypeOfEquipmentRefStructure typeOfEquipmentRef;

    @Transient
    protected FacilityRefStructure facilityRef;

    @Transient
    protected JAXBElement<? extends EquipmentRefStructure> equipmentRef;

    @Transient
    protected Delays delays;

    @Transient
    protected Throughput throughput;

    public PlaceRef getPlaceRef() {
        return placeRef;
    }

    public void setPlaceRef(PlaceRef value) {
        this.placeRef = value;
    }

    public CheckDirectionEnumeration getCheckDirection() {
        return checkDirection;
    }

    public void setCheckDirection(CheckDirectionEnumeration value) {
        this.checkDirection = value;
    }

    public CheckProcessTypeEnumeration getCheckProcess() {
        return checkProcess;
    }

    public void setCheckProcess(CheckProcessTypeEnumeration value) {
        this.checkProcess = value;
    }

    public CheckServiceEnumeration getCheckService() {
        return checkService;
    }

    public void setCheckService(CheckServiceEnumeration value) {
        this.checkService = value;
    }

    public AccessFeatureEnumeration getAccessFeatureType() {
        return accessFeatureType;
    }

    public void setAccessFeatureType(AccessFeatureEnumeration value) {
        this.accessFeatureType = value;
    }

    public CongestionEnumeration getCongestion() {
        return congestion;
    }

    public void setCongestion(CongestionEnumeration value) {
        this.congestion = value;
    }

    public ClassOfUseRef getClassOfUseRef() {
        return classOfUseRef;
    }

    public void setClassOfUseRef(ClassOfUseRef value) {
    }

    public TypeOfEquipmentRefStructure getTypeOfEquipmentRef() {
        return typeOfEquipmentRef;
    }

    public void setTypeOfEquipmentRef(TypeOfEquipmentRefStructure value) {
        this.typeOfEquipmentRef = value;
    }

    public FacilityRefStructure getFacilityRef() {
        return facilityRef;
    }

    public void setFacilityRef(FacilityRefStructure value) {
        this.facilityRef = value;
    }

    public JAXBElement<? extends EquipmentRefStructure> getEquipmentRef() {
        return equipmentRef;
    }

    public void setEquipmentRef(JAXBElement<? extends EquipmentRefStructure> value) {
        this.equipmentRef = value;
    }

    public Delays getDelays() {
        return delays;
    }

    public void setDelays(Delays value) {
        this.delays = value;
    }

    public Throughput getThroughput() {
        return throughput;
    }

    public void setThroughput(Throughput value) {
        this.throughput = value;
    }

}
