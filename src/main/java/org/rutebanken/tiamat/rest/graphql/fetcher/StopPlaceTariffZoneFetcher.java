package org.rutebanken.tiamat.rest.graphql.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.ReferenceResolver;
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
