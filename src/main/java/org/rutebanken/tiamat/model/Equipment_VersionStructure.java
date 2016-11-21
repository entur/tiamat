

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "name",
    "privateCode",
    "publicCode",
    "image",
    "typeOfEquipmentRef",
    "description",
    "note",
public abstract class Equipment_VersionStructure
    extends DataManagedObjectStructure
{

    protected MultilingualStringEntity name;
    protected PrivateCodeStructure privateCode;
    protected PrivateCodeStructure publicCode;
    protected String image;
    protected TypeOfEquipmentRefStructure typeOfEquipmentRef;
    protected MultilingualStringEntity description;
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
