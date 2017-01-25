package org.rutebanken.tiamat.model;

import javax.persistence.*;
import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public abstract class Site_VersionStructure
        extends SiteElement_VersionStructure {

    @OneToMany(cascade = CascadeType.ALL)
    @Transient
    private final List<Level> levels = new ArrayList<>();
    @AttributeOverrides({
            @AttributeOverride(name = "ref", column = @Column(name = "topographic_place_ref")),
            @AttributeOverride(name = "version", column = @Column(name = "topographic_place_version"))
    })
    @Embedded
    protected TopographicPlaceRefStructure topographicPlaceRef;
    @Transient
    protected TopographicPlaceView topographicPlaceView;
    @Transient
    protected TopographicPlaceRefs_RelStructure additionalTopographicPlaces;
    @Transient
    protected SiteTypeEnumeration siteType;
    @Transient
    protected Boolean atCentre;
    @Transient
    protected LocaleStructure locale;
    @Transient
    protected JAXBElement<? extends OrganisationRefStructure> organisationRef;
    @Transient
    protected Organisation_DerivedViewStructure operatingOrganisationView;
    @AttributeOverrides({
            @AttributeOverride(name = "ref", column = @Column(name = "parent_site_ref")),
            @AttributeOverride(name = "version", column = @Column(name = "parent_site_version"))
    })
    @Embedded
    protected SiteRefStructure parentSiteRef;
    @Transient
    protected SiteRefs_RelStructure adjacentSites;
    @Transient
    protected TopographicPlaceRefStructure containedInPlaceRef;
    @Transient
    protected SiteEntrances_RelStructure entrances;
    @Transient
    protected PlaceEquipments_RelStructure placeEquipments;
    @Transient
    protected LocalServices_RelStructure localServices;
    @OneToMany(cascade = CascadeType.ALL)
    private List<EquipmentPlace> equipmentPlaces;

    public Site_VersionStructure(EmbeddableMultilingualString name) {
        super(name);
    }

    public Site_VersionStructure() {
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

    public TopographicPlaceRefs_RelStructure getAdditionalTopographicPlaces() {
        return additionalTopographicPlaces;
    }

    public void setAdditionalTopographicPlaces(TopographicPlaceRefs_RelStructure value) {
        this.additionalTopographicPlaces = value;
    }

    public SiteTypeEnumeration getSiteType() {
        return siteType;
    }

    public void setSiteType(SiteTypeEnumeration value) {
        this.siteType = value;
    }

    public Boolean isAtCentre() {
        return atCentre;
    }

    public void setAtCentre(Boolean value) {
        this.atCentre = value;
    }

    public LocaleStructure getLocale() {
        return locale;
    }

    public void setLocale(LocaleStructure value) {
        this.locale = value;
    }

    public JAXBElement<? extends OrganisationRefStructure> getOrganisationRef() {
        return organisationRef;
    }

    public void setOrganisationRef(JAXBElement<? extends OrganisationRefStructure> value) {
        this.organisationRef = value;
    }

    public Organisation_DerivedViewStructure getOperatingOrganisationView() {
        return operatingOrganisationView;
    }

    public void setOperatingOrganisationView(Organisation_DerivedViewStructure value) {
        this.operatingOrganisationView = value;
    }

    public SiteRefStructure getParentSiteRef() {
        return parentSiteRef;
    }

    public void setParentSiteRef(SiteRefStructure value) {
        this.parentSiteRef = value;
    }

    public SiteRefs_RelStructure getAdjacentSites() {
        return adjacentSites;
    }

    public void setAdjacentSites(SiteRefs_RelStructure value) {
        this.adjacentSites = value;
    }

    public TopographicPlaceRefStructure getContainedInPlaceRef() {
        return containedInPlaceRef;
    }

    public void setContainedInPlaceRef(TopographicPlaceRefStructure value) {
        this.containedInPlaceRef = value;
    }


    public SiteEntrances_RelStructure getEntrances() {
        return entrances;
    }

    public void setEntrances(SiteEntrances_RelStructure value) {
        this.entrances = value;
    }


    public PlaceEquipments_RelStructure getPlaceEquipments() {
        return placeEquipments;
    }

    public void setPlaceEquipments(PlaceEquipments_RelStructure value) {
        this.placeEquipments = value;
    }

    public LocalServices_RelStructure getLocalServices() {
        return localServices;
    }

    public void setLocalServices(LocalServices_RelStructure value) {
        this.localServices = value;
    }

    public List<Level> getLevels() {
        return levels;
    }

    public List<EquipmentPlace> getEquipmentPlaces() {
        return equipmentPlaces;
    }

    public void setEquipmentPlaces(List<EquipmentPlace> equipmentPlaces) {
        this.equipmentPlaces = equipmentPlaces;
    }
}
