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

package org.rutebanken.tiamat.rest.graphql.mappers;

import org.rutebanken.tiamat.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ENTITY_REF_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.GROUP_OF_STOP_PLACES_MEMBERS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TARIFF_ZONES;

@Component
public class StopPlaceTariffZoneRefsMapper {

    public boolean populate(Map input, StopPlace stopPlace) {

        boolean isUpdated = false;

        if(input.get(TARIFF_ZONES) != null) {
            List refList = (List) input.get(TARIFF_ZONES);

            stopPlace.getTariffZones().clear();
            isUpdated = true;

            for(Object refObject : refList) {
                Map memberMap = (Map) refObject;
                String ref = (String) memberMap.get(ENTITY_REF_REF);
                stopPlace.getTariffZones().add(new TariffZoneRef(ref));
            }
        }

        return isUpdated;
    }

}
