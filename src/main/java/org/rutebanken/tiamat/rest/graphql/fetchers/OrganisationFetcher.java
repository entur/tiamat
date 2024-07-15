package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.Organisation;
import org.rutebanken.tiamat.repository.OrganisationRepository;
import org.rutebanken.tiamat.rest.graphql.GraphQLNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PAGE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SIZE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VERSION;

@Service("organisationFetcher")
@Transactional
public class OrganisationFetcher implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(OrganisationFetcher.class);

    @Autowired
    OrganisationRepository organisationRepository;

    @Override
    public Object get(DataFetchingEnvironment environment) {

        PageRequest pageable = PageRequest.of(environment.getArgument(PAGE), environment.getArgument(SIZE));

        Page<Organisation> allOrganisations;

        String organisationId = environment.getArgument(GraphQLNames.ID);
        Integer version = (Integer) environment.getArgument(VERSION);

        if (organisationId != null) {
            List<Organisation> organisationList = new ArrayList<>();
            if(version != null && version > 0) {
                logger.info("Finding organisation by netexid {} and version {}", organisationId, version);
                organisationList = Arrays.asList(organisationRepository.findFirstByNetexIdAndVersion(organisationId, version));
                allOrganisations = new PageImpl<>(organisationList, pageable, 1L);
            } else {
                logger.info("Finding first organisation by netexid {} and highest version", organisationId);
                organisationList.add(organisationRepository.findFirstByNetexIdOrderByVersionDesc(organisationId));
                allOrganisations = new PageImpl<>(organisationList, pageable, 1L);
            }
        } else {
            logger.info("Finding all organisations regardless of version and validity");
            allOrganisations = organisationRepository.findAll(pageable);
        }

        return allOrganisations;
    }
}
