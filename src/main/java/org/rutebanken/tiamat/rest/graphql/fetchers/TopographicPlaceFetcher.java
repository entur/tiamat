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
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Service("topographicPlaceFetcher")
@Transactional
class TopographicPlaceFetcher implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceFetcher.class);

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;


    @Override
    public Object get(DataFetchingEnvironment environment) {
        String netexId = environment.getArgument(ID);
        boolean allVersions = environment.getArgument(ALL_VERSIONS);

        if (netexId != null) {

            logger.debug("Returning topographic place from netexId: {}", netexId);
            if (allVersions) {
                return topographicPlaceRepository.findByNetexId(netexId);
            } else {
                return Arrays.asList(topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(netexId));
            }
        }
        logger.debug("Returning topographic places with query: {} and type {}", environment.getArgument(QUERY), environment.getArgument(TOPOGRAPHIC_PLACE_TYPE));
        return topographicPlaceRepository.findByNameAndTypeMaxVersion(environment.getArgument(QUERY), environment.getArgument(TOPOGRAPHIC_PLACE_TYPE));

    }
}
