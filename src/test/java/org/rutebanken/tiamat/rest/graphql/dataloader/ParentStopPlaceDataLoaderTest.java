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

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.dataloader.DataLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParentStopPlaceDataLoaderTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private StopPlaceRepository stopPlaceRepository;

    @Mock
    private TypedQuery<StopPlace> typedQuery;

    @Test
    public void testDataLoaderBatchesSingleVersionRequests() throws ExecutionException, InterruptedException {
        // Setup test data
        StopPlace parentStop = new StopPlace();
        parentStop.setNetexId("NSR:StopPlace:12345");
        parentStop.setVersion(1L);

        // Mock repository response
        when(stopPlaceRepository.findFirstByNetexIdAndVersion("NSR:StopPlace:12345", 1L))
            .thenReturn(parentStop);

        // Create DataLoader
        ParentStopPlaceDataLoader dataLoaderComponent = new ParentStopPlaceDataLoader(entityManager, stopPlaceRepository);
        DataLoader<ParentStopPlaceDataLoader.ParentStopPlaceKey, StopPlace> dataLoader = 
            dataLoaderComponent.createDataLoader();

        // Test batching
        ParentStopPlaceDataLoader.ParentStopPlaceKey key = 
            new ParentStopPlaceDataLoader.ParentStopPlaceKey("NSR:StopPlace:12345", 1L);

        CompletableFuture<StopPlace> future1 = dataLoader.load(key);
        CompletableFuture<StopPlace> future2 = dataLoader.load(key);

        // Dispatch the batch
        dataLoader.dispatch();

        // Verify results
        StopPlace result1 = future1.get();
        StopPlace result2 = future2.get();

        assertThat(result1).isEqualTo(parentStop);
        assertThat(result2).isEqualTo(parentStop);
        assertThat(result1.getNetexId()).isEqualTo("NSR:StopPlace:12345");
    }

    @Test
    public void testDataLoaderBatchesMultipleVersionRequests() throws ExecutionException, InterruptedException {
        // Setup test data
        StopPlace parentStop1 = new StopPlace();
        parentStop1.setNetexId("NSR:StopPlace:12345");
        parentStop1.setVersion(1L);

        StopPlace parentStop2 = new StopPlace();
        parentStop2.setNetexId("NSR:StopPlace:12345");
        parentStop2.setVersion(2L);

        // Mock EntityManager query
        when(entityManager.createQuery(anyString(), eq(StopPlace.class)))
            .thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("netexId"), anyString())).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("versions"), any(List.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(parentStop1, parentStop2));

        // Create DataLoader
        ParentStopPlaceDataLoader dataLoaderComponent = new ParentStopPlaceDataLoader(entityManager, stopPlaceRepository);
        DataLoader<ParentStopPlaceDataLoader.ParentStopPlaceKey, StopPlace> dataLoader = 
            dataLoaderComponent.createDataLoader();

        // Test batching with multiple versions
        ParentStopPlaceDataLoader.ParentStopPlaceKey key1 = 
            new ParentStopPlaceDataLoader.ParentStopPlaceKey("NSR:StopPlace:12345", 1L);
        ParentStopPlaceDataLoader.ParentStopPlaceKey key2 = 
            new ParentStopPlaceDataLoader.ParentStopPlaceKey("NSR:StopPlace:12345", 2L);

        CompletableFuture<StopPlace> future1 = dataLoader.load(key1);
        CompletableFuture<StopPlace> future2 = dataLoader.load(key2);

        // Dispatch the batch
        dataLoader.dispatch();

        // Verify results
        StopPlace result1 = future1.get();
        StopPlace result2 = future2.get();

        assertThat(result1.getVersion()).isEqualTo(1L);
        assertThat(result2.getVersion()).isEqualTo(2L);
    }

    @Test
    public void testParentStopPlaceKey() {
        ParentStopPlaceDataLoader.ParentStopPlaceKey key1 = 
            new ParentStopPlaceDataLoader.ParentStopPlaceKey("NSR:StopPlace:12345", 1L);
        ParentStopPlaceDataLoader.ParentStopPlaceKey key2 = 
            new ParentStopPlaceDataLoader.ParentStopPlaceKey("NSR:StopPlace:12345", 1L);
        ParentStopPlaceDataLoader.ParentStopPlaceKey key3 = 
            new ParentStopPlaceDataLoader.ParentStopPlaceKey("NSR:StopPlace:12345", 2L);

        // Test equality
        assertThat(key1).isEqualTo(key2);
        assertThat(key1).isNotEqualTo(key3);

        // Test hashCode
        assertThat(key1.hashCode()).isEqualTo(key2.hashCode());
        assertThat(key1.hashCode()).isNotEqualTo(key3.hashCode());

        // Test toString
        assertThat(key1.toString()).contains("NSR:StopPlace:12345").contains("version=1");
    }
}