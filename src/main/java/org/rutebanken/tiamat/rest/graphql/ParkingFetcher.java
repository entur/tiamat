package org.rutebanken.tiamat.rest.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.repository.ParkingRepository;
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

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Service("parkingFetcher")
@Transactional(propagation = Propagation.REQUIRES_NEW)
class ParkingFetcher implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(ParkingFetcher.class);

    @Autowired
    private ParkingRepository parkingRepository;


    @Override
    public Object get(DataFetchingEnvironment environment) {

        PageRequest pageable = new PageRequest(environment.getArgument(PAGE), environment.getArgument(SIZE));

        Page<Parking> allParkings;
        String netexId = environment.getArgument(ID);
        Integer version = (Integer) environment.getArgument(VERSION);

        if (netexId != null) {

            List<Parking> parkingList = new ArrayList<>();
            if(version != null && version > 0) {
                parkingList = Arrays.asList(parkingRepository.findFirstByNetexIdAndVersion(netexId, version));
                allParkings = new PageImpl<>(parkingList, pageable, 1L);
            } else {
                parkingList.add(parkingRepository.findFirstByNetexIdOrderByVersionDesc(netexId));
                allParkings = new PageImpl<>(parkingList, pageable, 1L);
            }
        } else {
            allParkings = parkingRepository.findAll(pageable);
        }

        return allParkings;
    }
}
