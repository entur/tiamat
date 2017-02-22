package org.rutebanken.tiamat.rest.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;

@Service("pathLinkFetcher")
@Transactional
class PathLinkFetcher implements DataFetcher {

    @Autowired
    private PathLinkRepository pathLinkRepository;

    @Override
    public Object get(DataFetchingEnvironment environment) {
        if (environment.getArgument(ID) != null) {
            String netexId = environment.<String>getArgument(ID);
            Long tiamatId = NetexIdMapper.getTiamatId(netexId);

            return Arrays.asList(pathLinkRepository.findOne(tiamatId));
        }
        return pathLinkRepository.findAll();

    }

}
