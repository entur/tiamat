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
import org.dataloader.DataLoaderRegistry;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.Value;
import org.rutebanken.tiamat.model.authorization.EntityPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service for creating and configuring DataLoader registry for GraphQL execution.
 * This provides proper DataLoader management for solving N+1 query problems.
 */
@Service
public class GraphQLDataLoaderRegistryService {

    private static final Logger logger = LoggerFactory.getLogger(GraphQLDataLoaderRegistryService.class);

    public static final String ENTITY_PERMISSIONS_LOADER = "entityPermissions";
    public static final String STOP_PLACE_LOADER = "stopPlace";
    public static final String TOPOGRAPHIC_PLACE_LOADER = "topographicPlace";
    public static final String QUAY_LOADER = "quay";
    public static final String QUAY_KEY_VALUES_LOADER = "quayKeyValues";
    public static final String ACCESSIBILITY_ASSESSMENT_LOADER = "accessibilityAssessmentDataLoader";
    public static final String GROUP_OF_STOP_PLACES_MEMBERS_LOADER = "groupOfStopPlacesMembersDataLoader";
    public static final String STOP_PLACE_KEY_VALUES_LOADER = "stopPlaceKeyValuesDataLoader";

    @Autowired
    private EntityPermissionsDataLoader entityPermissionsDataLoader;

    @Autowired
    private StopPlaceDataLoader stopPlaceDataLoader;

    @Autowired
    private TopographicPlaceDataLoader topographicPlaceDataLoader;

    @Autowired
    private QuayDataLoader quayDataLoader;

    @Autowired
    private AccessibilityAssessmentDataLoader accessibilityAssessmentDataLoader;

    @Autowired
    private GroupOfStopPlacesMembersDataLoader groupOfStopPlacesMembersDataLoader;

    @Autowired
    private StopPlaceKeyValuesDataLoader stopPlaceKeyValuesDataLoader;

    @Autowired
    private QuayKeyValuesDataLoader quayKeyValuesDataLoader;

    /**
     * Creates a new DataLoaderRegistry configured with all necessary DataLoaders
     *
     * @return configured DataLoaderRegistry for GraphQL execution
     */
    public DataLoaderRegistry createDataLoaderRegistry() {
        logger.debug("Creating DataLoaderRegistry with configured DataLoaders");

        DataLoaderRegistry registry = new DataLoaderRegistry();

        // Entity Permissions DataLoader
        DataLoader<String, EntityPermissions> entityPermissionsLoader = entityPermissionsDataLoader.createDataLoader();
        registry.register(ENTITY_PERMISSIONS_LOADER, entityPermissionsLoader);

        // StopPlace DataLoader
        DataLoader<StopPlaceDataLoader.StopPlaceKey, StopPlace> stopPlaceLoader = stopPlaceDataLoader.createDataLoader();
        registry.register(STOP_PLACE_LOADER, stopPlaceLoader);

        // TopographicPlace DataLoader
        DataLoader<TopographicPlaceDataLoader.TopographicPlaceKey, TopographicPlace> topographicPlaceLoader = topographicPlaceDataLoader.createDataLoader();
        registry.register(TOPOGRAPHIC_PLACE_LOADER, topographicPlaceLoader);

        // Quay DataLoader
        DataLoader<Long, List<Quay>> quayLoader = quayDataLoader.createDataLoader();
        registry.register(QUAY_LOADER, quayLoader);

        // Quay KeyValues DataLoader
        DataLoader<Long, Map<String, Value>> quayKeyValuesLoader = quayKeyValuesDataLoader.createDataLoader();
        registry.register(QUAY_KEY_VALUES_LOADER, quayKeyValuesLoader);

        // AccessibilityAssessment DataLoader
        DataLoader<Long, AccessibilityAssessment> accessibilityAssessmentLoader = accessibilityAssessmentDataLoader.createDataLoader();
        registry.register(ACCESSIBILITY_ASSESSMENT_LOADER, accessibilityAssessmentLoader);

        // GroupOfStopPlacesMembers DataLoader
        DataLoader<GroupOfStopPlacesMembersDataLoader.MemberKey, StopPlace> groupOfStopPlacesMembersLoader = groupOfStopPlacesMembersDataLoader.createDataLoader();
        registry.register(GROUP_OF_STOP_PLACES_MEMBERS_LOADER, groupOfStopPlacesMembersLoader);

        // StopPlaceKeyValues DataLoader
        DataLoader<Long, Map<String, Value>> stopPlaceKeyValuesLoader = stopPlaceKeyValuesDataLoader.createDataLoader();
        registry.register(STOP_PLACE_KEY_VALUES_LOADER, stopPlaceKeyValuesLoader);

        logger.debug("DataLoaderRegistry created with {} DataLoaders", registry.getKeys().size());

        return registry;
    }
}
