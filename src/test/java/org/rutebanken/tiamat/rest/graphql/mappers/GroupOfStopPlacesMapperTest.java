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

import org.junit.Test;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.StopPlaceReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ENTITY_REF_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.GROUP_OF_STOP_PLACES_MEMBERS;

public class GroupOfStopPlacesMapperTest {

    private GroupOfEntitiesMapper groupOfEntitiesMapper = mock(GroupOfEntitiesMapper.class);

    private GroupOfStopPlacesMapper groupOfStopPlacesMapper = new GroupOfStopPlacesMapper(groupOfEntitiesMapper);

    @Test
    public void populate() throws Exception {

        Map<String, String> memberRefMap = new HashMap<>();
        String stopPlaceRef = "NSR:StopPlace:1";
        memberRefMap.put(ENTITY_REF_REF, stopPlaceRef);

        List<Map<String, String>> membersList = new ArrayList<>();
        membersList.add(memberRefMap);

        Map<String, List<Map<String, String>>> input = new HashMap<>();
        input.put(GROUP_OF_STOP_PLACES_MEMBERS, membersList);

        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces();

        groupOfStopPlacesMapper.populate(input, groupOfStopPlaces);;

        assertThat(groupOfStopPlaces.getMembers())
                .isNotEmpty()
                .extracting(StopPlaceReference::getRef)
                    .contains(stopPlaceRef);

    }

}