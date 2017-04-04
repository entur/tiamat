package org.rutebanken.tiamat.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class Equipment_VersionStructure
        extends EntityInVersionStructure {

    @Transient
    protected MultilingualStringEntity name;
    @Transient
    protected PrivateCodeStructure privateCode;
    @Transient
    protected PrivateCodeStructure publicCode;
    @Transient
    protected String image;
    @Transient
    protected TypeOfEquipmentRefStructure typeOfEquipmentRef;
    @Transient
    protected MultilingualStringEntity description;
    @Transient
    protected MultilingualStringEntity note;
    protected Boolean outOfService;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public PrivateCodeStructure getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(PrivateCodeStructure value) {
        this.privateCode = value;
    }

    public PrivateCodeStructure getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(PrivateCodeStructure value) {
        this.publicCode = value;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String value) {
        this.image = value;
    }

    public TypeOfEquipmentRefStructure getTypeOfEquipmentRef() {
        return typeOfEquipmentRef;
    }

    public void setTypeOfEquipmentRef(TypeOfEquipmentRefStructure value) {
        this.typeOfEquipmentRef = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public MultilingualStringEntity getNote() {
        return note;
    }

    public void setNote(MultilingualStringEntity value) {
        this.note = value;
    }

    public Boolean isOutOfService() {
        return outOfService;
    }

    public void setOutOfService(Boolean value) {
        this.outOfService = value;
    }

}
