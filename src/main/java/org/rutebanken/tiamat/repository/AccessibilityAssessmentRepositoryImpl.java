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

package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class AccessibilityAssessmentRepositoryImpl implements AccessibilityAssessmentRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(AccessibilityAssessmentRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<Long, AccessibilityAssessment> findBySiteElementIds(Set<Long> siteElementIds) {
        if (siteElementIds == null || siteElementIds.isEmpty()) {
            logger.debug("No site element IDs provided, returning empty map");
            return new HashMap<>();
        }

        logger.debug("Batch loading accessibility assessments for {} site elements", siteElementIds.size());

        // Query to find accessibility assessments for site elements
        // We need to query concrete entity types since SiteElement is a @MappedSuperclass
        String jpql = """
            SELECT sp.id, sp.accessibilityAssessment FROM StopPlace sp 
            WHERE sp.accessibilityAssessment IS NOT NULL 
            AND sp.id IN :siteElementIds
            UNION
            SELECT q.id, q.accessibilityAssessment FROM Quay q 
            WHERE q.accessibilityAssessment IS NOT NULL 
            AND q.id IN :siteElementIds
            """;

        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("siteElementIds", siteElementIds);

        List<Object[]> results = query.getResultList();
        
        Map<Long, AccessibilityAssessment> assessmentMap = results.stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],        // site element ID
                row -> (AccessibilityAssessment) row[1], // accessibility assessment
                (existing, replacement) -> existing // handle duplicates by keeping the first
            ));

        logger.debug("Found {} accessibility assessments for {} requested site elements", 
            assessmentMap.size(), siteElementIds.size());

        return assessmentMap;
    }
}