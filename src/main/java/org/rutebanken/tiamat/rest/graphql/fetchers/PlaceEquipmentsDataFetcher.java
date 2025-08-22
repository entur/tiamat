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
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.rutebanken.tiamat.rest.graphql.dataloader.GraphQLDataLoaderRegistryService.PLACE_EQUIPMENTS_LOADER;

@Component
public class PlaceEquipmentsDataFetcher implements DataFetcher<CompletableFuture<PlaceEquipment>> {

    @Override
    public CompletableFuture<PlaceEquipment> get(DataFetchingEnvironment environment) throws Exception {
        PlaceEquipment placeEquipment = null;
        
        if (environment.getSource() instanceof StopPlace stopPlace) {
            placeEquipment = stopPlace.getPlaceEquipments();
        } else if (environment.getSource() instanceof Quay quay) {
            placeEquipment = quay.getPlaceEquipments();
        }

        if (placeEquipment == null || placeEquipment.getId() == null) {
            return CompletableFuture.completedFuture(null);
        }

        DataLoader<Long, PlaceEquipment> dataLoader = environment.getDataLoader(PLACE_EQUIPMENTS_LOADER);
        return dataLoader.load(placeEquipment.getId());
    }
}