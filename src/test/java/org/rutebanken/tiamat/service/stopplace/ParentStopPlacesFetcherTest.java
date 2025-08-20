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

package org.rutebanken.tiamat.service.stopplace;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.MultilingualString;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.dataloader.DataLoader;
import org.rutebanken.tiamat.rest.graphql.dataloader.ParentStopPlaceDataLoader;

import java.util.concurrent.CompletableFuture;

public class ParentStopPlacesFetcherTest {

    private StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);

    private EntityManager entityManager = mock(EntityManager.class);
    private ParentStopPlacesFetcher parentStopPlacesFetcher = new ParentStopPlacesFetcher(stopPlaceRepository, entityManager);

    @Before
    public void before() {
        when(entityManager.unwrap(any())).thenReturn(mock(Session.class));
    }

    private DataLoader<ParentStopPlaceDataLoader.ParentStopPlaceKey, StopPlace> createMockDataLoader() {
        @SuppressWarnings("unchecked")
        DataLoader<ParentStopPlaceDataLoader.ParentStopPlaceKey, StopPlace> mockDataLoader = mock(DataLoader.class);
        
        // Mock the load method to return the parent based on the key
        when(mockDataLoader.load(any(ParentStopPlaceDataLoader.ParentStopPlaceKey.class)))
            .thenAnswer(invocation -> {
                ParentStopPlaceDataLoader.ParentStopPlaceKey key = invocation.getArgument(0);
                StopPlace result = stopPlaceRepository.findFirstByNetexIdAndVersion(key.getNetexId(), key.getVersion());
                return CompletableFuture.completedFuture(result);
            });
        
        // Mock dispatch to return completed future (no-op for our test)    
        when(mockDataLoader.dispatch()).thenReturn(CompletableFuture.completedFuture(null));
            
        return mockDataLoader;
    }

    @Test
    public void resolveParents() throws Exception {

        int counter = 0;
        StopPlace parent = createAndMockStopPlaceWithNetexIdAndVersion(++counter);
        parent.setParentStopPlace(true);
        StopPlace parentSecondVersion = new StopPlace();
        parentSecondVersion.setParentStopPlace(true);
        parentSecondVersion.setNetexId(parent.getNetexId());
        parentSecondVersion.setVersion(2L);

        StopPlace child1 = createAndMockStopPlaceWithNetexIdAndVersion(++counter);
        child1.setParentStopPlace(false);
        addParentRef(child1, parent);
        StopPlace child2 = createAndMockStopPlaceWithNetexIdAndVersion(++counter);
        child2.setParentStopPlace(false);
        addParentRef(child2, parent);

        DataLoader<ParentStopPlaceDataLoader.ParentStopPlaceKey, StopPlace> mockDataLoader = createMockDataLoader();
        List<StopPlace> result = parentStopPlacesFetcher.resolveParents(Arrays.asList(parent, parentSecondVersion, child1, child2), false, mockDataLoader);

        assertThat(result).extracting(this::concatenateNetexIdVersion)
                .as("parent first version should be kept")
                .contains(concatenateNetexIdVersion(parent));
        assertThat(result).extracting(this::concatenateNetexIdVersion)
                .as("parent second version should be kept")
                .contains(concatenateNetexIdVersion(parentSecondVersion));
        assertThat(result).extracting(stopPlace -> stopPlace.getNetexId()).doesNotContain(child1.getNetexId());
        assertThat(result).extracting(stopPlace -> stopPlace.getNetexId()).doesNotContain(child2.getNetexId());
    }

    @Test
    public void resolveParentsKeepChilds() throws Exception {

        final String parentStopPlaceName = "name";

        int counter = 10;
        StopPlace parent = createAndMockStopPlaceWithNetexIdAndVersion(++counter);
        parent.setName(new EmbeddableMultilingualString(parentStopPlaceName, "nor"));
        parent.setParentStopPlace(true);

        StopPlace parentSecondVersion = new StopPlace();
        parentSecondVersion.setParentStopPlace(true);
        parentSecondVersion.setNetexId(parent.getNetexId());
        parentSecondVersion.setName(new EmbeddableMultilingualString(parentStopPlaceName, "nor"));
        parentSecondVersion.setVersion(2L);

        StopPlace child1 = createAndMockStopPlaceWithNetexIdAndVersion(++counter);
        child1.setParentStopPlace(false);
        addParentRef(child1, parent);

        StopPlace child2 = createAndMockStopPlaceWithNetexIdAndVersion(++counter);
        child2.setParentStopPlace(false);
        addParentRef(child2, parent);

        DataLoader<ParentStopPlaceDataLoader.ParentStopPlaceKey, StopPlace> mockDataLoader = createMockDataLoader();
        List<StopPlace> result = parentStopPlacesFetcher.resolveParents(Arrays.asList(parent, parentSecondVersion, child1, child2), true, mockDataLoader);

        assertThat(result).extracting(this::concatenateNetexIdVersion)
                .as("parent first version should be kept")
                .contains(concatenateNetexIdVersion(parent));
        assertThat(result).extracting(this::concatenateNetexIdVersion)
                .as("parent second version should be kept")
                .contains(concatenateNetexIdVersion(parentSecondVersion));
        assertThat(result).extracting(stopPlace -> stopPlace.getNetexId()).contains(child1.getNetexId());
        assertThat(result).extracting(stopPlace -> stopPlace.getNetexId()).contains(child2.getNetexId());

        Set<EmbeddableMultilingualString> childrensNames = result.stream()
                .filter(stopPlace -> !stopPlace.isParentStopPlace())
                .map(StopPlace::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        assertThat(childrensNames).extracting(MultilingualString::getValue).containsOnly(parentStopPlaceName, parentStopPlaceName);

    }

    private void addParentRef(StopPlace child, StopPlace parent) {
        child.setParentSiteRef(new SiteRefStructure(parent.getNetexId(), String.valueOf(parent.getVersion())));
    }

    private String concatenateNetexIdVersion(EntityInVersionStructure entity) {
        return entity.getVersion() + entity.getNetexId();
    }

    private StopPlace createAndMockStopPlaceWithNetexIdAndVersion(int counter) {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("XYZ:StopPlace:" + counter);
        stopPlace.setVersion(1L);
        when(stopPlaceRepository.findFirstByNetexIdAndVersion(stopPlace.getNetexId(), stopPlace.getVersion())).thenReturn(stopPlace);
        return stopPlace;
    }

}
