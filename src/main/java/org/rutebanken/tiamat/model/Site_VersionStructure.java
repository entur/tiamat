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

    @Transient
    protected SiteRefs_RelStructure adjacentSites;

    @Transient
    protected SiteEntrances_RelStructure entrances;

    @OneToOne(cascade = CascadeType.ALL)
    protected PlaceEquipment placeEquipments;

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
}
