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

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.FetchType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@MappedSuperclass
public abstract class Site_VersionStructure
        extends SiteElement {

    @OneToMany(cascade = CascadeType.ALL)
    @Transient
    private final List<Level> levels = new ArrayList<>();

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    protected TopographicPlace topographicPlace;

    @Transient
    protected SiteTypeEnumeration siteType;

    @Transient
    protected Boolean atCentre;

    @Transient
    protected LocaleStructure locale;

    @Transient
    protected OrganisationRefStructure organisationRef;

    @AttributeOverrides({
            @AttributeOverride(name = "ref", column = @Column(name = "parent_site_ref")),
            @AttributeOverride(name = "version", column = @Column(name = "parent_site_ref_version"))
    })
    @Embedded
    protected SiteRefStructure parentSiteRef;

    @ElementCollection(targetClass = SiteRefStructure.class, fetch = FetchType.EAGER)
    protected Set<SiteRefStructure> adjacentSites = new HashSet<>();

    @Transient
    protected SiteEntrances_RelStructure entrances;

    @OneToOne(cascade = CascadeType.ALL)
    protected PlaceEquipment placeEquipments;

    @OneToMany(cascade = CascadeType.ALL)
    private List<EquipmentPlace> equipmentPlaces;

    @OneToMany(cascade = CascadeType.ALL)
    private List<LocalService> localServices;

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

    public OrganisationRefStructure getOrganisationRef() {
        return organisationRef;
    }

    public void setOrganisationRef(OrganisationRefStructure organisationRef) {
        this.organisationRef = organisationRef;
    }


    public SiteRefStructure getParentSiteRef() {
        return parentSiteRef;
    }

    public void setParentSiteRef(SiteRefStructure value) {
        this.parentSiteRef = value;
    }

    public Set<SiteRefStructure> getAdjacentSites() {
        return adjacentSites;
    }

    public SiteEntrances_RelStructure getEntrances() {
        return entrances;
    }

    public void setEntrances(SiteEntrances_RelStructure value) {
        this.entrances = value;
    }


    public PlaceEquipment getPlaceEquipments() {
        return placeEquipments;
    }

    public void setPlaceEquipments(PlaceEquipment value) {
        this.placeEquipments = value;
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

    public List<LocalService> getLocalServices() {
        return localServices;
    }

    public void setLocalServices(List<LocalService> localServices) {
        this.localServices = localServices;
    }
}
