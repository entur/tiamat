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
import org.rutebanken.tiamat.model.vehicle.VehicleType;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Fetches vehicle plans for vehicle type.
 * Resolves vehicle plan references to vehicle plan entities.
 */
@Component
public class VehicleTypeDeckPlanFetcher implements DataFetcher {

    @Autowired
    private ReferenceResolver referenceResolver;

    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
        VehicleType vehicleType = dataFetchingEnvironment.getSource();
        return vehicleType.getDeckPlan();
    }

}
