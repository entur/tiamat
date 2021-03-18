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

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PAGE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.QUERY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SIZE;

import java.util.List;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.exporter.params.TariffZoneSearch;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("tariffZonesFetcher")
@Transactional
public class TariffZonesFetcher implements DataFetcher<Page<TariffZone>> {

    private static final Logger logger = LoggerFactory.getLogger(TariffZonesFetcher.class);

    @Autowired
    private TariffZoneRepository tariffZoneRepository;

    @Override
    @Transactional
    public Page<TariffZone> get(DataFetchingEnvironment environment) {

        TariffZoneSearch tariffZoneSearch = TariffZoneSearch.newTariffZoneSearchBuilder()
                .query(environment.getArgument(QUERY))
                .build();

        List<TariffZone> allTariffZones = tariffZoneRepository.findTariffZones(tariffZoneSearch);
        int pageSize = environment.getArgument(SIZE);
        PagedList<TariffZone> pagedList = new PagedList<>(allTariffZones, pageSize);
        List<TariffZone> currentPage = pagedList.getPage(environment.getArgument(PAGE));
        return new PageImpl<>(currentPage, PageRequest.of(environment.getArgument(PAGE), pageSize), currentPage.size());
    }
}
