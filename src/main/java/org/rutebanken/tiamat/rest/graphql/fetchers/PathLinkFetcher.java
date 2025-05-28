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
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.rest.graphql.mappers.IdMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ALL_VERSIONS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FIND_BY_STOP_PLACE_ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;

@Service("pathLinkFetcher")
@Transactional
class PathLinkFetcher implements DataFetcher {

    @Autowired
    private PathLinkRepository pathLinkRepository;

    @Autowired
    private IdMapper idMapper;

    @Override
    public Object get(DataFetchingEnvironment environment) {

        Optional<String> pathLinkNetexId = idMapper.extractIdIfPresent(ID, environment.getArguments());

        boolean allVersions = environment.getArgument(ALL_VERSIONS) != null ? environment.getArgument(ALL_VERSIONS) : false;

        if (pathLinkNetexId.isPresent()) {
            if(allVersions) {
                return pathLinkRepository.findByNetexId(pathLinkNetexId.get());
            }
            return Arrays.asList(pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(pathLinkNetexId.get()));
        }

        Optional<String> stopPlaceNetexId = idMapper.extractIdIfPresent(FIND_BY_STOP_PLACE_ID, environment.getArguments());
        if (stopPlaceNetexId.isPresent()) {
            // Find pathlinks referencing to stops. Or path links referencing to quays that belong to stop.

            return pathLinkRepository.findByStopPlaceNetexId(stopPlaceNetexId.get())
                    .stream()
                    .map(netexId -> {
                        if(allVersions) {
                            return pathLinkRepository.findByNetexId(netexId);
                        } else {
                            return pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(netexId);
                        }
                    })
                    .toList();
        }

        return pathLinkRepository.findAll();

    }

}
