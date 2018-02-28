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

import java.util.ArrayList;
import java.util.List;


public class FacilitySet_VersionStructure
        extends DataManagedObjectStructure {

    protected OrganisationRefStructure providedByRef;
    protected MultilingualStringEntity description;
    protected TypeOfFacilityRefStructure typeOfFacilityRef;
    protected TypesOfEquipment_RelStructure otherFacilities;
    protected List<AccessibilityInfoFacilityEnumeration> accessibilityInfoFacilityList;
    protected List<AssistanceFacilityEnumeration> assistanceFacilityList;
    protected List<AccessibilityToolEnumeration> accessibilityToolList;
    protected List<CarServiceFacilityEnumeration> carServiceFacilityList;
    protected List<CateringFacilityEnumeration> cateringFacilityList;
    protected List<FamilyFacilityEnumeration> familyFacilityList;

    protected GenderLimitationEnumeration genderLimitation;

    protected List<MealFacilityEnumeration> mealFacilityList;
    protected List<MedicalFacilityEnumeration> medicalFacilityList;
    protected List<MobilityFacilityEnumeration> mobilityFacilityList;
    protected List<NuisanceFacilityEnumeration> nuisanceFacilityList;
    protected List<PassengerCommsFacilityEnumeration> passengerCommsFacilityList;
    protected PassengerInformationEquipmentEnumeration passengerInformationEquipmentList;
    protected List<PassengerInformationFacilityEnumeration> passengerInformationFacilityList;
    protected List<RetailFacilityEnumeration> retailFacilityList;
    protected List<SafetyFacilityEnumeration> safetyFacilityList;
    protected List<SanitaryFacilityEnumeration> sanitaryFacilityList;
    protected List<TicketingFacilityEnumeration> ticketingFacilityList;
    protected List<TicketingServiceFacilityEnumeration> ticketingServiceFacilityList;

    public OrganisationRefStructure getProvidedByRef() {
        return providedByRef;
    }

    public void setProvidedByRef(OrganisationRefStructure value) {
        this.providedByRef = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public TypeOfFacilityRefStructure getTypeOfFacilityRef() {
        return typeOfFacilityRef;
    }

    public void setTypeOfFacilityRef(TypeOfFacilityRefStructure value) {
        this.typeOfFacilityRef = value;
    }

    public TypesOfEquipment_RelStructure getOtherFacilities() {
        return otherFacilities;
    }

    public void setOtherFacilities(TypesOfEquipment_RelStructure value) {
        this.otherFacilities = value;
    }

    public List<AccessibilityInfoFacilityEnumeration> getAccessibilityInfoFacilityList() {
        if (accessibilityInfoFacilityList == null) {
            accessibilityInfoFacilityList = new ArrayList<>();
        }
        return this.accessibilityInfoFacilityList;
    }

    public List<AssistanceFacilityEnumeration> getAssistanceFacilityList() {
        if (assistanceFacilityList == null) {
            assistanceFacilityList = new ArrayList<>();
        }
        return this.assistanceFacilityList;
    }

    public List<AccessibilityToolEnumeration> getAccessibilityToolList() {
        if (accessibilityToolList == null) {
            accessibilityToolList = new ArrayList<>();
        }
        return this.accessibilityToolList;
    }

    public List<CarServiceFacilityEnumeration> getCarServiceFacilityList() {
        if (carServiceFacilityList == null) {
            carServiceFacilityList = new ArrayList<>();
        }
        return this.carServiceFacilityList;
    }

    public List<CateringFacilityEnumeration> getCateringFacilityList() {
        if (cateringFacilityList == null) {
            cateringFacilityList = new ArrayList<>();
        }
        return this.cateringFacilityList;
    }

    public List<FamilyFacilityEnumeration> getFamilyFacilityList() {
        if (familyFacilityList == null) {
            familyFacilityList = new ArrayList<>();
        }
        return this.familyFacilityList;
    }

    public GenderLimitationEnumeration getGenderLimitation() {
        return genderLimitation;
    }

    public void setGenderLimitation(GenderLimitationEnumeration value) {
        this.genderLimitation = value;
    }

    public List<MealFacilityEnumeration> getMealFacilityList() {
        if (mealFacilityList == null) {
            mealFacilityList = new ArrayList<>();
        }
        return this.mealFacilityList;
    }

    public List<MedicalFacilityEnumeration> getMedicalFacilityList() {
        if (medicalFacilityList == null) {
            medicalFacilityList = new ArrayList<>();
        }
        return this.medicalFacilityList;
    }

    public List<MobilityFacilityEnumeration> getMobilityFacilityList() {
        if (mobilityFacilityList == null) {
            mobilityFacilityList = new ArrayList<>();
        }
        return this.mobilityFacilityList;
    }

    public List<NuisanceFacilityEnumeration> getNuisanceFacilityList() {
        if (nuisanceFacilityList == null) {
            nuisanceFacilityList = new ArrayList<>();
        }
        return this.nuisanceFacilityList;
    }

    public List<PassengerCommsFacilityEnumeration> getPassengerCommsFacilityList() {
        if (passengerCommsFacilityList == null) {
            passengerCommsFacilityList = new ArrayList<>();
        }
        return this.passengerCommsFacilityList;
    }

    public PassengerInformationEquipmentEnumeration getPassengerInformationEquipmentList() {
        return passengerInformationEquipmentList;
    }

    public void setPassengerInformationEquipmentList(PassengerInformationEquipmentEnumeration value) {
        this.passengerInformationEquipmentList = value;
    }

    public List<PassengerInformationFacilityEnumeration> getPassengerInformationFacilityList() {
        if (passengerInformationFacilityList == null) {
            passengerInformationFacilityList = new ArrayList<>();
        }
        return this.passengerInformationFacilityList;
    }

    public List<RetailFacilityEnumeration> getRetailFacilityList() {
        if (retailFacilityList == null) {
            retailFacilityList = new ArrayList<>();
        }
        return this.retailFacilityList;
    }

    public List<SafetyFacilityEnumeration> getSafetyFacilityList() {
        if (safetyFacilityList == null) {
            safetyFacilityList = new ArrayList<>();
        }
        return this.safetyFacilityList;
    }

    public List<SanitaryFacilityEnumeration> getSanitaryFacilityList() {
        if (sanitaryFacilityList == null) {
            sanitaryFacilityList = new ArrayList<>();
        }
        return this.sanitaryFacilityList;
    }

    public List<TicketingFacilityEnumeration> getTicketingFacilityList() {
        if (ticketingFacilityList == null) {
            ticketingFacilityList = new ArrayList<>();
        }
        return this.ticketingFacilityList;
    }

    public List<TicketingServiceFacilityEnumeration> getTicketingServiceFacilityList() {
        if (ticketingServiceFacilityList == null) {
            ticketingServiceFacilityList = new ArrayList<>();
        }
        return this.ticketingServiceFacilityList;
    }

}
