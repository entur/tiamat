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

package org.rutebanken.tiamat.rest.graphql.registry;

import org.rutebanken.tiamat.rest.graphql.operations.MultiModalityOperationsBuilder;
import org.rutebanken.tiamat.rest.graphql.operations.ParkingOperationsBuilder;
import org.rutebanken.tiamat.rest.graphql.operations.StopPlaceOperationsBuilder;
import org.rutebanken.tiamat.rest.graphql.operations.TagOperationsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Registry that aggregates all GraphQL operation builders.
 * Provides a single point of access to all mutation and query operations.
 */
@Component
public class GraphQLOperationsRegistry {

    private final StopPlaceOperationsBuilder stopPlaceOperationsBuilder;
    private final ParkingOperationsBuilder parkingOperationsBuilder;
    private final TagOperationsBuilder tagOperationsBuilder;
    private final MultiModalityOperationsBuilder multiModalityOperationsBuilder;

    @Autowired
    public GraphQLOperationsRegistry(
            StopPlaceOperationsBuilder stopPlaceOperationsBuilder,
            ParkingOperationsBuilder parkingOperationsBuilder,
            TagOperationsBuilder tagOperationsBuilder,
            MultiModalityOperationsBuilder multiModalityOperationsBuilder) {
        this.stopPlaceOperationsBuilder = stopPlaceOperationsBuilder;
        this.parkingOperationsBuilder = parkingOperationsBuilder;
        this.tagOperationsBuilder = tagOperationsBuilder;
        this.multiModalityOperationsBuilder = multiModalityOperationsBuilder;
    }

    // Individual getters for specific operation builders

    public StopPlaceOperationsBuilder getStopPlaceOperationsBuilder() {
        return stopPlaceOperationsBuilder;
    }

    public ParkingOperationsBuilder getParkingOperationsBuilder() {
        return parkingOperationsBuilder;
    }

    public TagOperationsBuilder getTagOperationsBuilder() {
        return tagOperationsBuilder;
    }

    public MultiModalityOperationsBuilder getMultiModalityOperationsBuilder() {
        return multiModalityOperationsBuilder;
    }
}
