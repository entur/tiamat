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
import org.rutebanken.tiamat.model.SiteFacilitySet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MOBILITY_FACILITY_LIST;

@Component
public class FacilitiesMapper {
    private SiteFacilitySet mapFacility(Map facilityMap) {
        List mobilityFacilityListInput = (List) facilityMap.get(MOBILITY_FACILITY_LIST);
        List<MobilityFacilityEnumeration> mobilityFacilityList = new ArrayList<>();
        for (Object mobilityFacilityObject : mobilityFacilityListInput) {
            MobilityFacilityEnumeration mobilityFacilityVal = (MobilityFacilityEnumeration) mobilityFacilityObject;
            mobilityFacilityList.add(mobilityFacilityVal);
        }

        if (!mobilityFacilityListInput.isEmpty()) {
            SiteFacilitySet facilitySet = new SiteFacilitySet();
            facilitySet.setMobilityFacilityList(mobilityFacilityList);
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
}
