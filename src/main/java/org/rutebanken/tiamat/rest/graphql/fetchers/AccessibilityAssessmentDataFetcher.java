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
import org.dataloader.DataLoader;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.SiteElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * GraphQL DataFetcher for AccessibilityAssessment that uses DataLoader to batch requests
 * and avoid N+1 query problems when accessing the accessibilityAssessment field on SiteElement.
 */
@Component("accessibilityAssessmentDataFetcher")
public class AccessibilityAssessmentDataFetcher implements DataFetcher<CompletableFuture<AccessibilityAssessment>> {

    private static final Logger logger = LoggerFactory.getLogger(AccessibilityAssessmentDataFetcher.class);

    @Override
    public CompletableFuture<AccessibilityAssessment> get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        SiteElement siteElement = dataFetchingEnvironment.getSource();
        
        if (siteElement == null || siteElement.getId() == null) {
            logger.debug("No site element or site element ID found, returning null");
            return CompletableFuture.completedFuture(null);
        }

        DataLoader<Long, AccessibilityAssessment> dataLoader = 
            dataFetchingEnvironment.getDataLoader("accessibilityAssessmentDataLoader");
        
        if (dataLoader == null) {
            logger.error("AccessibilityAssessment DataLoader not found in GraphQL context");
            throw new RuntimeException("AccessibilityAssessment DataLoader not registered");
        }

        logger.debug("Loading accessibility assessment for site element ID: {}", siteElement.getId());
        return dataLoader.load(siteElement.getId());
    }
}