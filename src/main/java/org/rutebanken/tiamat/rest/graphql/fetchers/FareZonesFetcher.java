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

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.exporter.params.FareZoneSearch;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FARE_ZONES_AUTHORITY_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FARE_ZONES_SCOPING_METHOD;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FARE_ZONES_ZONE_TOPOLOGY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PAGE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.QUERY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SIZE;

@Service("fareZonesFetcher")
@Transactional
public class FareZonesFetcher implements DataFetcher {

    @Autowired
    private FareZoneRepository fareZoneRepository;

    @Override
    @Transactional
    public Object get(DataFetchingEnvironment environment) {
        String netexId = environment.getArgument(ID);

        if (netexId != null) {
            final Optional<FareZone> validFareZone = fareZoneRepository.findValidFareZone(netexId);
            if (validFareZone.isPresent()) {
                return Collections.singletonList(validFareZone.get());
            }
        }

        FareZoneSearch fareZoneSearch = FareZoneSearch.newFareZoneSearchBuilder()
                .query(environment.getArgument(QUERY))
                .authorityRef(environment.getArgument(FARE_ZONES_AUTHORITY_REF))
                .scopingMethod(environment.getArgument(FARE_ZONES_SCOPING_METHOD))
                .zoneTopology(environment.getArgument(FARE_ZONES_ZONE_TOPOLOGY))
                .build();

        List<FareZone> fareZones = fareZoneRepository.findFareZones(fareZoneSearch);
        return new PageImpl<>(fareZones, PageRequest.of(environment.getArgument(PAGE), environment.getArgument(SIZE)), fareZones.size());
    }
}
