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

package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.model.vehicle.DeckPlan;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.DeckPlanRepository;
import org.rutebanken.tiamat.versioning.save.DeckPlanVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

@Transactional
@Component
public class DeckPlanImporter {

    private static final Logger logger = LoggerFactory.getLogger(DeckPlanImporter.class);

    private final NetexMapper netexMapper;

    private final DeckPlanRepository deckPlanRepository;

    private final DeckPlanVersionedSaverService deckPlanVersionedSaverService;

    @Autowired
    public DeckPlanImporter(NetexMapper netexMapper, DeckPlanRepository deckPlanRepository, DeckPlanVersionedSaverService deckPlanVersionedSaverService) {
        this.netexMapper = netexMapper;
        this.deckPlanRepository = deckPlanRepository;
        this.deckPlanVersionedSaverService = deckPlanVersionedSaverService;
    }

    public List<org.rutebanken.netex.model.DeckPlan> importDeckPlans(List<DeckPlan> deckPlans, AtomicInteger deckPlansCounter) {

        logger.info("Importing {} incoming deck plans", deckPlans.size());

        List<DeckPlan> result = new ArrayList<>();

        logger.info("Importing deck plans");
        for (DeckPlan incomingDeckPlan : deckPlans) {
            result.add(importDeckPlan(incomingDeckPlan, deckPlansCounter));
        }

        return result.stream().map(deckPlan -> netexMapper.mapToNetexModel(deckPlan)).collect(toList());

    }

    private DeckPlan importDeckPlan(DeckPlan incomingDeckPlan, AtomicInteger deckPlansCounter) {
        logger.debug("{}", incomingDeckPlan);
        incomingDeckPlan = deckPlanVersionedSaverService.saveNewVersion(incomingDeckPlan);

        deckPlansCounter.incrementAndGet();
        return incomingDeckPlan;
    }

}
