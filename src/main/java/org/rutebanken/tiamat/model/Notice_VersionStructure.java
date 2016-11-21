

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class Notice_VersionStructure
    extends DataManagedObjectStructure
{

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity text;
    protected String publicCode;
    protected String shortCode;
    protected PrivateCodeStructure privateCode;
    protected TypeOfNoticeRefStructure typeOfNoticeRef;
    protected Boolean canBeAdvertised;
    protected MultilingualStringEntity driverDisplayText;
    protected DeliveryVariants_RelStructure variants;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getText() {
        return text;
    }

    public void setText(MultilingualStringEntity value) {
        this.text = value;
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String value) {
        this.shortCode = value;
    }

    public PrivateCodeStructure getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(PrivateCodeStructure value) {
        this.privateCode = value;
    }

    public TypeOfNoticeRefStructure getTypeOfNoticeRef() {
        return typeOfNoticeRef;
    }

    public void setTypeOfNoticeRef(TypeOfNoticeRefStructure value) {
        this.typeOfNoticeRef = value;
    }

    public Boolean isCanBeAdvertised() {
        return canBeAdvertised;
    }

    public void setCanBeAdvertised(Boolean value) {
        this.canBeAdvertised = value;
    }

    public MultilingualStringEntity getDriverDisplayText() {
        return driverDisplayText;
    }

    public void setDriverDisplayText(MultilingualStringEntity value) {
        this.driverDisplayText = value;
    }

    public DeliveryVariants_RelStructure getVariants() {
        return variants;
    }

    public void setVariants(DeliveryVariants_RelStructure value) {
        this.variants = value;
    }

}
