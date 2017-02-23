package org.rutebanken.tiamat.rest.graphql;

import graphql.language.*;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.rest.graphql.resolver.PathLinkMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Service("pathLinkUpdater")
@Transactional
class PathLinkUpdater implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(PathLinkUpdater.class);

    @Autowired
    private PathLinkRepository pathLinkRepository;

    @Autowired
    private PathLinkMapper pathLinkMapper;

    @Override
    public Object get(DataFetchingEnvironment environment) {

        List<Field> fields = environment.getFields();

        logger.info("Got fields {}", fields);

//        try {

            for (Field field : fields) {
                if (field.getName().equals(MUTATE_PATH_LINK)) {

                    if (environment.getArgument(OUTPUT_TYPE_PATH_LINK) != null) {
                        Map input = environment.getArgument(OUTPUT_TYPE_PATH_LINK);
                        PathLink pathLink = pathLinkMapper.map(input);
                        logger.info("Got {}", pathLink);

                        createOrUpdatePathLink(pathLink);

                    } else {
                        logger.warn("Could not find argument {}", OUTPUT_TYPE_PATH_LINK);
                    }

                }
            }
//        } catch (RuntimeException e) {
//            logger.warn("Caught exception when updating path link", e);
//            throw e;
//        }


        return pathLinkRepository.findAll();
    }

    public PathLink createOrUpdatePathLink(PathLink pathLink) {

        if(pathLink.getId() != null) {
            // Update?

            logger.info("Looking for PathLink with ID: {}", pathLink.getId());

            PathLink existingPathLink = pathLinkRepository.findOne(pathLink.getId());

            if(existingPathLink == null) {
                logger.warn("Specified path link with ID: {} does not exist", pathLink.getId());
            }

            logger.info("Found existing path link: {}", existingPathLink);





        } else {
            // new?
            logger.info("New path link {}", pathLink.getId());
        }

        return pathLink;

    }


}
