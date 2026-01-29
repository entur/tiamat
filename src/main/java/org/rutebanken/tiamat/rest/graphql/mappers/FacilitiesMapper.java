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

package org.rutebanken.tiamat.rest.graphql.mappers;

import org.rutebanken.tiamat.model.MobilityFacilityEnumeration;
import org.rutebanken.tiamat.model.PassengerInformationEquipmentEnumeration;
import org.rutebanken.tiamat.model.PassengerInformationFacilityEnumeration;
import org.rutebanken.tiamat.model.SiteFacilitySet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MOBILITY_FACILITY_LIST;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PASSENGER_INFORMATION_EQUIPMENT_LIST;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PASSENGER_INFORMATION_FACILITY_LIST;

@Component
public class FacilitiesMapper {
    private SiteFacilitySet mapFacility(Map facilityMap) {
        List mobilityFacilityListInput = (List) facilityMap.get(MOBILITY_FACILITY_LIST);
        List<MobilityFacilityEnumeration> mobilityFacilityList = getMobilityFacilityList(mobilityFacilityListInput);

        List passengerInformationFacilityListInput = (List) facilityMap.get(PASSENGER_INFORMATION_FACILITY_LIST);
        List<PassengerInformationFacilityEnumeration> passengerInformationFacilityList = getPassengerInformationFacilityEnumerationList(passengerInformationFacilityListInput);

        List passengerInformationEquipmentInput = (List) facilityMap.get(PASSENGER_INFORMATION_EQUIPMENT_LIST);
        List<PassengerInformationEquipmentEnumeration> passengerEquipmentFacilityList  = getPassengerInformationEquipmentEnumerationList(passengerInformationEquipmentInput);

        if (!mobilityFacilityList.isEmpty() || !passengerInformationFacilityList.isEmpty() || !passengerEquipmentFacilityList.isEmpty()) {
            SiteFacilitySet facilitySet = new SiteFacilitySet();
            facilitySet.setMobilityFacilityList(mobilityFacilityList);
            facilitySet.setPassengerInformationFacilityList(passengerInformationFacilityList);
            facilitySet.setPassengerInformationEquipmentList(passengerEquipmentFacilityList);
            return facilitySet;
        }
        return null;
    }

    public Set<SiteFacilitySet> mapFacilities(List facilitiesListObject) {
        if (facilitiesListObject != null) {
            Set<SiteFacilitySet> tiamatFacilities = new HashSet<>();
            for (Object facilityObject : facilitiesListObject) {
                SiteFacilitySet tiamatFacility = mapFacility((Map) facilityObject);
                if (tiamatFacility != null) {
                    tiamatFacilities.add(tiamatFacility);
                }
            }
            if (tiamatFacilities.isEmpty()) {
                // Skip setting empty facilities
                return null;
            }
            return tiamatFacilities;
        }
        return null;
    }

    private List<MobilityFacilityEnumeration> getMobilityFacilityList(List mobilityFacilityListInput) {
        List<MobilityFacilityEnumeration> mobilityFacilityList = new ArrayList<>();
        if (mobilityFacilityListInput != null) {
            for (Object mobilityFacilityObject : mobilityFacilityListInput) {
                MobilityFacilityEnumeration mobilityFacilityVal = (MobilityFacilityEnumeration) mobilityFacilityObject;
                mobilityFacilityList.add(mobilityFacilityVal);
            }
        }

        return mobilityFacilityList;
    }

    private List<PassengerInformationFacilityEnumeration> getPassengerInformationFacilityEnumerationList(List passengerInformationFacilityListInput) {
        List<PassengerInformationFacilityEnumeration> passengerInformationFacilityList  = new ArrayList<>();
        if (passengerInformationFacilityListInput != null) {
            for (Object passengerInformationFacilityObject : passengerInformationFacilityListInput) {
                PassengerInformationFacilityEnumeration passengerInformationFacilityVal = (PassengerInformationFacilityEnumeration) passengerInformationFacilityObject;
                passengerInformationFacilityList.add(passengerInformationFacilityVal);
            }
        }
        return passengerInformationFacilityList;
    }

    private List<PassengerInformationEquipmentEnumeration> getPassengerInformationEquipmentEnumerationList(List passengerInformationEquipmentInput) {
        List<PassengerInformationEquipmentEnumeration> passengerEquipmentFacilityList  = new ArrayList<>();
        if (passengerInformationEquipmentInput != null) {
            for (Object passengerEquipmentFacilityObject : passengerInformationEquipmentInput) {
                PassengerInformationEquipmentEnumeration passengerEquipmentFacilityVal = (PassengerInformationEquipmentEnumeration) passengerEquipmentFacilityObject;
                passengerEquipmentFacilityList.add(passengerEquipmentFacilityVal);
            }
        }
        return passengerEquipmentFacilityList;
    }
}
