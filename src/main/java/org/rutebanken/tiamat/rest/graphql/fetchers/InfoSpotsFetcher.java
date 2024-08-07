package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import org.rutebanken.tiamat.repository.InfoSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;

@Service("infoSpotsFetcher")
public class InfoSpotsFetcher implements DataFetcher {

    @Autowired
    private InfoSpotRepository infoSpotRepository;

    @Override
    @Transactional(readOnly = true)
    public Object get(DataFetchingEnvironment environment) throws Exception {

        if (environment.containsArgument(ID)) {
            String netexId = environment.getArgument(ID);
            return List.of(infoSpotRepository.findFirstByNetexIdOrderByVersionDesc(netexId));
        }
        return infoSpotRepository.findAllMaxVersion();
    }

}
