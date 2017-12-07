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

import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.StopPlaceReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Component
public class GroupOfStopPlacesMapper {

    private final GroupOfEntitiesMapper groupOfEntitiesMapper;

    @Autowired
    public GroupOfStopPlacesMapper(GroupOfEntitiesMapper groupOfEntitiesMapper) {
        this.groupOfEntitiesMapper = groupOfEntitiesMapper;
    }

    public boolean populate(Map input, GroupOfStopPlaces entity) {

        boolean isUpdated = groupOfEntitiesMapper.populate(input, entity);

        if(input.get(GROUP_OF_STOP_PLACES_MEMBERS) != null) {
            List membersList = (List) input.get(GROUP_OF_STOP_PLACES_MEMBERS);

            for(Object memberObject : membersList) {
                Map memberMap = (Map) memberObject;
                String ref = (String) memberMap.get(ENTITY_REF_REF);
                entity.getMembers().add(new StopPlaceReference(ref));
                isUpdated = true;
            }
        }

        return isUpdated;
    }
}
