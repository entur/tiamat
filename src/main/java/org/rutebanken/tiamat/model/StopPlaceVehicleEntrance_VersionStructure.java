

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
public class StopPlaceVehicleEntrance_VersionStructure
    extends VehicleEntrance_VersionStructure
{

    protected VehicleModeEnumeration transportMode;
    protected AirSubmodeEnumeration airSubmode;
    protected BusSubmodeEnumeration busSubmode;
    protected CoachSubmodeEnumeration coachSubmode;
    protected FunicularSubmodeEnumeration funicularSubmode;
    protected MetroSubmodeEnumeration metroSubmode;
    protected TramSubmodeEnumeration tramSubmode;
    protected TelecabinSubmodeEnumeration telecabinSubmode;
    protected RailSubmodeEnumeration railSubmode;
    protected WaterSubmodeEnumeration waterSubmode;

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

}
