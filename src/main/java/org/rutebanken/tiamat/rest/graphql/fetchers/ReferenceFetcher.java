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

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.VersionOfObjectRefStructure;
import org.rutebanken.tiamat.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("referenceFetcher")
@Transactional
class ReferenceFetcher implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceFetcher.class);

    @Autowired
    private ReferenceResolver referenceResolver;

    @Override
    @Transactional
    public Object get(DataFetchingEnvironment environment) {
        VersionOfObjectRefStructure reference = (VersionOfObjectRefStructure) environment.getSource();
        logger.info("Fetching reference: {}, version: {}", reference.getRef(), reference.getVersion());
        return referenceResolver.resolve(reference);
    }
}
