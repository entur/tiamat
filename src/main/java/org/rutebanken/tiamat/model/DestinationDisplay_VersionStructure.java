

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


    "name",
    "shortName",
    "sideText",
    "frontText",
    "driverDisplayText",
    "shortCode",
    "publicCode",
    "privateCode",
    "vias",
public class DestinationDisplay_VersionStructure
    extends DataManagedObjectStructure
{

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity shortName;
    protected MultilingualStringEntity sideText;
    protected MultilingualStringEntity frontText;
    protected MultilingualStringEntity driverDisplayText;
    protected String shortCode;
    protected String publicCode;
    protected PrivateCodeStructure privateCode;
    protected Vias_RelStructure vias;
    protected Variants variants;

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

    public MultilingualStringEntity getSideText() {
        return sideText;
    }

    public void setSideText(MultilingualStringEntity value) {
        this.sideText = value;
    }

    public MultilingualStringEntity getFrontText() {
        return frontText;
    }

    public void setFrontText(MultilingualStringEntity value) {
        this.frontText = value;
    }

    public MultilingualStringEntity getDriverDisplayText() {
        return driverDisplayText;
    }

    public void setDriverDisplayText(MultilingualStringEntity value) {
        this.driverDisplayText = value;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String value) {
        this.shortCode = value;
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public PrivateCodeStructure getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(PrivateCodeStructure value) {
        this.privateCode = value;
    }

    public Vias_RelStructure getVias() {
        return vias;
    }

    public void setVias(Vias_RelStructure value) {
        this.vias = value;
    }

    public Variants getVariants() {
        return variants;
    }

    public void setVariants(Variants value) {
        this.variants = value;
    }

}
