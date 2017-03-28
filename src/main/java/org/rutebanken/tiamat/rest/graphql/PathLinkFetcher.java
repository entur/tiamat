package org.rutebanken.tiamat.rest.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.rest.graphql.resolver.IdResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FIND_BY_STOP_PLACE_ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;

@Service("pathLinkFetcher")
@Transactional
class PathLinkFetcher implements DataFetcher {

    @Autowired
    private PathLinkRepository pathLinkRepository;

    @Autowired
    private IdResolver idResolver;

    @Override
    public Object get(DataFetchingEnvironment environment) {

        Optional<String> pathLinkNetexId = idResolver.extractIdIfPresent(ID, environment.getArguments());
        if (pathLinkNetexId.isPresent()) {
            return Arrays.asList(pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(pathLinkNetexId.get()));
        }

        Optional<String> stopPlaceNetexId = idResolver.extractIdIfPresent(FIND_BY_STOP_PLACE_ID, environment.getArguments());
        if (stopPlaceNetexId.isPresent()) {
            // Find pathlinks referencing to stops. Or path links referencing to quays that belong to stop.

            return pathLinkRepository.findAll(pathLinkRepository.findByStopPlaceNetexId(stopPlaceNetexId.get()));
        }

        return new ArrayList<PathLink>();

    }

}
