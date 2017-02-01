package org.rutebanken.tiamat.model;

import javax.persistence.*;


@MappedSuperclass
public class DestinationDisplay_DerivedViewStructure
        extends DerivedViewStructure {


    @Transient
    protected DestinationDisplayRefStructure destinationDisplayRef;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected MultilingualStringEntity name;

    @Transient
    protected MultilingualStringEntity shortName;

    @Transient
    protected MultilingualStringEntity sideText;

    @Transient
    protected MultilingualStringEntity frontText;

    @Transient
    protected MultilingualStringEntity driverDisplayText;

    protected String shortCode;

    protected String publicCode;

    @Transient
    protected PrivateCodeStructure privateCode;

    public DestinationDisplayRefStructure getDestinationDisplayRef() {
        return destinationDisplayRef;
    }

    public void setDestinationDisplayRef(DestinationDisplayRefStructure value) {
        this.destinationDisplayRef = value;
    }

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

}
