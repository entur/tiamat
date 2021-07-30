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

package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.exporter.params.GroupOfStopPlacesSearch;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.repository.GroupOfStopPlacesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FIND_BY_STOP_PLACE_ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PAGE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.QUERY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SIZE;

@Service("groupOfStopPlacesFetcher")
@Transactional
public class GroupOfStopPlacesFetcher implements DataFetcher<Page<GroupOfStopPlaces>> {

    private static final Logger logger = LoggerFactory.getLogger(GroupOfStopPlacesFetcher.class);

    @Autowired
    private GroupOfStopPlacesRepository groupOfStopPlacesRepository;

    @Override
    @Transactional
    public Page<GroupOfStopPlaces> get(DataFetchingEnvironment environment) {

        GroupOfStopPlacesSearch groupOfStopPlacesSearch = GroupOfStopPlacesSearch.newGroupOfStopPlacesSearchBuilder()
                .stopPlaceId(environment.getArgument(FIND_BY_STOP_PLACE_ID))
                .idList(environment.getArgument(ID) != null ? Arrays.asList( (String) environment.getArgument(ID)) : null)
                .query(environment.getArgument(QUERY))
                .build();

        List<GroupOfStopPlaces> groupOfStopPlaces = groupOfStopPlacesRepository.findGroupOfStopPlaces(groupOfStopPlacesSearch);

        return new PageImpl<>(groupOfStopPlaces, PageRequest.of(environment.getArgument(PAGE), environment.getArgument(SIZE)), groupOfStopPlaces.size());
    }
}
