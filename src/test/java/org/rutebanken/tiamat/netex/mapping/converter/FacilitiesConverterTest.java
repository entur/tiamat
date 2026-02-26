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
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.rutebanken.netex.model.MobilityFacilityEnumeration;
import org.rutebanken.netex.model.PassengerInformationEquipmentEnumeration;
import org.rutebanken.netex.model.PassengerInformationFacilityEnumeration;
import org.rutebanken.netex.model.SiteFacilitySets_RelStructure;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.SiteFacilitySet;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FacilitiesConverterTest extends TiamatIntegrationTest {
    private final Type<Set<SiteFacilitySet>> facilitiesType = new TypeBuilder<Set<SiteFacilitySet>>() {}.build();
    private final Type<SiteFacilitySets_RelStructure> siteFacilitySetsRelStructureType = new TypeBuilder<SiteFacilitySets_RelStructure>() {}.build();
    private final MappingContext mappingContext = new MappingContext(new HashMap<>());

    @Autowired
    private FacilitiesConverter facilitiesConverter;

    @Test
    public void convertToNetexStructure() {
        SiteFacilitySet facility1 = new SiteFacilitySet();
        facility1.setMobilityFacilityList(List.of(org.rutebanken.tiamat.model.MobilityFacilityEnumeration.TACTILE_GUIDING_STRIPS));
        SiteFacilitySet facility2 = new SiteFacilitySet();
        Set<SiteFacilitySet> facilities = Set.of(facility1, facility2);

        SiteFacilitySets_RelStructure relStructure = facilitiesConverter.convertTo(facilities, siteFacilitySetsRelStructureType, mappingContext);
        assertThat(relStructure).isNotNull();
        // Only 1 because facility2 doesn't have a ny data in it
        Assertions.assertThat(relStructure.getSiteFacilitySetRefOrSiteFacilitySet().size()).isEqualTo(1);
    }

    @Test
    public void convertFromNetexStructure() {
        SiteFacilitySets_RelStructure relStructure = new SiteFacilitySets_RelStructure();
        org.rutebanken.netex.model.SiteFacilitySet netexFacility1 = new org.rutebanken.netex.model.SiteFacilitySet();
        netexFacility1.withMobilityFacilityList(MobilityFacilityEnumeration.TACTILE_GUIDING_STRIPS, MobilityFacilityEnumeration.TACTILE_PLATFORM_EDGES);
        netexFacility1.withPassengerInformationFacilityList(PassengerInformationFacilityEnumeration.PASSENGER_INFORMATION_DISPLAY);
        netexFacility1.withPassengerInformationEquipmentList(PassengerInformationEquipmentEnumeration.INFORMATION_DESK);
        org.rutebanken.netex.model.SiteFacilitySet netexFacility2 = new org.rutebanken.netex.model.SiteFacilitySet();
        netexFacility2.withMobilityFacilityList(MobilityFacilityEnumeration.UNKNOWN);
        netexFacility2.withPassengerInformationEquipmentList(PassengerInformationEquipmentEnumeration.INFORMATION_DESK);
        relStructure.withSiteFacilitySetRefOrSiteFacilitySet(Arrays.asList(netexFacility1, netexFacility2).toArray());

        Set<SiteFacilitySet> facilities = facilitiesConverter.convertFrom(relStructure, facilitiesType, mappingContext);
        assertThat(facilities).isNotNull();
        assertThat(facilities.size()).isEqualTo(2);

        boolean hasFacilityWithTactiles = facilities.stream()
                .anyMatch(facility -> facility.getMobilityFacilityList().contains(org.rutebanken.tiamat.model.MobilityFacilityEnumeration.TACTILE_GUIDING_STRIPS) && facility.getMobilityFacilityList().contains(org.rutebanken.tiamat.model.MobilityFacilityEnumeration.TACTILE_PLATFORM_EDGES));
        assertThat(hasFacilityWithTactiles).isTrue();

        boolean hasFacilityWithPassengerDisplay= facilities.stream()
                .anyMatch(facility -> facility.getPassengerInformationFacilityList().contains(org.rutebanken.tiamat.model.PassengerInformationFacilityEnumeration.PASSENGER_INFORMATION_DISPLAY));
        assertThat(hasFacilityWithPassengerDisplay).isTrue();

        boolean hasFacilityWithInformationDesk= facilities.stream()
                .anyMatch(facility -> facility.getPassengerInformationEquipmentList().contains(org.rutebanken.tiamat.model.PassengerInformationEquipmentEnumeration.INFORMATION_DESK));
        assertThat(hasFacilityWithInformationDesk).isTrue();

        boolean hasFacilityWithUnknown = facilities.stream()
                .anyMatch(facility -> facility.getMobilityFacilityList().contains(org.rutebanken.tiamat.model.MobilityFacilityEnumeration.UNKNOWN));
        assertThat(hasFacilityWithUnknown).isTrue();
    }
}

