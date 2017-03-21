package org.rutebanken.tiamat.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public abstract class Site_VersionStructure
        extends SiteElement {

    @OneToMany(cascade = CascadeType.ALL)
    @Transient
    private final List<Level> levels = new ArrayList<>();

    @org.hibernate.annotations.Cache(
            usage = CacheConcurrencyStrategy.READ_WRITE
    )
    @OneToOne(fetch = FetchType.LAZY)
    protected TopographicPlace topographicPlace;

    @Transient
    protected TopographicPlaceRefStructure topographicPlaceRef;

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

    public TopographicPlace getTopographicPlace() {
        return topographicPlace;
    }

    public void setTopographicPlace(TopographicPlace topographicPlace) {
        this.topographicPlace = topographicPlace;
    }

    /**
     * Should be removed.
     */
    public TopographicPlaceRefStructure getTopographicPlaceRef() {
        return topographicPlaceRef;
    }

    /**
     * NOT for persistance. Should be removed. See TopographicPlace instead.
     */
    public void setTopographicPlaceRef(TopographicPlaceRefStructure value) {
        this.topographicPlaceRef = value;
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
