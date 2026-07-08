package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.repository.PurposeOfGroupingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("purposeOfGroupingFetcher")
public class PurposeOfGroupingFetcher implements DataFetcher {
    @Autowired
    private PurposeOfGroupingRepository purposeOfGroupingRepository;

    @Override
    @Transactional
    public Object get(DataFetchingEnvironment environment) throws Exception {

            return purposeOfGroupingRepository.findAllPurposeOfGrouping();

    }
}
