package org.rutebanken.tiamat.rest.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.rest.graphql.resolver.IdResolver;
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

    @Autowired
    private IdResolver idResolver;

    @Override
    public Object get(DataFetchingEnvironment environment) {

        Long tiamatId = idResolver.extractIdIfPresent(ID, environment.getArguments());
        if (tiamatId != null) {
            return Arrays.asList(pathLinkRepository.findOne(tiamatId));
        }
        return pathLinkRepository.findAll();

    }

}
