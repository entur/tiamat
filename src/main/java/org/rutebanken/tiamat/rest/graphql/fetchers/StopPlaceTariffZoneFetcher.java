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
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * Fetches tariff zones for stop place.
 * Resolves tariff zone references to tariff zone entities.
 */
@Component
public class StopPlaceTariffZoneFetcher implements DataFetcher {

    @Autowired
    private ReferenceResolver referenceResolver;

    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
        StopPlace stopPlace = (StopPlace) dataFetchingEnvironment.getSource();
        if(stopPlace.getTariffZones() != null) {
            return stopPlace.getTariffZones()
                    .stream()
                    .map(tariffZoneRef -> referenceResolver.resolve(tariffZoneRef))
                    .filter(Objects::nonNull)
                    .collect(toList());
        }
        return new ArrayList<>();
    }

}
