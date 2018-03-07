/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;


@MappedSuperclass
public abstract class StopPlaceComponent_VersionStructure
        extends SiteComponent_VersionStructure {

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

    public StopPlaceComponent_VersionStructure() {
    }

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
            otherTransportModes = new ArrayList<>();
        }
        return this.otherTransportModes;
    }

}
