package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import org.rutebanken.tiamat.model.InfoSpot;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.InfoSpotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("stopPlaceInfoSpotsFetcher")
public class StopPlaceInfoSpotsFetcher implements DataFetcher<List<InfoSpot>> {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceInfoSpotsFetcher.class);

    @Autowired
    InfoSpotRepository infoSpotRepository;

    @Override
    public List<InfoSpot> get(DataFetchingEnvironment environment) {

        StopPlace stopPlace = environment.getSource();

        logger.info("Fetching info spots for stop place {}", stopPlace.getNetexId());

        return infoSpotRepository.findForAssociation(stopPlace.getNetexId());
    }
}
