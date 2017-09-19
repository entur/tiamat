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

import java.math.BigDecimal;


public class TrainElement_VersionStructure
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity description;
    protected TrainElementTypeEnumeration trainElementType;

    protected PassengerCapacityStructure passengerCapacity;
    protected PassengerCapacities_RelStructure capacities;

    protected BigDecimal length;
    protected ServiceFacilitySets_RelStructure facilities;
    protected Equipments_RelStructure equipments;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public TrainElementTypeEnumeration getTrainElementType() {
        return trainElementType;
    }

    public void setTrainElementType(TrainElementTypeEnumeration value) {
        this.trainElementType = value;
    }

    public PassengerCapacityStructure getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setPassengerCapacity(PassengerCapacityStructure value) {
        this.passengerCapacity = value;
    }

    public PassengerCapacities_RelStructure getCapacities() {
        return capacities;
    }

    public void setCapacities(PassengerCapacities_RelStructure value) {
        this.capacities = value;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal value) {
        this.length = value;
    }

    public ServiceFacilitySets_RelStructure getFacilities() {
        return facilities;
    }

    public void setFacilities(ServiceFacilitySets_RelStructure value) {
        this.facilities = value;
    }

    public Equipments_RelStructure getEquipments() {
        return equipments;
    }

    public void setEquipments(Equipments_RelStructure value) {
        this.equipments = value;
    }

}
