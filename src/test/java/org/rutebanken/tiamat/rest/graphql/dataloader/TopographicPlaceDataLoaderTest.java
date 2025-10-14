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
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TopographicPlaceDataLoaderTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TopographicPlaceRepository topographicPlaceRepository;

    @Mock
    private TypedQuery<TopographicPlace> typedQuery;

    @Test
    public void testDataLoaderBatchesSingleVersionRequests() throws ExecutionException, InterruptedException {
        // Setup test data
        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setNetexId("NSR:TopographicPlace:12345");
        topographicPlace.setVersion(1L);

        // Mock repository batch response
        Map<String, Map<Long, TopographicPlace>> batchResponse = Map.of(
            "NSR:TopographicPlace:12345", Map.of(1L, topographicPlace)
        );
        when(topographicPlaceRepository.findByNetexIdsAndVersions(any()))
            .thenReturn(batchResponse);

        // Create DataLoader
        TopographicPlaceDataLoader dataLoaderComponent = new TopographicPlaceDataLoader(topographicPlaceRepository);
        DataLoader<TopographicPlaceDataLoader.TopographicPlaceKey, TopographicPlace> dataLoader = 
            dataLoaderComponent.createDataLoader();

        // Test batching
        TopographicPlaceDataLoader.TopographicPlaceKey key = 
            new TopographicPlaceDataLoader.TopographicPlaceKey("NSR:TopographicPlace:12345", 1L);

        CompletableFuture<TopographicPlace> future1 = dataLoader.load(key);
        CompletableFuture<TopographicPlace> future2 = dataLoader.load(key);

        // Dispatch the batch
        dataLoader.dispatch();

        // Verify results
        TopographicPlace result1 = future1.get();
        TopographicPlace result2 = future2.get();

        assertThat(result1).isEqualTo(topographicPlace);
        assertThat(result2).isEqualTo(topographicPlace);
        assertThat(result1.getNetexId()).isEqualTo("NSR:TopographicPlace:12345");
    }

    @Test
    public void testDataLoaderBatchesMultipleVersionRequests() throws ExecutionException, InterruptedException {
        // Setup test data
        TopographicPlace topographicPlace1 = new TopographicPlace();
        topographicPlace1.setNetexId("NSR:TopographicPlace:12345");
        topographicPlace1.setVersion(1L);

        TopographicPlace topographicPlace2 = new TopographicPlace();
        topographicPlace2.setNetexId("NSR:TopographicPlace:12345");
        topographicPlace2.setVersion(2L);

        // Mock repository batch response
        Map<String, Map<Long, TopographicPlace>> batchResponse = Map.of(
            "NSR:TopographicPlace:12345", Map.of(1L, topographicPlace1, 2L, topographicPlace2)
        );
        when(topographicPlaceRepository.findByNetexIdsAndVersions(any()))
            .thenReturn(batchResponse);

        // Create DataLoader
        TopographicPlaceDataLoader dataLoaderComponent = new TopographicPlaceDataLoader(topographicPlaceRepository);
        DataLoader<TopographicPlaceDataLoader.TopographicPlaceKey, TopographicPlace> dataLoader = 
            dataLoaderComponent.createDataLoader();

        // Test batching with multiple versions
        TopographicPlaceDataLoader.TopographicPlaceKey key1 = 
            new TopographicPlaceDataLoader.TopographicPlaceKey("NSR:TopographicPlace:12345", 1L);
        TopographicPlaceDataLoader.TopographicPlaceKey key2 = 
            new TopographicPlaceDataLoader.TopographicPlaceKey("NSR:TopographicPlace:12345", 2L);

        CompletableFuture<TopographicPlace> future1 = dataLoader.load(key1);
        CompletableFuture<TopographicPlace> future2 = dataLoader.load(key2);

        // Dispatch the batch
        dataLoader.dispatch();

        // Verify results
        TopographicPlace result1 = future1.get();
        TopographicPlace result2 = future2.get();

        assertThat(result1.getVersion()).isEqualTo(1L);
        assertThat(result2.getVersion()).isEqualTo(2L);
    }

    @Test
    public void testDataLoaderBatchesMultipleTopographicPlaces() throws ExecutionException, InterruptedException {
        // Setup test data - simulating bbox query that crosses boundaries
        TopographicPlace oslo = new TopographicPlace();
        oslo.setNetexId("NSR:TopographicPlace:Oslo");
        oslo.setVersion(1L);

        TopographicPlace akershus = new TopographicPlace();
        akershus.setNetexId("NSR:TopographicPlace:Akershus");
        akershus.setVersion(1L);

        // Mock repository batch response
        Map<String, Map<Long, TopographicPlace>> batchResponse = Map.of(
            "NSR:TopographicPlace:Oslo", Map.of(1L, oslo),
            "NSR:TopographicPlace:Akershus", Map.of(1L, akershus)
        );
        when(topographicPlaceRepository.findByNetexIdsAndVersions(any()))
            .thenReturn(batchResponse);

        // Create DataLoader
        TopographicPlaceDataLoader dataLoaderComponent = new TopographicPlaceDataLoader(topographicPlaceRepository);
        DataLoader<TopographicPlaceDataLoader.TopographicPlaceKey, TopographicPlace> dataLoader = 
            dataLoaderComponent.createDataLoader();

        // Test batching with multiple topographic places (bbox crossing boundary scenario)
        TopographicPlaceDataLoader.TopographicPlaceKey osloKey = 
            new TopographicPlaceDataLoader.TopographicPlaceKey("NSR:TopographicPlace:Oslo", 1L);
        TopographicPlaceDataLoader.TopographicPlaceKey akershusKey = 
            new TopographicPlaceDataLoader.TopographicPlaceKey("NSR:TopographicPlace:Akershus", 1L);

        // Multiple stop places in Oslo
        CompletableFuture<TopographicPlace> osloFuture1 = dataLoader.load(osloKey);
        CompletableFuture<TopographicPlace> osloFuture2 = dataLoader.load(osloKey);
        CompletableFuture<TopographicPlace> osloFuture3 = dataLoader.load(osloKey);
        
        // Some stop places in Akershus
        CompletableFuture<TopographicPlace> akershusFuture1 = dataLoader.load(akershusKey);
        CompletableFuture<TopographicPlace> akershusFuture2 = dataLoader.load(akershusKey);

        // Dispatch the batch
        dataLoader.dispatch();

        // Verify results
        assertThat(osloFuture1.get()).isEqualTo(oslo);
        assertThat(osloFuture2.get()).isEqualTo(oslo);
        assertThat(osloFuture3.get()).isEqualTo(oslo);
        assertThat(akershusFuture1.get()).isEqualTo(akershus);
        assertThat(akershusFuture2.get()).isEqualTo(akershus);
        
        assertThat(osloFuture1.get().getNetexId()).isEqualTo("NSR:TopographicPlace:Oslo");
        assertThat(akershusFuture1.get().getNetexId()).isEqualTo("NSR:TopographicPlace:Akershus");
    }

    @Test
    public void testTopographicPlaceKey() {
        TopographicPlaceDataLoader.TopographicPlaceKey key1 = 
            new TopographicPlaceDataLoader.TopographicPlaceKey("NSR:TopographicPlace:12345", 1L);
        TopographicPlaceDataLoader.TopographicPlaceKey key2 = 
            new TopographicPlaceDataLoader.TopographicPlaceKey("NSR:TopographicPlace:12345", 1L);
        TopographicPlaceDataLoader.TopographicPlaceKey key3 = 
            new TopographicPlaceDataLoader.TopographicPlaceKey("NSR:TopographicPlace:12345", 2L);

        // Test equality
        assertThat(key1).isEqualTo(key2);
        assertThat(key1).isNotEqualTo(key3);

        // Test hashCode
        assertThat(key1.hashCode()).isEqualTo(key2.hashCode());
        assertThat(key1.hashCode()).isNotEqualTo(key3.hashCode());

        // Test toString
        assertThat(key1.toString()).contains("NSR:TopographicPlace:12345").contains("version=1");
    }
}