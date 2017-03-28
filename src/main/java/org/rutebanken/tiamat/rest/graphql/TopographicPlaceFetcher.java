package org.rutebanken.tiamat.rest.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Service("topographicPlaceFetcher")
@Transactional
class TopographicPlaceFetcher implements DataFetcher {

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;


    @Override
    public Object get(DataFetchingEnvironment environment) {
        Example<TopographicPlace> example = getTopographicPlaceExample(environment);

        String netexId = environment.getArgument(ID);
        boolean allVersions = Boolean.parseBoolean(environment.getArgument(ALL_VERSIONS));

        if (netexId != null) {

            if (allVersions) {
                return topographicPlaceRepository.findByNetexId(netexId);
            } else {
                return Arrays.asList(topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(netexId));
            }
        }
        return topographicPlaceRepository.findAll(example);

    }

    private Example<TopographicPlace> getTopographicPlaceExample(DataFetchingEnvironment environment) {
        TopographicPlace tp = new TopographicPlace();
        if (environment.getArgument(TOPOGRAPHIC_PLACE_TYPE) != null) {
            tp.setTopographicPlaceType(environment.getArgument(TOPOGRAPHIC_PLACE_TYPE));
        }
        if (environment.getArgument(QUERY) != null) {
            EmbeddableMultilingualString mlString = new EmbeddableMultilingualString(environment.getArgument(QUERY));
            tp.setName(mlString);
        }
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase();

        return Example.of(tp, matcher);
    }
}
