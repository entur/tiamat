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
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.rest.graphql.GraphQLNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FIND_BY_STOP_PLACE_ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PAGE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SIZE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VERSION;

@Service("parkingFetcher")
@Transactional
class ParkingFetcher implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(ParkingFetcher.class);

    @Autowired
    private ParkingRepository parkingRepository;


    @Override
    public Object get(DataFetchingEnvironment environment) {

        PageRequest pageable = PageRequest.of(environment.getArgument(PAGE), environment.getArgument(SIZE));

        Page<Parking> allParkings;

        String stopPlaceId = environment.getArgument(FIND_BY_STOP_PLACE_ID);

        String parkingId = environment.getArgument(GraphQLNames.ID);
        Integer version = (Integer) environment.getArgument(VERSION);

        if (parkingId != null) {
            List<Parking> parkingList = new ArrayList<>();
            if(version != null && version > 0) {
                logger.info("Finding parking by netexid {} and version {}", parkingId, version);
                parkingList = Arrays.asList(parkingRepository.findFirstByNetexIdAndVersion(parkingId, version));
                allParkings = new PageImpl<>(parkingList, pageable, 1L);
            } else {
                logger.info("Finding first parking by netexid {} and highest version", parkingId);
                parkingList.add(parkingRepository.findFirstByNetexIdOrderByVersionDesc(parkingId));
                allParkings = new PageImpl<>(parkingList, pageable, 1L);
            }
        } else if (stopPlaceId != null) {
            logger.info("Finding parkings by stop place netexid {}", stopPlaceId);
            return parkingRepository.findByStopPlaceNetexId(stopPlaceId).stream()
                    .peek(parkingNetexId -> logger.info("Finding parking by netexid {} and highest version", parkingNetexId))
                    .map(netexId -> parkingRepository.findFirstByNetexIdOrderByVersionDesc(netexId))
                    .toList();
        } else {
            logger.info("Finding all parkings regardless of version and validity");
            allParkings = parkingRepository.findAll(pageable);
        }

        return allParkings;
    }
}
