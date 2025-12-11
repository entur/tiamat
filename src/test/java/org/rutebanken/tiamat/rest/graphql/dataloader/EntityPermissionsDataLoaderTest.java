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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.authorization.EntityPermissions;
import org.rutebanken.tiamat.netex.id.TypeFromIdResolver;
import org.rutebanken.tiamat.repository.generic.GenericEntityInVersionRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EntityPermissionsDataLoaderTest {

    @Mock
    private GenericEntityInVersionRepository genericEntityInVersionRepository;

    @Mock
    private TypeFromIdResolver typeFromIdResolver;

    @Mock
    private AuthorizationService authorizationService;

    @Test
    public void testBatchLoadEntityPermissions() {
        // Setup test data
        StopPlace stopPlace1 = new StopPlace();
        stopPlace1.setNetexId("NSR:StopPlace:12345");
        
        StopPlace stopPlace2 = new StopPlace();
        stopPlace2.setNetexId("NSR:StopPlace:67890");

        List<String> netexIds = Arrays.asList("NSR:StopPlace:12345", "NSR:StopPlace:67890");

        // Mock dependencies
        when(typeFromIdResolver.resolveClassFromId("NSR:StopPlace:12345")).thenReturn((Class) StopPlace.class);
        when(typeFromIdResolver.resolveClassFromId("NSR:StopPlace:67890")).thenReturn((Class) StopPlace.class);

        Map<Class<?>, List<String>> idsByType = new HashMap<>();
        idsByType.put(StopPlace.class, netexIds);

        Map<String, org.rutebanken.tiamat.model.EntityInVersionStructure> entities = new HashMap<>();
        entities.put("NSR:StopPlace:12345", stopPlace1);
        entities.put("NSR:StopPlace:67890", stopPlace2);
        
        when(genericEntityInVersionRepository.findLatestVersionByNetexIdsGrouped(eq(idsByType)))
            .thenReturn(entities);

        // Mock authorization service responses
        when(authorizationService.canEditEntity(any(org.rutebanken.tiamat.model.EntityInVersionStructure.class))).thenReturn(true);
        when(authorizationService.canDeleteEntity(any(org.rutebanken.tiamat.model.EntityInVersionStructure.class))).thenReturn(false);
        when(authorizationService.getAllowedStopPlaceTypes(any(org.rutebanken.tiamat.model.EntityInVersionStructure.class))).thenReturn(java.util.Set.of());
        when(authorizationService.getBannedStopPlaceTypes(any(org.rutebanken.tiamat.model.EntityInVersionStructure.class))).thenReturn(java.util.Set.of());
        when(authorizationService.getAllowedSubmodes(any(org.rutebanken.tiamat.model.EntityInVersionStructure.class))).thenReturn(java.util.Set.of());
        when(authorizationService.getBannedSubmodes(any(org.rutebanken.tiamat.model.EntityInVersionStructure.class))).thenReturn(java.util.Set.of());

        // Create and test the DataLoader
        EntityPermissionsDataLoader dataLoader = new EntityPermissionsDataLoader(
            genericEntityInVersionRepository, typeFromIdResolver, authorizationService);
        
        Map<String, EntityPermissions> result = dataLoader.batchLoadEntityPermissions(netexIds);

        // Verify results
        assertThat(result).hasSize(2);
        assertThat(result.get("NSR:StopPlace:12345")).isNotNull();
        assertThat(result.get("NSR:StopPlace:67890")).isNotNull();
        
        EntityPermissions permissions1 = result.get("NSR:StopPlace:12345");
        assertThat(permissions1.isCanEdit()).isTrue();
        assertThat(permissions1.isCanDelete()).isFalse();
    }

    @Test
    public void testBatchLoadEntityPermissionsEmptyList() {
        EntityPermissionsDataLoader dataLoader = new EntityPermissionsDataLoader(
            genericEntityInVersionRepository, typeFromIdResolver, authorizationService);
        
        Map<String, EntityPermissions> result = dataLoader.batchLoadEntityPermissions(Arrays.asList());
        
        assertThat(result).isEmpty();
    }
}