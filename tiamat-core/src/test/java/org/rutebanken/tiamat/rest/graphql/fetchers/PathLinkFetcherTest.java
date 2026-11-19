package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetchingEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.rest.graphql.mappers.IdMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FIND_BY_STOP_PLACE_ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;

public class PathLinkFetcherTest {

    private static final String STOP_PLACE_ID = "NSR:StopPlace:1";
    private static final String PATH_LINK_ID = "NSR:PathLink:1";

    private final PathLinkRepository pathLinkRepository = mock(PathLinkRepository.class);
    private final IdMapper idMapper = mock(IdMapper.class);
    private final PathLinkFetcher fetcher = new PathLinkFetcher();

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(fetcher, "pathLinkRepository", pathLinkRepository);
        ReflectionTestUtils.setField(fetcher, "idMapper", idMapper);
    }

    @Test
    public void skipsPathLinkDeletedBetweenStopPlaceLookupAndVersionLookup() {
        DataFetchingEnvironment env = mock(DataFetchingEnvironment.class);
        when(env.getArguments()).thenReturn(Map.of());
        when(idMapper.extractIdIfPresent(eq(ID), any())).thenReturn(Optional.empty());
        when(idMapper.extractIdIfPresent(eq(FIND_BY_STOP_PLACE_ID), any())).thenReturn(Optional.of(STOP_PLACE_ID));
        when(pathLinkRepository.findByStopPlaceNetexId(STOP_PLACE_ID)).thenReturn(List.of(PATH_LINK_ID));
        when(pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(PATH_LINK_ID)).thenReturn(null);

        Object result = fetcher.get(env);

        assertThat((Collection<?>) result).isEmpty();
    }
}
