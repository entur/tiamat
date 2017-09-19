/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.rest.graphql.mappers.PathLinkMapper;
import org.rutebanken.tiamat.service.stopplace.PathLinkUpdaterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
