package org.rutebanken.tiamat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(indexes = {@Index(name = "name_value_index", columnList = "name_value"),
        @Index(name="topographic_place_ref_index", columnList = "topographic_place_ref"),
        @Index(name="stop_place_type_index", columnList = "stopPlaceType")})
@GraphQLType
public class StopPlace
        extends Site_VersionStructure implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AccessSpace> accessSpaces = new ArrayList<>();
    @GraphQLField
    protected String publicCode;

    @GraphQLField
    @Enumerated(EnumType.STRING)
    protected VehicleModeEnumeration transportMode;

    @GraphQLField
    @Enumerated(EnumType.STRING)
    protected AirSubmodeEnumeration airSubmode = AirSubmodeEnumeration.UNKNOWN;

    @GraphQLField
    @Enumerated(EnumType.STRING)
    protected BusSubmodeEnumeration busSubmode;

    @GraphQLField
    @Enumerated(EnumType.STRING)
    protected CoachSubmodeEnumeration coachSubmode;

    @GraphQLField
    @Enumerated(EnumType.STRING)
    protected FunicularSubmodeEnumeration funicularSubmode;

    @GraphQLField
    @Enumerated(EnumType.STRING)
    protected MetroSubmodeEnumeration metroSubmode;

    @GraphQLField
    @Enumerated(EnumType.STRING)
    protected TramSubmodeEnumeration tramSubmode;

    @GraphQLField
    @Enumerated(EnumType.STRING)
    protected TelecabinSubmodeEnumeration telecabinSubmode;

    @GraphQLField
    @Enumerated(EnumType.STRING)
    protected RailSubmodeEnumeration railSubmode;

    @GraphQLField
    @Enumerated(EnumType.STRING)
    protected WaterSubmodeEnumeration waterSubmode;
    @Enumerated(EnumType.STRING)
    @Transient
    protected List<VehicleModeEnumeration> otherTransportModes;

    @GraphQLField
    @Enumerated(EnumType.STRING)
    protected StopTypeEnumeration stopPlaceType;

    @GraphQLField
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

    @OneToMany(cascade = CascadeType.MERGE)
    private Set<Quay> quays = new HashSet<>();

    public StopPlace(EmbeddableMultilingualString name) {
        super(name);
    }

    public StopPlace() {
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

    @JsonIgnore
    @GraphQLField
    @GraphQLName("quays")
    public List<Quay> getQuaysAsList() {
        /*
         * TODO: Remove this when graphql-java-annotations supports Set
         * https://github.com/graphql-java/graphql-java-annotations/pull/57
         */
        return quays.stream().collect(Collectors.toList());
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
                .add("quays", quays)
                .add("centroid", centroid)
                .add("keyValues", getKeyValues())
                .toString();
    }
}
