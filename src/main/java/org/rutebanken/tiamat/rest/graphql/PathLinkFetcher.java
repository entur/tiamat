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
import java.util.stream.Collectors;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ALL_VERSIONS;
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

        boolean allVersions = Boolean.valueOf(environment.getArgument(ALL_VERSIONS));

        if (pathLinkNetexId.isPresent()) {
            if(allVersions) {
                return pathLinkRepository.findByNetexId(pathLinkNetexId.get());
            }
            return Arrays.asList(pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(pathLinkNetexId.get()));
        }

        Optional<String> stopPlaceNetexId = idResolver.extractIdIfPresent(FIND_BY_STOP_PLACE_ID, environment.getArguments());
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
                    .collect(Collectors.toList());
        }

        return new ArrayList<PathLink>();

    }

}
