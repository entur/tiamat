package org.rutebanken.tiamat.rest.graphql;

import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.rest.graphql.resolver.PathLinkMapper;
import org.rutebanken.tiamat.service.PathLinkUpdaterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MUTATE_PATH_LINK;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_PATH_LINK;

@Service("pathLinkUpdater")
@Transactional
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

        try {
            for (Field field : fields) {
                if (field.getName().equals(MUTATE_PATH_LINK)) {

                    if (environment.getArgument(OUTPUT_TYPE_PATH_LINK) != null) {
                        Map input = environment.getArgument(OUTPUT_TYPE_PATH_LINK);
                        PathLink pathLink = pathLinkMapper.map(input);
                        logger.debug("Mapped {}", pathLink);

                        PathLink importedPathLink = pathLinkUpdaterService.createOrUpdatePathLink(pathLink);
                        return Arrays.asList(importedPathLink);

                    } else {
                        logger.warn("Could not find argument {}", OUTPUT_TYPE_PATH_LINK);
                    }

                }
            }
        } catch (RuntimeException e) {
            logger.warn("Caught exception when updating path link", e);
            throw e;
        }
        return pathLinkRepository.findAll();
    }
}
