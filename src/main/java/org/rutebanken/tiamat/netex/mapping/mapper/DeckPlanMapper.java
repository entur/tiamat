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

package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.DeckPlan;
import org.rutebanken.netex.model.ObjectFactory;

public class DeckPlanMapper extends CustomMapper<DeckPlan, org.rutebanken.tiamat.model.vehicle.DeckPlan> {

    @Override
    public void mapAtoB(DeckPlan deckPlan, org.rutebanken.tiamat.model.vehicle.DeckPlan deckPlan2, MappingContext context) {
        super.mapAtoB(deckPlan, deckPlan2, context);
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.vehicle.DeckPlan tiamatDeckPlan, DeckPlan netexDeckPlan, MappingContext context) {
        super.mapBtoA(tiamatDeckPlan, netexDeckPlan, context);
    }
}
