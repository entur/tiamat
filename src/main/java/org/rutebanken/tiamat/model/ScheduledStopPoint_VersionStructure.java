

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class ScheduledStopPoint_VersionStructure
    extends TimingPoint_VersionStructure
{

    protected StopAreaRefs_RelStructure stopAreas;
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
    protected List<VehicleModeEnumeration> vehicleModes;
    protected Boolean forAlighting;
    protected Boolean forBoarding;
    protected Boolean requestStop;
    protected CountryRef countryRef;
    protected TopographicPlaceRefStructure topographicPlaceRef;
    protected TopographicPlaceView topographicPlaceView;
    protected Boolean atCentre;

    public StopAreaRefs_RelStructure getStopAreas() {
        return stopAreas;
    }

    public void setStopAreas(StopAreaRefs_RelStructure value) {
        this.stopAreas = value;
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

    public List<VehicleModeEnumeration> getVehicleModes() {
        if (vehicleModes == null) {
            vehicleModes = new ArrayList<VehicleModeEnumeration>();
        }
        return this.vehicleModes;
    }

    public Boolean isForAlighting() {
        return forAlighting;
    }

    public void setForAlighting(Boolean value) {
        this.forAlighting = value;
    }

    public Boolean isForBoarding() {
        return forBoarding;
    }

    public void setForBoarding(Boolean value) {
        this.forBoarding = value;
    }

    public Boolean isRequestStop() {
        return requestStop;
    }

    public void setRequestStop(Boolean value) {
        this.requestStop = value;
    }

    public CountryRef getCountryRef() {
        return countryRef;
    }

    public void setCountryRef(CountryRef value) {
        this.countryRef = value;
    }

    public TopographicPlaceRefStructure getTopographicPlaceRef() {
        return topographicPlaceRef;
    }

    public void setTopographicPlaceRef(TopographicPlaceRefStructure value) {
        this.topographicPlaceRef = value;
    }

    public TopographicPlaceView getTopographicPlaceView() {
        return topographicPlaceView;
    }

    public void setTopographicPlaceView(TopographicPlaceView value) {
        this.topographicPlaceView = value;
    }

    public Boolean isAtCentre() {
        return atCentre;
    }

    public void setAtCentre(Boolean value) {
        this.atCentre = value;
    }

}
