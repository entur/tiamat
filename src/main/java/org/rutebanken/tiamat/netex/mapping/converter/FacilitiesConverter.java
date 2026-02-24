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

package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.SiteFacilitySets_RelStructure;
import org.rutebanken.tiamat.model.SiteFacilitySet;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FacilitiesConverter extends BidirectionalConverter<Set<SiteFacilitySet>, org.rutebanken.netex.model.SiteFacilitySets_RelStructure> {

    @Override
    public SiteFacilitySets_RelStructure convertTo(Set<SiteFacilitySet> facilities, Type<SiteFacilitySets_RelStructure> type, MappingContext mappingContext) {
        if (facilities != null && !facilities.isEmpty()) {
            final SiteFacilitySets_RelStructure siteFacilitySets_relStructure = new SiteFacilitySets_RelStructure();
            Set<SiteFacilitySet> facilitiesContainingData = facilities.stream().filter(this::isTiamatFacilityContainingData).collect(Collectors.toSet());
            List<org.rutebanken.netex.model.SiteFacilitySet> facilitiesList = facilitiesContainingData.stream().map(facility -> {
                org.rutebanken.netex.model.SiteFacilitySet netexFacility = new org.rutebanken.netex.model.SiteFacilitySet();
                mapperFacade.map(facility, netexFacility);
                return netexFacility;
            }).toList();

            siteFacilitySets_relStructure.withSiteFacilitySetRefOrSiteFacilitySet(facilitiesList.toArray());
            return siteFacilitySets_relStructure;
        }
        return null;
    }

    @Override
    public Set<SiteFacilitySet> convertFrom(SiteFacilitySets_RelStructure siteFacilitySets_relStructure, Type<Set<SiteFacilitySet>> type, MappingContext mappingContext) {
        if (siteFacilitySets_relStructure == null) {
            return null;
        }
        Set<SiteFacilitySet> tiamatFacilities = new HashSet<>();
        for (Object netexFacility : siteFacilitySets_relStructure.getSiteFacilitySetRefOrSiteFacilitySet()) {
            if (netexFacility instanceof org.rutebanken.netex.model.SiteFacilitySet facility && isNetexFacilityContainingData(facility)) {
                final SiteFacilitySet tiamatFacility = new SiteFacilitySet();
                mapperFacade.map(facility, tiamatFacility);
                tiamatFacilities.add(tiamatFacility);
            }
        }
        return tiamatFacilities;
    }

    private boolean isTiamatFacilityContainingData(SiteFacilitySet facility) {
        boolean containsNonEmptyMobilityFacilityList = facility.getMobilityFacilityList() != null && !facility.getMobilityFacilityList().isEmpty();
        boolean containsNonEmptyPassengerInformationFacilityList = facility.getPassengerInformationFacilityList() != null && !facility.getPassengerInformationFacilityList().isEmpty();
        boolean containsNonEmptyPassengerInformationEquipmentList = facility.getPassengerInformationEquipmentList() != null && !facility.getPassengerInformationEquipmentList().isEmpty();
        return containsNonEmptyMobilityFacilityList || containsNonEmptyPassengerInformationFacilityList || containsNonEmptyPassengerInformationEquipmentList;
    }

    private boolean isNetexFacilityContainingData(org.rutebanken.netex.model.SiteFacilitySet facility) {
        boolean containsNonEmptyMobilityFacilityList = facility.getMobilityFacilityList() != null && !facility.getMobilityFacilityList().isEmpty();
        boolean containsNonEmptyPassengerInformationFacilityList = facility.getPassengerInformationFacilityList() != null && !facility.getPassengerInformationFacilityList().isEmpty();
        boolean containsNonEmptyPassengerInformationEquipmentList = facility.getPassengerInformationEquipmentList() != null && !facility.getPassengerInformationEquipmentList().isEmpty();
        return containsNonEmptyMobilityFacilityList || containsNonEmptyPassengerInformationFacilityList || containsNonEmptyPassengerInformationEquipmentList;
    }
}
