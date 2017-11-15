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

import com.google.common.base.Strings;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.netex.model.TopographicPlacesInFrame_RelStructure;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.TopographicPlaceErasor;
import org.rutebanken.tiamat.importer.TopographicPlaceImporter;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TopographicPlaceImportHandler {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceImportHandler.class);

    private final PublicationDeliveryHelper publicationDeliveryHelper;

    private final NetexMapper netexMapper;

    private final TopographicPlaceImporter topographicPlaceImporter;

    private final TopographicPlaceErasor topographicPlaceErasor;

    public TopographicPlaceImportHandler(PublicationDeliveryHelper publicationDeliveryHelper,
                                         NetexMapper netexMapper,
                                         TopographicPlaceImporter topographicPlaceImporter,
                                         TopographicPlaceErasor topographicPlaceErasor) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.netexMapper = netexMapper;
        this.topographicPlaceImporter = topographicPlaceImporter;
        this.topographicPlaceErasor = topographicPlaceErasor;
    }

    public void handleTopographicPlaces(SiteFrame netexSiteFrame, ImportParams importParams, AtomicInteger topographicPlacesCounter, SiteFrame responseSiteframe) {

        if(!Strings.isNullOrEmpty(importParams.eraseTopographicPlaceWithIdPrefixAndType)) {
            logger.warn("Detected property eraseTopographicPlaceWithIdPrefixAndType: {}", importParams.eraseTopographicPlaceWithIdPrefixAndType);

            String[] splitted = importParams.eraseTopographicPlaceWithIdPrefixAndType.split(";");
            String idPrefix = splitted[0];
            String type = splitted[1];
            TopographicPlaceTypeEnumeration topographicPlaceType = TopographicPlaceTypeEnumeration.valueOf(type);
            topographicPlaceErasor.erase(idPrefix, topographicPlaceType);
        }


        if (publicationDeliveryHelper.hasTopographicPlaces(netexSiteFrame)) {
            logger.info("Publication delivery contains {} topographic places for import.", netexSiteFrame.getTopographicPlaces().getTopographicPlace().size());

            logger.info("About to map {} topographic places to internal model", netexSiteFrame.getTopographicPlaces().getTopographicPlace().size());
            List<org.rutebanken.tiamat.model.TopographicPlace> mappedTopographicPlaces = netexMapper.getFacade()
                    .mapAsList(netexSiteFrame.getTopographicPlaces().getTopographicPlace(),
                            org.rutebanken.tiamat.model.TopographicPlace.class);
            logger.info("Mapped {} topographic places to internal model", mappedTopographicPlaces.size());
            List<TopographicPlace> importedTopographicPlaces = topographicPlaceImporter.importTopographicPlaces(mappedTopographicPlaces, topographicPlacesCounter);
            responseSiteframe.withTopographicPlaces(new TopographicPlacesInFrame_RelStructure().withTopographicPlace(importedTopographicPlaces));
            logger.info("Finished importing topographic places");
        }
    }

}
