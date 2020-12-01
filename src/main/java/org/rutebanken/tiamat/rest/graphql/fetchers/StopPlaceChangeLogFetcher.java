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
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import org.rutebanken.tiamat.repository.search.ChangedStopPlaceSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service("stopPlaceChangelogFetcher")
@Transactional
class StopPlaceChangeLogFetcher implements DataFetcher {

    //TODO: Update Stopplace changelog fetcher

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Override
    @Transactional
    public Object get(DataFetchingEnvironment environment) {

        var from = ZonedDateTime.of(2019,10,24,0,0,0,0, ZoneId.systemDefault()).toInstant();
        var to = ZonedDateTime.of(2019,10,25,0,0,0,0, ZoneId.systemDefault()).toInstant();


        final ChangedStopPlaceSearch changedStopPlaceSearch = new ChangedStopPlaceSearch(from,to, null);

        List<StopPlaceChangelogDto> stopPlaceChangelogDtoList = stopPlaceRepository.findStopPlaceChangelog(changedStopPlaceSearch);

                return getStopPlaces(environment,stopPlaceChangelogDtoList,stopPlaceChangelogDtoList.size());

    }

    private PageImpl<StopPlaceChangelogDto> getStopPlaces(DataFetchingEnvironment environment, List<StopPlaceChangelogDto> stopPlaces, long size) {
        return new PageImpl<>(stopPlaces, PageRequest.of(10, 10), size);
    }

}
