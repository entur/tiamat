package org.rutebanken.tiamat.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;


@MappedSuperclass
public abstract class SiteComponent_VersionStructure
        extends SiteElement_VersionStructure {

    @Embedded
    protected SiteRefStructure siteRef;

    @Embedded
    protected LevelRefStructure levelRef;

    @XmlElement(name = "ClassOfUseRef")
    @Transient
    protected ClassOfUseRef classOfUseRef;

    @OneToMany(cascade = CascadeType.ALL)
    private List<CheckConstraint> checkConstraints;

    @OneToMany(cascade = CascadeType.ALL)
    protected List<EquipmentPlace> equipmentPlaces;

    @Transient
    protected PlaceEquipments_RelStructure placeEquipments;

    @Transient
    protected LocalServices_RelStructure localServices;

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
        this.classOfUseRef = value;
    }


    public List<EquipmentPlace> getEquipmentPlaces() {
        return equipmentPlaces;
    }


    public void setEquipmentPlaces(List<EquipmentPlace> value) {
        this.equipmentPlaces = value;
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

    public List<CheckConstraint> getCheckConstraints() {
        return checkConstraints;
    }

    public void setCheckConstraints(List<CheckConstraint> checkConstraints) {
        this.checkConstraints = checkConstraints;
    }
}
