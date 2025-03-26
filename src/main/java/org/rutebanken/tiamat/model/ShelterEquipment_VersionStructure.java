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

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import org.rutebanken.tiamat.model.hsl.ElectricityTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.ShelterTypeEnumeration;
import org.rutebanken.tiamat.model.hsl.ShelterConditionEnumeration;

@MappedSuperclass
public class ShelterEquipment_VersionStructure
        extends WaitingEquipment_VersionStructure {

    protected Boolean enclosed;
    @Transient
    protected BigDecimal distanceFromNearestKerb;

    @Enumerated(EnumType.STRING)
    protected ShelterTypeEnumeration shelterType;

    @Enumerated(EnumType.STRING)
    protected ElectricityTypeEnumeration shelterElectricity;

    protected Boolean shelterLighting;

    @Enumerated(EnumType.STRING)
    protected ShelterConditionEnumeration shelterCondition;

    protected Integer timetableCabinets;

    protected Boolean trashCan;

    protected Boolean shelterHasDisplay;

    protected Boolean bicycleParking;

    protected Boolean leaningRail;

    protected Boolean outsideBench;

    protected Boolean shelterFasciaBoardTaping;

    protected Integer shelterNumber;

    protected String shelterExternalId;

    public Boolean isEnclosed() {
        return enclosed;
    }

    public void setEnclosed(Boolean value) {
        this.enclosed = value;
    }

    public BigDecimal getDistanceFromNearestKerb() {
        return distanceFromNearestKerb;
    }

    public void setDistanceFromNearestKerb(BigDecimal value) {
        this.distanceFromNearestKerb = value;
    }

    public ShelterTypeEnumeration getShelterType() {
        return shelterType;
    }

    public void setShelterType(ShelterTypeEnumeration shelterType) {
        this.shelterType = shelterType;
    }

    public ElectricityTypeEnumeration getShelterElectricity() {
        return shelterElectricity;
    }

    public void setShelterElectricity(ElectricityTypeEnumeration shelterElectricity) {
        this.shelterElectricity = shelterElectricity;
    }

    public Boolean isShelterLighting() {
        return shelterLighting;
    }

    public void setShelterLighting(Boolean shelterLighting) {
        this.shelterLighting = shelterLighting;
    }

    public ShelterConditionEnumeration getShelterCondition() {
        return shelterCondition;
    }

    public void setShelterCondition(ShelterConditionEnumeration shelterCondition) {
        this.shelterCondition = shelterCondition;
    }

    public Integer getTimetableCabinets() {
        return timetableCabinets;
    }

    public void setTimetableCabinets(Integer timetableCabinets) {
        this.timetableCabinets = timetableCabinets;
    }

    public Boolean isTrashCan() {
        return trashCan;
    }

    public void setTrashCan(Boolean trashCan) {
        this.trashCan = trashCan;
    }

    public Boolean isShelterHasDisplay() {
        return shelterHasDisplay;
    }

    public void setShelterHasDisplay(Boolean shelterHasDisplay) {
        this.shelterHasDisplay = shelterHasDisplay;
    }

    public Boolean isBicycleParking() {
        return bicycleParking;
    }

    public void setBicycleParking(Boolean bicycleParking) {
        this.bicycleParking = bicycleParking;
    }

    public Boolean isLeaningRail() {
        return leaningRail;
    }

    public void setLeaningRail(Boolean leaningRail) {
        this.leaningRail = leaningRail;
    }

    public Boolean isOutsideBench() {
        return outsideBench;
    }

    public void setOutsideBench(Boolean outsideBench) {
        this.outsideBench = outsideBench;
    }

    public Boolean isShelterFasciaBoardTaping() {
        return shelterFasciaBoardTaping;
    }

    public void setShelterFasciaBoardTaping(Boolean shelterFasciaBoardTaping) {
        this.shelterFasciaBoardTaping = shelterFasciaBoardTaping;
    }

    public Integer getShelterNumber() {
        return shelterNumber;
    }

    public void setShelterNumber(Integer shelterNumber) {
        this.shelterNumber = shelterNumber;
    }

    public String getShelterExternalId() {
        return shelterExternalId;
    }

    public void setShelterExternalId(String shelterExternalId) {
        this.shelterExternalId = shelterExternalId;
    }
}
