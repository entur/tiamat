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

package org.rutebanken.tiamat.importer.handler;

import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.importer.DeckPlanImporter;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.converter.DeckPlanIdConverter;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DeckPlanImportHandler {

    private static final Logger logger = LoggerFactory.getLogger(DeckPlanImportHandler.class);

    private final PublicationDeliveryHelper publicationDeliveryHelper;

    private final NetexMapper netexMapper;

    private final DeckPlanImporter deckPlanImporter;
    private final DeckPlanIdConverter deckPlanIdConverter;

    public DeckPlanImportHandler(PublicationDeliveryHelper publicationDeliveryHelper,
                                 NetexMapper netexMapper,
                                 DeckPlanImporter deckPlanImporter, DeckPlanIdConverter deckPlanIdConverter) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.netexMapper = netexMapper;
        this.deckPlanImporter = deckPlanImporter;
        this.deckPlanIdConverter = deckPlanIdConverter;
    }

    public void handleDeckPlans(ResourceFrame netexResourceFrame, ImportParams importParams, AtomicInteger deckPlansCounter, ResourceFrame responseResourceframe) {

        if (publicationDeliveryHelper.hasDeckPlans(netexResourceFrame)) {
            var originalDeckPlans = netexResourceFrame.getDeckPlans().getDeckPlan();
            logger.info("Publication delivery contains {} deck plans for import.", originalDeckPlans.size());

            logger.info("About to check if incoming deck plans have previously been imported with the same id");
            var originalWithMappedIds = originalDeckPlans.stream()
                    .map(deckPlanIdConverter::convertIncomingId)
                    .toList();

            logger.info("About to map {} deck plans to internal model", netexResourceFrame.getDeckPlans().getDeckPlan().size());
            List<org.rutebanken.tiamat.model.vehicle.DeckPlan> mappedDeckPlans = netexMapper.getFacade()
                    .mapAsList(originalWithMappedIds,
                            org.rutebanken.tiamat.model.vehicle.DeckPlan.class);
            logger.info("Mapped {} deck plans to internal model", mappedDeckPlans.size());
            List<DeckPlan> importedDeckPlans = deckPlanImporter.importDeckPlans(mappedDeckPlans, deckPlansCounter);

            responseResourceframe.withDeckPlans(
                    new DeckPlans_RelStructure()
                            .withDeckPlan(importedDeckPlans));

            logger.info("Finished importing deck plans");
        }
    }
}
