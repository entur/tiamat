package org.rutebanken.tiamat.rest.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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
        boolean allVersions = Boolean.parseBoolean(environment.getArgument(ALL_VERSIONS));

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
