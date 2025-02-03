package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import org.rutebanken.tiamat.model.InfoSpot;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.repository.InfoSpotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("quayInfoSpotsFetcher")
public class QuayInfoSpotsFetcher implements DataFetcher<List<InfoSpot>> {
    private static final Logger logger = LoggerFactory.getLogger(QuayInfoSpotsFetcher.class);

    @Autowired
    InfoSpotRepository infoSpotRepository;

    @Override
    public List<InfoSpot> get(DataFetchingEnvironment environment) {

        Quay quay = environment.getSource();

        logger.info("Fetching info spots for quay {}", quay.getNetexId());

        return infoSpotRepository.findForAssociation(quay.getNetexId());
    }
}
