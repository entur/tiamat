package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StopPlaceChildFetcher implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceChildFetcher.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
        StopPlace parent = (StopPlace) dataFetchingEnvironment.getSource();
        logger.info("Looking for children of {}, version {}", parent.getNetexId(), parent.getVersion());
        return stopPlaceRepository.findByParentRef(parent.getNetexId(), String.valueOf(parent.getVersion()));
    }
}
