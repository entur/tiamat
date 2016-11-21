package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class ScheduledStopPoint_DerivedViewStructure
        extends DerivedViewStructure {

    protected JAXBElement<? extends ScheduledStopPointRefStructure> scheduledStopPointRef;
    protected MultilingualStringEntity name;
    protected TypeOfPointRefStructure typeOfPointRef;
    protected MultilingualStringEntity shortName;
    protected MultilingualStringEntity description;
    protected MultilingualStringEntity label;
    protected String shortStopCode;
    protected String publicCode;
    protected PrivateCodeStructure privateCode;
    protected ExternalObjectRefStructure externalStopPointRef;
    protected String url;
    protected StopTypeEnumeration stopType;
    protected Float compassBearing;
    protected PresentationStructure presentation;

    public JAXBElement<? extends ScheduledStopPointRefStructure> getScheduledStopPointRef() {
        return scheduledStopPointRef;
    }

    public void setScheduledStopPointRef(JAXBElement<? extends ScheduledStopPointRefStructure> value) {
        this.scheduledStopPointRef = value;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public TypeOfPointRefStructure getTypeOfPointRef() {
        return typeOfPointRef;
    }

    public void setTypeOfPointRef(TypeOfPointRefStructure value) {
        this.typeOfPointRef = value;
    }

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public MultilingualStringEntity getLabel() {
        return label;
    }

    public void setLabel(MultilingualStringEntity value) {
        this.label = value;
    }

    public String getShortStopCode() {
        return shortStopCode;
    }

    public void setShortStopCode(String value) {
        this.shortStopCode = value;
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

    public ExternalObjectRefStructure getExternalStopPointRef() {
        return externalStopPointRef;
    }

    public void setExternalStopPointRef(ExternalObjectRefStructure value) {
        this.externalStopPointRef = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String value) {
        this.url = value;
    }

    public StopTypeEnumeration getStopType() {
        return stopType;
    }

    public void setStopType(StopTypeEnumeration value) {
        this.stopType = value;
    }

    public Float getCompassBearing() {
        return compassBearing;
    }

    public void setCompassBearing(Float value) {
        this.compassBearing = value;
    }

    public PresentationStructure getPresentation() {
        return presentation;
    }

    public void setPresentation(PresentationStructure value) {
        this.presentation = value;
    }

}
