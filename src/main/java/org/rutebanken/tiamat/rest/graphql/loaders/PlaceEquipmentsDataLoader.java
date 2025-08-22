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

package org.rutebanken.tiamat.rest.graphql.loaders;

import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component
public class PlaceEquipmentsDataLoader {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    public DataLoader<Long, PlaceEquipment> create() {
        return DataLoaderFactory.newDataLoader(this::batchLoad);
    }

    private CompletableFuture<List<PlaceEquipment>> batchLoad(List<Long> placeEquipmentIds) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Set<Long> placeEquipmentIdSet = Set.copyOf(placeEquipmentIds);
                Map<Long, PlaceEquipment> placeEquipmentMap = stopPlaceRepository.findPlaceEquipmentsByIds(placeEquipmentIdSet);
                
                return placeEquipmentIds.stream()
                    .map(placeEquipmentMap::get)
                    .toList();
            } catch (Exception e) {
                throw new RuntimeException("Failed to batch load place equipments", e);
            }
        });
    }
}