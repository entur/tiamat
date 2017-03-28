package org.rutebanken.tiamat.rest.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.AddressablePlace;
import org.rutebanken.tiamat.model.AddressablePlaceRefStructure;
import org.rutebanken.tiamat.model.VersionOfObjectRefStructure;
import org.rutebanken.tiamat.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("referenceFetcher")
@Transactional
class ObjectReferenceFetcher implements DataFetcher {


    private static final Logger logger = LoggerFactory.getLogger(ObjectReferenceFetcher.class);

    @Autowired
    private ReferenceResolver referenceResolver;

    @Override
    @Transactional
    public Object get(DataFetchingEnvironment environment) {
        VersionOfObjectRefStructure reference = (VersionOfObjectRefStructure) environment.getSource();
        return referenceResolver.resolve(reference);
    }
}
