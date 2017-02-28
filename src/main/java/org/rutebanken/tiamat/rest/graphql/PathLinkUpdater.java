package org.rutebanken.tiamat.rest.graphql;

import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.rest.graphql.resolver.PathLinkMapper;
import org.rutebanken.tiamat.service.PathLinkUpdaterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MUTATE_PATH_LINK;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_PATH_LINK;

@Service("pathLinkUpdater")
@Transactional(propagation = Propagation.REQUIRES_NEW)
class PathLinkUpdater implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(PathLinkUpdater.class);

    @Autowired
    private PathLinkRepository pathLinkRepository;

    @Autowired
    private PathLinkMapper pathLinkMapper;

    @Autowired
    private PathLinkUpdaterService pathLinkUpdaterService;


    @Override
    public Object get(DataFetchingEnvironment environment) {

        List<Field> fields = environment.getFields();

        logger.trace("Got fields {}", fields);

        List<PathLink> createdOrUpdated = new ArrayList<>();

        for (Field field : fields) {
            if (field.getName().equals(MUTATE_PATH_LINK)) {

                if (environment.getArgument(OUTPUT_TYPE_PATH_LINK) != null) {
                    List<Map> inputs = environment.getArgument(OUTPUT_TYPE_PATH_LINK);
                    for(Map input : inputs) {

                        PathLink pathLink = pathLinkMapper.map(input);
                        logger.debug("Mapped {}", pathLink);

                        PathLink createdOrUpdatedPathLink = pathLinkUpdaterService.createOrUpdatePathLink(pathLink);
                        createdOrUpdated.add(createdOrUpdatedPathLink);
                    }

                } else {
                    logger.warn("Could not find argument {}", OUTPUT_TYPE_PATH_LINK);
                }

            }
        }

        return createdOrUpdated;
    }
}
