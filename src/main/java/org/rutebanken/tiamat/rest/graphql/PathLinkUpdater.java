package org.rutebanken.tiamat.rest.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("pathLinkUpdater")
@Transactional
class PathLinkUpdater implements DataFetcher {

    @Autowired
    private PathLinkRepository pathLinkRepository;

    @Override
    public Object get(DataFetchingEnvironment environment) {
        if (environment.getArgument("PathLink") != null) {

            //TODO: Create / update
        }
        return pathLinkRepository.findAll();

    }

}
