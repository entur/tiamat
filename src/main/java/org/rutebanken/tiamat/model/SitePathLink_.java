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

public class SitePathLink_ extends PathLink {

    protected SiteRefStructure siteRef;
    protected LevelRefStructure levelRef;
    protected ClassOfUseRef classOfUseRef;
    protected CheckConstraints_RelStructure checkConstraints;
    protected EquipmentPlaces_RelStructure equipmentPlaces;
    protected PlaceEquipment placeEquipments;
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

    public PlaceEquipment getPlaceEquipments() {
        return placeEquipments;
    }

    public void setPlaceEquipments(PlaceEquipment value) {
        this.placeEquipments = value;
    }

    public MultilingualStringEntity getLabel() {
        return label;
    }

    public void setLabel(MultilingualStringEntity value) {
        this.label = value;
    }

}
