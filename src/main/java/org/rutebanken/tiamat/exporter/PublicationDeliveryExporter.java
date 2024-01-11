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

import jakarta.xml.bind.JAXBElement;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.PurposeOfGrouping;
import org.rutebanken.netex.model.ResourceFrame;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.search.ChangedStopPlaceSearch;
import org.rutebanken.tiamat.service.stopplace.ChildStopPlacesFetcher;
import org.rutebanken.tiamat.service.stopplace.ParentStopPlacesFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toSet;

/**
 * This class should be removed.
 * The reason is that we have two ways of exporting betex (sync and async) in Tiamat, but we want to maintain only one, for reduced complexity.
 * So, the regular synchronous export has been pointed at @{{@link StreamingPublicationDelivery}}, which also is used for async export.
 * The remeaining code to migrate is ChangedStopPlaceSearch and the ability to fetch children as @{{@link ChildStopPlacesFetcher}}.
 */
@Component
@Transactional
@Deprecated
public class PublicationDeliveryExporter {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryExporter.class);
    private final String publicationDeliveryId;
    private final StopPlaceRepository stopPlaceRepository;
    private final NetexMapper netexMapper;
    private final TiamatSiteFrameExporter tiamatSiteFrameExporter;
    private final TiamatServiceFrameExporter tiamatServiceFrameExporter;
    private final TopographicPlacesExporter topographicPlacesExporter;
    private final TariffZonesFromStopsExporter tariffZonesFromStopsExporter;
    private final ParentStopPlacesFetcher parentStopPlacesFetcher;
    private final ChildStopPlacesFetcher childStopPlacesFetcher;
    private final ValidPrefixList validPrefixList;

    public enum MultiModalFetchMode {CHILDREN, PARENTS}

    @Autowired
    public PublicationDeliveryExporter(@Value("${netex.profile.version:1.12:NO-NeTEx-stops:1.4}") String publicationDeliveryId,
                                       StopPlaceRepository stopPlaceRepository,
                                       NetexMapper netexMapper,
                                       TiamatSiteFrameExporter tiamatSiteFrameExporter,
                                       TiamatServiceFrameExporter tiamatServiceFrameExporter,
                                       TopographicPlacesExporter topographicPlacesExporter,
                                       TariffZonesFromStopsExporter tariffZonesFromStopsExporter,
                                       ParentStopPlacesFetcher parentStopPlacesFetcher,
                                       ChildStopPlacesFetcher childStopPlacesFetcher,
                                       ValidPrefixList validPrefixList) {
        this.publicationDeliveryId = publicationDeliveryId;
        this.stopPlaceRepository = stopPlaceRepository;
        this.netexMapper = netexMapper;
        this.tiamatSiteFrameExporter = tiamatSiteFrameExporter;
        this.tiamatServiceFrameExporter = tiamatServiceFrameExporter;
        this.topographicPlacesExporter = topographicPlacesExporter;
        this.tariffZonesFromStopsExporter = tariffZonesFromStopsExporter;
        this.parentStopPlacesFetcher = parentStopPlacesFetcher;
        this.childStopPlacesFetcher = childStopPlacesFetcher;
        this.validPrefixList = validPrefixList;
    }

    @Transactional(readOnly = true)
    public PublicationDeliveryStructurePage exportStopPlacesWithEffectiveChangeInPeriod(ChangedStopPlaceSearch search, ExportParams exportParams) {
        logger.info("Finding changed stop places with search params: {}", search);
        Page<StopPlace> stopPlacePage = stopPlaceRepository.findStopPlacesWithEffectiveChangeInPeriod(search);
        logger.debug("Found {} changed stop places", stopPlacePage.getSize());

        PublicationDeliveryStructure publicationDelivery = exportPublicationDeliveryWithStops(stopPlacePage.getContent(), exportParams, MultiModalFetchMode.CHILDREN);

        PublicationDeliveryStructurePage publicationDeliveryStructure = new PublicationDeliveryStructurePage(
                publicationDelivery,
                stopPlacePage.getSize(),
                stopPlacePage.getTotalElements(),
                stopPlacePage.hasNext());
        logger.debug("Returning publication delivery structure: {}", publicationDeliveryStructure);
        return publicationDeliveryStructure;
    }

    public PublicationDeliveryStructure createPublicationDelivery() {
        return new PublicationDeliveryStructure()
                .withVersion(publicationDeliveryId)
                .withPublicationTimestamp(LocalDateTime.now())
                .withParticipantRef(validPrefixList.getValidNetexPrefix());
    }

    @SuppressWarnings("unchecked")
    public PublicationDeliveryStructure createPublicationDelivery(org.rutebanken.netex.model.SiteFrame siteFrame) {
        PublicationDeliveryStructure publicationDeliveryStructure = createPublicationDelivery();
        publicationDeliveryStructure.withDataObjects(
                new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame))
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createResourceFrame(createResourceFrame()))
        );

        logger.info("Returning publication delivery {} with site frame", publicationDeliveryStructure);
        return publicationDeliveryStructure;
    }

    @SuppressWarnings("unchecked")
    public PublicationDeliveryStructure createPublicationDelivery(org.rutebanken.netex.model.SiteFrame siteFrame, ResourceFrame netexResourceFrame) {
        PublicationDeliveryStructure publicationDeliveryStructure = createPublicationDelivery();
        publicationDeliveryStructure.withDataObjects(
                new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame))
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createResourceFrame(netexResourceFrame))
        );

        logger.info("Returning publication delivery {} with site frame", publicationDeliveryStructure);
        return publicationDeliveryStructure;
    }

    private ResourceFrame createResourceFrame() {
        List<JAXBElement<? extends DataManagedObjectStructure>> purposeOfGroupingList = new ArrayList<>();
        final PurposeOfGrouping purposeOfGrouping = new ObjectFactory().createPurposeOfGrouping().withId("NSR:PurposeOfGrouping:3").withName(new MultilingualString().withValue("generalization")).withVersion("1");
        final JAXBElement<PurposeOfGrouping> purposeOfGrouping2= new ObjectFactory().createPurposeOfGrouping(purposeOfGrouping);
        purposeOfGroupingList.add(purposeOfGrouping2);


        return new ResourceFrame().withId("NSR:RescourceFrame:1").withVersion("1")
                .withTypesOfValue(new ObjectFactory()
                                                    .createTypesOfValueInFrame_RelStructure().withValueSetOrTypeOfValue(purposeOfGroupingList ));
    }

    @SuppressWarnings("unchecked")
    public PublicationDeliveryStructure createPublicationDelivery(org.rutebanken.netex.model.SiteFrame siteFrame,
                                                                  org.rutebanken.netex.model.FareFrame fareFrame,
                                                                  ResourceFrame netexResourceFrame) {
        PublicationDeliveryStructure publicationDeliveryStructure = createPublicationDelivery();
        publicationDeliveryStructure.withDataObjects(
                new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame))
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createFareFrame(fareFrame))
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createResourceFrame(netexResourceFrame))
        );

        logger.info("Returning publication delivery {} with site frame and fare frame", publicationDeliveryStructure);
        return publicationDeliveryStructure;
    }



    @SuppressWarnings("unchecked")
    public PublicationDeliveryStructure createPublicationDelivery(org.rutebanken.netex.model.SiteFrame siteFrame,
                                                                  org.rutebanken.netex.model.ServiceFrame serviceFrame,
                                                                  org.rutebanken.netex.model.FareFrame fareFrame,
                                                                  ResourceFrame netexResourceFrame
    ) {
        PublicationDeliveryStructure publicationDeliveryStructure = createPublicationDelivery();

        publicationDeliveryStructure.withDataObjects
                (
                new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createServiceFrame(serviceFrame))
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame))
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createFareFrame(fareFrame))
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createResourceFrame(netexResourceFrame))
                );

        logger.info("Returning publication delivery {} with site frame and  service frame", publicationDeliveryStructure);
        return publicationDeliveryStructure;
    }
    /**
     *
     * @param stopPlaces
     * @param exportParams
     * @param multiModalFetchMode if parents or children should be fetched
     * @return
     */
    public PublicationDeliveryStructure exportPublicationDeliveryWithStops(List<StopPlace> stopPlaces, ExportParams exportParams, MultiModalFetchMode multiModalFetchMode) {
        logger.info("Preparing publication delivery export");

        if(multiModalFetchMode == null) {
            multiModalFetchMode = MultiModalFetchMode.PARENTS;
        }

        if(multiModalFetchMode.equals(MultiModalFetchMode.CHILDREN)) {
            stopPlaces = childStopPlacesFetcher.resolveChildren(stopPlaces);
        } else if( multiModalFetchMode.equals(MultiModalFetchMode.PARENTS)){
            stopPlaces = parentStopPlacesFetcher.resolveParents(stopPlaces, true);
        }


        org.rutebanken.tiamat.model.SiteFrame siteFrame = tiamatSiteFrameExporter.createTiamatSiteFrame("Site frame with stops");

        tiamatSiteFrameExporter.addStopsToTiamatSiteFrame(siteFrame, stopPlaces);
        topographicPlacesExporter.addTopographicPlacesToTiamatSiteFrame(exportParams.getTopographicPlaceExportMode(), siteFrame);

        boolean relevantTariffZones = ExportParams.ExportMode.RELEVANT.equals(exportParams.getTariffZoneExportMode());

        if (!relevantTariffZones && ExportParams.ExportMode.ALL.equals(exportParams.getTariffZoneExportMode())) {
            tiamatSiteFrameExporter.addAllTariffZones(siteFrame);
        }

        Set<Long> stopPlaceIds = StreamSupport.stream(stopPlaces.spliterator(), false).map(stopPlace -> stopPlace.getId()).collect(toSet());
        tiamatSiteFrameExporter.addRelevantPathLinks(stopPlaceIds, siteFrame);

        logger.info("Mapping site frame to netex model");
        org.rutebanken.netex.model.SiteFrame convertedSiteFrame = netexMapper.mapToNetexModel(siteFrame);

        if (convertedSiteFrame.getStopPlaces() != null) {
            if (relevantTariffZones) {
                tariffZonesFromStopsExporter.resolveTariffZones(convertedSiteFrame.getStopPlaces().getStopPlace(), convertedSiteFrame);
            } else if (ExportParams.ExportMode.NONE.equals(exportParams.getTariffZoneExportMode())) {
                logger.info("TariffZone export mode is NONE. Removing references from {} converted stop places", convertedSiteFrame.getStopPlaces().getStopPlace().size());
                convertedSiteFrame.getStopPlaces().getStopPlace().stream()
                        .forEach(convertedStop -> convertedStop.setTariffZones(null));
            }
        }

        if (ExportParams.ExportMode.NONE.equals(exportParams.getTopographicPlaceExportMode())) {
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
