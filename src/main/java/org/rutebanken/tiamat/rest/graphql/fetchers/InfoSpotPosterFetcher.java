package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.rutebanken.tiamat.model.InfoSpot;
import org.rutebanken.tiamat.model.InfoSpotPoster;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InfoSpotPosterFetcher implements DataFetcher {

    @Autowired
    private ReferenceResolver referenceResolver;

    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
        InfoSpot infoSpot = (InfoSpot) dataFetchingEnvironment.getSource();
        return Optional.ofNullable(infoSpot)
                .map(InfoSpot::getPosters)
                .stream()
                .flatMap(Collection::stream)
                .map(referenceResolver::resolve)
                .filter(Objects::nonNull)
                .filter(p -> p instanceof InfoSpotPoster)
                .toList();
    }
}
