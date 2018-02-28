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

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class PassengerInformationEquipment_VersionStructure
        extends PassengerEquipment_VersionStructure {

    protected LogicalDisplayRefStructure logicalDisplayRef;
    protected StopPlaceReference stopPlaceRef;
    protected JAXBElement<? extends SiteComponentRefStructure> siteComponentRef;
    protected TypeOfPassengerInformationEquipmentRefStructure typeOfPassengerInformationEquipmentRef;
    protected List<PassengerInformationFacilityEnumeration> passengerInformationFacilityList;
    protected List<AccessibilityInfoFacilityEnumeration> accessibilityInfoFacilityList;

    public LogicalDisplayRefStructure getLogicalDisplayRef() {
        return logicalDisplayRef;
    }

    public void setLogicalDisplayRef(LogicalDisplayRefStructure value) {
        this.logicalDisplayRef = value;
    }

    public StopPlaceReference getStopPlaceRef() {
        return stopPlaceRef;
    }

    public void setStopPlaceRef(StopPlaceReference value) {
        this.stopPlaceRef = value;
    }

    public JAXBElement<? extends SiteComponentRefStructure> getSiteComponentRef() {
        return siteComponentRef;
    }

    public void setSiteComponentRef(JAXBElement<? extends SiteComponentRefStructure> value) {
        this.siteComponentRef = value;
    }

    public TypeOfPassengerInformationEquipmentRefStructure getTypeOfPassengerInformationEquipmentRef() {
        return typeOfPassengerInformationEquipmentRef;
    }

    public void setTypeOfPassengerInformationEquipmentRef(TypeOfPassengerInformationEquipmentRefStructure value) {
        this.typeOfPassengerInformationEquipmentRef = value;
    }

    public List<PassengerInformationFacilityEnumeration> getPassengerInformationFacilityList() {
        if (passengerInformationFacilityList == null) {
            passengerInformationFacilityList = new ArrayList<>();
        }
        return this.passengerInformationFacilityList;
    }

    public List<AccessibilityInfoFacilityEnumeration> getAccessibilityInfoFacilityList() {
        if (accessibilityInfoFacilityList == null) {
            accessibilityInfoFacilityList = new ArrayList<>();
        }
        return this.accessibilityInfoFacilityList;
    }

}
