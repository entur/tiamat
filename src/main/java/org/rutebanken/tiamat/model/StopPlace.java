package org.rutebanken.tiamat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(
        indexes = {
                @Index(name = "stop_place_topographic_place_ref_index", columnList = "topographic_place_ref"),
                @Index(name = "stop_place_name_value_index", columnList = "name_value"),
                @Index(name = "stop_place_type_index", columnList = "stopPlaceType")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "netex_id_version_constraint", columnNames = {"netexId", "version"})}
)
public class StopPlace
        extends Site_VersionStructure implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AccessSpace> accessSpaces = new ArrayList<>();
    protected String publicCode;

    @Enumerated(EnumType.STRING)
    protected VehicleModeEnumeration transportMode;

    @Enumerated(EnumType.STRING)
    protected AirSubmodeEnumeration airSubmode = AirSubmodeEnumeration.UNKNOWN;

    @Enumerated(EnumType.STRING)
    protected BusSubmodeEnumeration busSubmode;

    @Enumerated(EnumType.STRING)
    protected CoachSubmodeEnumeration coachSubmode;

    @Enumerated(EnumType.STRING)
    protected FunicularSubmodeEnumeration funicularSubmode;

    @Enumerated(EnumType.STRING)
    protected MetroSubmodeEnumeration metroSubmode;

    @Enumerated(EnumType.STRING)
    protected TramSubmodeEnumeration tramSubmode;

    @Enumerated(EnumType.STRING)
    protected TelecabinSubmodeEnumeration telecabinSubmode;

    @Enumerated(EnumType.STRING)
    protected RailSubmodeEnumeration railSubmode;

    @Enumerated(EnumType.STRING)
    protected WaterSubmodeEnumeration waterSubmode;
    @Enumerated(EnumType.STRING)
    @Transient
    protected List<VehicleModeEnumeration> otherTransportModes;

    @Enumerated(EnumType.STRING)
    protected StopTypeEnumeration stopPlaceType;

    protected Boolean borderCrossing;
    @Enumerated(value = EnumType.STRING)
    protected InterchangeWeightingEnumeration weighting;
    @OneToOne(fetch = FetchType.LAZY)
    @Transient
    protected SitePathLinks_RelStructure pathLinks;
    @OneToOne(fetch = FetchType.LAZY)
    @Transient
    protected PathJunctions_RelStructure pathJunctions;
    @OneToOne(fetch = FetchType.LAZY)
    @Transient
    protected NavigationPaths_RelStructure navigationPaths;

    @org.hibernate.annotations.Cache(
            usage = CacheConcurrencyStrategy.READ_WRITE
    )
    @OneToOne(fetch = FetchType.LAZY)
    protected TopographicPlace topographicPlace;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Quay> quays = new HashSet<>();

    public StopPlace(EmbeddableMultilingualString name) {
        super(name);
    }

    public StopPlace() {
    }

    public TopographicPlace getTopographicPlace() {
        return topographicPlace;
    }

    public void setTopographicPlace(TopographicPlace topographicPlace) {
        this.topographicPlace = topographicPlace;
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
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

    @JsonIgnore
    public List<VehicleModeEnumeration> getOtherTransportModes() {
        if (otherTransportModes == null) {
            otherTransportModes = new ArrayList<VehicleModeEnumeration>();
        }
        return this.otherTransportModes;
    }

    public StopTypeEnumeration getStopPlaceType() {
        return stopPlaceType;
    }

    public void setStopPlaceType(StopTypeEnumeration value) {
        this.stopPlaceType = value;
    }

    public Boolean isBorderCrossing() {
        return borderCrossing;
    }

    public void setBorderCrossing(Boolean value) {
        this.borderCrossing = value;
    }

    public InterchangeWeightingEnumeration getWeighting() {
        return weighting;
    }

    public void setWeighting(InterchangeWeightingEnumeration value) {
        this.weighting = value;
    }

    public SitePathLinks_RelStructure getPathLinks() {
        return pathLinks;
    }

    public void setPathLinks(SitePathLinks_RelStructure value) {
        this.pathLinks = value;
    }

    public PathJunctions_RelStructure getPathJunctions() {
        return pathJunctions;
    }

    public void setPathJunctions(PathJunctions_RelStructure value) {
        this.pathJunctions = value;
    }

    public NavigationPaths_RelStructure getNavigationPaths() {
        return navigationPaths;
    }

    public void setNavigationPaths(NavigationPaths_RelStructure value) {
        this.navigationPaths = value;
    }


    public List<AccessSpace> getAccessSpaces() {
        return accessSpaces;
    }

    public Set<Quay> getQuays() {
        return quays;
    }

    public void setQuays(Set<Quay> quays) {
        this.quays = quays;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof StopPlace)) {
            return false;
        }

        StopPlace other = (StopPlace) object;

        return Objects.equals(this.name, other.name)
                && Objects.equals(this.centroid, other.centroid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, centroid);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", id)
                .add("name", name)
                .add("stopPlaceType", stopPlaceType)
                .add("centroid", centroid)
                .add("keyValues", getKeyValues())
                .add("quays", quays)
                .toString();
    }
}
