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
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.List;


@MappedSuperclass
public abstract class SiteComponent_VersionStructure
        extends SiteElement {

    @AttributeOverrides({
            @AttributeOverride(name = "ref", column = @Column(name = "site_ref")),
            @AttributeOverride(name = "version", column = @Column(name = "site_ref_version"))
    })
    @Embedded
    protected SiteRefStructure siteRef;

    @AttributeOverrides({
            @AttributeOverride(name = "ref", column = @Column(name = "level_ref")),
            @AttributeOverride(name = "version", column = @Column(name = "level_ref_version"))
    })
    @Embedded
    protected LevelRefStructure levelRef;

    @Transient
    protected ClassOfUseRef classOfUseRef;
    @OneToMany(cascade = CascadeType.ALL)
    protected List<EquipmentPlace> equipmentPlaces;
    @OneToOne(cascade = CascadeType.ALL)
    protected PlaceEquipment placeEquipments;
    @OneToMany(cascade = CascadeType.ALL)
    private List<LocalService> localServices = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<CheckConstraint> checkConstraints;

    public SiteComponent_VersionStructure(EmbeddableMultilingualString name) {
        super(name);
    }

    protected SiteComponent_VersionStructure() {
    }


    public SiteRefStructure getSiteRef() {
        return siteRef;
    }


    public void setSiteRef(SiteRefStructure value) {
        this.siteRef = value;
    }


    public LevelRefStructure getLevelRef() {
        return levelRef;
    }


    public void setLevelRef(LevelRefStructure value) {
        this.levelRef = value;
    }


    public ClassOfUseRef getClassOfUseRef() {
        return classOfUseRef;
    }


    public void setClassOfUseRef(ClassOfUseRef value) {
    }


    public List<EquipmentPlace> getEquipmentPlaces() {
        return equipmentPlaces;
    }


    public void setEquipmentPlaces(List<EquipmentPlace> value) {
        this.equipmentPlaces = value;
    }


    public PlaceEquipment getPlaceEquipments() {
        return placeEquipments;
    }


    public void setPlaceEquipments(PlaceEquipment value) {
        this.placeEquipments = value;
    }

    public List<CheckConstraint> getCheckConstraints() {
        return checkConstraints;
    }

    public void setCheckConstraints(List<CheckConstraint> checkConstraints) {
        this.checkConstraints = checkConstraints;
    }

    public List<LocalService> getLocalServices() {
        return localServices;
    }

    public void setLocalServices(List<LocalService> localServices) {
        this.localServices = localServices;
    }
}
