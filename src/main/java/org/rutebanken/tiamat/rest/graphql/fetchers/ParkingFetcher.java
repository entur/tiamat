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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Service("parkingFetcher")
@Transactional
class ParkingFetcher implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(ParkingFetcher.class);

    @Autowired
    private ParkingRepository parkingRepository;


    @Override
    public Object get(DataFetchingEnvironment environment) {

        PageRequest pageable = new PageRequest(environment.getArgument(PAGE), environment.getArgument(SIZE));

        Page<Parking> allParkings;

        String stopPlaceId = environment.getArgument(FIND_BY_STOP_PLACE_ID);

        String id = environment.getArgument(GraphQLNames.ID);
        Integer version = (Integer) environment.getArgument(VERSION);

        if (id != null) {
            List<Parking> parkingList = new ArrayList<>();
            if(version != null && version > 0) {
                parkingList = Arrays.asList(parkingRepository.findFirstByNetexIdAndVersion(id, version));
                allParkings = new PageImpl<>(parkingList, pageable, 1L);
            } else {
                parkingList.add(parkingRepository.findFirstByNetexIdOrderByVersionDesc(id));
                allParkings = new PageImpl<>(parkingList, pageable, 1L);
            }
        } else if (stopPlaceId != null) {
            return parkingRepository.findByStopPlaceNetexId(stopPlaceId).stream()
                    .map(netexId -> parkingRepository.findFirstByNetexIdOrderByVersionDesc(netexId))
                    .collect(Collectors.toList());
        } else {
            allParkings = parkingRepository.findAll(pageable);
        }

        return allParkings;
    }
}
