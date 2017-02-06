package org.rutebanken.tiamat.model;

public class SitePathLink_ extends PathLink {

    protected SiteRefStructure siteRef;
    protected LevelRefStructure levelRef;
    protected ClassOfUseRef classOfUseRef;
    protected CheckConstraints_RelStructure checkConstraints;
    protected EquipmentPlaces_RelStructure equipmentPlaces;
    protected PlaceEquipments_RelStructure placeEquipments;
    protected LocalServices_RelStructure localServices;
    protected MultilingualStringEntity label;

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

    public CheckConstraints_RelStructure getCheckConstraints() {
        return checkConstraints;
    }

    public void setCheckConstraints(CheckConstraints_RelStructure value) {
        this.checkConstraints = value;
    }

    public EquipmentPlaces_RelStructure getEquipmentPlaces() {
        return equipmentPlaces;
    }

    public void setEquipmentPlaces(EquipmentPlaces_RelStructure value) {
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

    public MultilingualStringEntity getLabel() {
        return label;
    }

    public void setLabel(MultilingualStringEntity value) {
        this.label = value;
    }

}
