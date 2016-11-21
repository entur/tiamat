

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


    "transportMode",
    "airSubmode",
    "busSubmode",
    "coachSubmode",
    "funicularSubmode",
    "metroSubmode",
    "tramSubmode",
    "telecabinSubmode",
    "railSubmode",
    "waterSubmode",
@MappedSuperclass
public abstract class StopPlaceComponent_VersionStructure
    extends SiteComponent_VersionStructure
{

    @Transient
    protected VehicleModeEnumeration transportMode;

    @Transient
    protected AirSubmodeEnumeration airSubmode;

    @Transient
    protected BusSubmodeEnumeration busSubmode;

    @Transient
    protected CoachSubmodeEnumeration coachSubmode;

    @Transient
    protected FunicularSubmodeEnumeration funicularSubmode;

    @Transient
    protected MetroSubmodeEnumeration metroSubmode;

    @Transient
    protected TramSubmodeEnumeration tramSubmode;

    @Transient
    protected TelecabinSubmodeEnumeration telecabinSubmode;

    @Transient
    protected RailSubmodeEnumeration railSubmode;

    @Transient
    protected WaterSubmodeEnumeration waterSubmode;

    @Transient
    protected List<VehicleModeEnumeration> otherTransportModes;

    public StopPlaceComponent_VersionStructure(EmbeddableMultilingualString name) {
        super(name);
    }

    public StopPlaceComponent_VersionStructure() { }

    public VehicleModeEnumeration getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(VehicleModeEnumeration value) {
        this.transportMode = value;
    }

    public AirSubmodeEnumeration getAirSubmode() {
        return airSubmode;
    }

    public void setAirSubmode(AirSubmodeEnumeration value) {
        this.airSubmode = value;
    }

    public BusSubmodeEnumeration getBusSubmode() {
        return busSubmode;
    }

    public void setBusSubmode(BusSubmodeEnumeration value) {
        this.busSubmode = value;
    }

    public CoachSubmodeEnumeration getCoachSubmode() {
        return coachSubmode;
    }

    public void setCoachSubmode(CoachSubmodeEnumeration value) {
        this.coachSubmode = value;
    }

    public FunicularSubmodeEnumeration getFunicularSubmode() {
        return funicularSubmode;
    }

    public void setFunicularSubmode(FunicularSubmodeEnumeration value) {
        this.funicularSubmode = value;
    }

    public MetroSubmodeEnumeration getMetroSubmode() {
        return metroSubmode;
    }

    public void setMetroSubmode(MetroSubmodeEnumeration value) {
        this.metroSubmode = value;
    }

    public TramSubmodeEnumeration getTramSubmode() {
        return tramSubmode;
    }

    public void setTramSubmode(TramSubmodeEnumeration value) {
        this.tramSubmode = value;
    }

    public TelecabinSubmodeEnumeration getTelecabinSubmode() {
        return telecabinSubmode;
    }

    public void setTelecabinSubmode(TelecabinSubmodeEnumeration value) {
        this.telecabinSubmode = value;
    }

    public RailSubmodeEnumeration getRailSubmode() {
        return railSubmode;
    }

    public void setRailSubmode(RailSubmodeEnumeration value) {
        this.railSubmode = value;
    }

    public WaterSubmodeEnumeration getWaterSubmode() {
        return waterSubmode;
    }

    public void setWaterSubmode(WaterSubmodeEnumeration value) {
        this.waterSubmode = value;
    }

    public List<VehicleModeEnumeration> getOtherTransportModes() {
        if (otherTransportModes == null) {
            otherTransportModes = new ArrayList<VehicleModeEnumeration>();
        }
        return this.otherTransportModes;
    }

}
