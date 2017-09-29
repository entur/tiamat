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

package org.rutebanken.tiamat.exporter;

import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.*;
import org.rutebanken.tiamat.repository.search.ChangedStopPlaceSearch;
import org.rutebanken.tiamat.service.stopplace.ParentStopPlacesFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toSet;
import static org.rutebanken.tiamat.model.VersionOfObjectRefStructure.ANY_VERSION;

@Component
@Transactional
public class PublicationDeliveryExporter {
    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryExporter.class);
    private final StopPlaceRepository stopPlaceRepository;
    private final NetexMapper netexMapper;
    private final TiamatSiteFrameExporter tiamatSiteFrameExporter;
    private final TopographicPlacesExporter topographicPlacesExporter;
    private final TariffZonesFromStopsExporter tariffZonesFromStopsExporter;
    private final ParentStopPlacesFetcher parentStopPlacesFetcher;

    @Autowired
    public PublicationDeliveryExporter(StopPlaceRepository stopPlaceRepository,
                                       NetexMapper netexMapper, TiamatSiteFrameExporter tiamatSiteFrameExporter, TopographicPlacesExporter topographicPlacesExporter, TariffZonesFromStopsExporter tariffZonesFromStopsExporter, ParentStopPlacesFetcher parentStopPlacesFetcher) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.netexMapper = netexMapper;
        this.tiamatSiteFrameExporter = tiamatSiteFrameExporter;
        this.topographicPlacesExporter = topographicPlacesExporter;
        this.tariffZonesFromStopsExporter = tariffZonesFromStopsExporter;
        this.parentStopPlacesFetcher = parentStopPlacesFetcher;
    }

    @Transactional(readOnly = true)
    public PublicationDeliveryStructure exportStopPlaces(ExportParams exportParams) {
        return exportPublicationDeliveryWithStops(stopPlaceRepository.findStopPlace(exportParams).getContent(), exportParams);
    }

    @Transactional(readOnly = true)
    public PublicationDeliveryStructurePage exportStopPlacesWithEffectiveChangeInPeriod(ChangedStopPlaceSearch search, ExportParams exportParams) {
        logger.info("Finding changed stop places with search params: {}", search);
        Page<StopPlace> stopPlacePage = stopPlaceRepository.findStopPlacesWithEffectiveChangeInPeriod(search);
        logger.debug("Found {} changed stop places", stopPlacePage.getSize());
        PublicationDeliveryStructurePage publicationDeliveryStructure = new PublicationDeliveryStructurePage(exportPublicationDeliveryWithStops(stopPlacePage.getContent(), exportParams), stopPlacePage.getSize(), stopPlacePage.getTotalElements(), stopPlacePage.hasNext());
        logger.debug("Returning publication delivery structure: {}", publicationDeliveryStructure);
        return publicationDeliveryStructure;
    }

    public PublicationDeliveryStructure createPublicationDelivery() {
        PublicationDeliveryStructure publicationDeliveryStructure = new PublicationDeliveryStructure()
                .withVersion(ANY_VERSION)
                .withPublicationTimestamp(OffsetDateTime.now())
                .withParticipantRef(NetexIdHelper.NSR);
        return publicationDeliveryStructure;
    }

    @SuppressWarnings("unchecked")
    public PublicationDeliveryStructure createPublicationDelivery(org.rutebanken.netex.model.SiteFrame siteFrame) {
        PublicationDeliveryStructure publicationDeliveryStructure = createPublicationDelivery();
        publicationDeliveryStructure.withDataObjects(
                new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame)));

        logger.info("Returning publication delivery {} with site frame", publicationDeliveryStructure);
        return publicationDeliveryStructure;
    }

    public PublicationDeliveryStructure exportPublicationDeliveryWithStops(List<StopPlace> stopPlaces, ExportParams exportParams) {
        logger.info("Preparing publication delivery export");

        stopPlaces = parentStopPlacesFetcher.resolveParents(stopPlaces, true);

        org.rutebanken.tiamat.model.SiteFrame siteFrame = tiamatSiteFrameExporter.createTiamatSiteFrame("Site frame with stops");
        tiamatSiteFrameExporter.addStopsToTiamatSiteFrame(siteFrame, stopPlaces);
        topographicPlacesExporter.addTopographicPlacesToTiamatSiteFrame(exportParams.getTopographicPlaceExportMode(), siteFrame);

        boolean relevantTariffZones = ExportParams.ExportMode.RELEVANT.equals(exportParams.getTariffZoneExportMode());

        if(!relevantTariffZones && ExportParams.ExportMode.ALL.equals(exportParams.getTariffZoneExportMode())) {
            tiamatSiteFrameExporter.addAllTariffZones(siteFrame);
        }

        Set<Long> stopPlaceIds = StreamSupport.stream(stopPlaces.spliterator(), false).map(stopPlace -> stopPlace.getId()).collect(toSet());
        tiamatSiteFrameExporter.addRelevantPathLinks(stopPlaceIds, siteFrame);

        logger.info("Mapping site frame to netex model");
        org.rutebanken.netex.model.SiteFrame convertedSiteFrame = netexMapper.mapToNetexModel(siteFrame);

        if(convertedSiteFrame.getStopPlaces() != null) {
            if (relevantTariffZones) {
                tariffZonesFromStopsExporter.resolveTariffZones(convertedSiteFrame.getStopPlaces().getStopPlace(), convertedSiteFrame);
            } else if (ExportParams.ExportMode.NONE.equals(exportParams.getTariffZoneExportMode())) {
                logger.info("TariffZone export mode is NONE. Removing references from {} converted stop places", convertedSiteFrame.getStopPlaces().getStopPlace().size());
                convertedSiteFrame.getStopPlaces().getStopPlace().stream()
                        .forEach(convertedStop -> convertedStop.setTariffZones(null));
            }
        }

        if (ExportParams.ExportMode.NONE.equals(exportParams.getTopographicPlaceExportMode())){
            removeVersionFromTopographicPlaceReferences(convertedSiteFrame);
        }

        return createPublicationDelivery(convertedSiteFrame);
    }

    private void removeVersionFromTopographicPlaceReferences(org.rutebanken.netex.model.SiteFrame convertedSiteFrame) {
        if (convertedSiteFrame.getStopPlaces() != null) {
            convertedSiteFrame.getStopPlaces().getStopPlace()
                    .stream()
                    .filter(sp -> sp.getTopographicPlaceRef() != null)
                    .forEach(sp -> sp.getTopographicPlaceRef().setVersion(null));
        }
    }

}
