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

package org.rutebanken.tiamat.rest.graphql.dataloader;

import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.repository.AccessibilityAssessmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * DataLoader for efficiently loading AccessibilityAssessment entities to solve N+1 query problems.
 * Batches multiple accessibility assessment requests for site elements into single database queries.
 */
@Component
public class AccessibilityAssessmentDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(AccessibilityAssessmentDataLoader.class);

    private final AccessibilityAssessmentRepository accessibilityAssessmentRepository;

    @Autowired
    public AccessibilityAssessmentDataLoader(AccessibilityAssessmentRepository accessibilityAssessmentRepository) {
        this.accessibilityAssessmentRepository = accessibilityAssessmentRepository;
    }

    /**
     * Creates a DataLoader for batching accessibility assessment requests by site element ID
     */
    public DataLoader<Long, AccessibilityAssessment> createDataLoader() {
        return DataLoaderFactory.newDataLoader(siteElementIds -> {
            logger.debug("Batch loading accessibility assessments for {} site elements", siteElementIds.size());
            
            try {
                // Get accessibility assessments for the requested site element IDs
                Map<Long, AccessibilityAssessment> assessmentsBySiteElementId = 
                    accessibilityAssessmentRepository.findBySiteElementIds(Set.copyOf(siteElementIds));
                
                // Return in the order requested, with null for site elements with no assessment
                List<AccessibilityAssessment> result = siteElementIds.stream()
                    .map(siteElementId -> {
                        AccessibilityAssessment assessment = assessmentsBySiteElementId.get(siteElementId);
                        if (assessment != null) {
                            logger.debug("Found accessibility assessment for site element ID {}", siteElementId);
                        }
                        return assessment;
                    })
                    .collect(Collectors.toList());
                    
                return CompletableFuture.completedFuture(result);
                    
            } catch (Exception e) {
                logger.error("Error in AccessibilityAssessmentDataLoader batch function", e);
                return CompletableFuture.failedFuture(new RuntimeException("Failed to load accessibility assessments", e));
            }
        });
    }
}