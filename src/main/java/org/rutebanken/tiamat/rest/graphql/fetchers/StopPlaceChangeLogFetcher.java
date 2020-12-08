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
import org.rutebanken.tiamat.dtoassembling.dto.StopPlaceChangelogDto;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import org.rutebanken.tiamat.repository.search.ChangedStopPlaceSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

import java.time.temporal.ChronoUnit;
import java.util.List;


import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PAGE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SIZE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STOP_PLACE_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALID_BETWEEN_FROM_DATE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALID_BETWEEN_TO_DATE;


@Service("stopPlaceChangelogFetcher")
@Transactional
class StopPlaceChangeLogFetcher implements DataFetcher {
    private static final int DEFAULT_CHANGELOG_DAYS = 7;

    //TODO: Update Stopplace changelog fetcher

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Override
    @Transactional
    public Object get(DataFetchingEnvironment environment) {
        Instant from;
        Instant to;

        //TODO Add date validation
        final Object fromDateArgument = environment.getArgument(VALID_BETWEEN_FROM_DATE);
        final Object toDateArgument = environment.getArgument(VALID_BETWEEN_TO_DATE);
        if (toDateArgument instanceof Instant) {
            to = (Instant) toDateArgument;
        } else {
            to  = Instant.now();
        }
        if (fromDateArgument instanceof Instant) {
            from = (Instant) fromDateArgument;
        } else {
            from = to.minus(DEFAULT_CHANGELOG_DAYS, ChronoUnit.DAYS);
        }

        if (from.isAfter(to)) {
            from = to.minus(DEFAULT_CHANGELOG_DAYS, ChronoUnit.DAYS);
        }

        final long duration = Duration.between(from,to).toDays();

        if(duration > DEFAULT_CHANGELOG_DAYS) {
            from = to.minus(DEFAULT_CHANGELOG_DAYS,ChronoUnit.DAYS);
        }

        List<StopTypeEnumeration> stopTypes = environment.getArgument(STOP_PLACE_TYPE);
        if (stopTypes != null && !stopTypes.isEmpty()) {
            //TODO: implement stoplaceChangelogSearchBuilder
           // stopPlaceChangeLogSearchBuilder.setStopTypeEnumerations(stopTypes.stream()
           //         .filter(type -> type != null)
            //        .collect(Collectors.toList())
           // );
        }

        final ChangedStopPlaceSearch changedStopPlaceSearch = new ChangedStopPlaceSearch(from,to, null);

        List<StopPlaceChangelogDto> stopPlaceChangelogDtoList = stopPlaceRepository.findStopPlaceChangelog(changedStopPlaceSearch);

                return getStopPlaces(environment,stopPlaceChangelogDtoList,stopPlaceChangelogDtoList.size());

    }

    private PageImpl<StopPlaceChangelogDto> getStopPlaces(DataFetchingEnvironment environment, List<StopPlaceChangelogDto> stopPlaces, long size) {
        return new PageImpl<>(stopPlaces, PageRequest.of(environment.getArgument(PAGE), environment.getArgument(SIZE)), size);
    }

}
