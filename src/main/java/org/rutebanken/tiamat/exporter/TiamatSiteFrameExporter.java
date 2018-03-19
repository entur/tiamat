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

import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TiamatSiteFrameExporter {

    private static final Logger logger = LoggerFactory.getLogger(TiamatSiteFrameExporter.class);


    private final TopographicPlaceRepository topographicPlaceRepository;

    private final TariffZoneRepository tariffZoneRepository;

    private final PathLinkRepository pathLinkRepository;

    private final ExportTimeZone exportTimeZone;

    private final NetexIdHelper netexIdHelper;

    @Autowired
    public TiamatSiteFrameExporter(TopographicPlaceRepository topographicPlaceRepository, TariffZoneRepository tariffZoneRepository, PathLinkRepository pathLinkRepository, ExportTimeZone exportTimeZone, NetexIdHelper netexIdHelper) {
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.tariffZoneRepository = tariffZoneRepository;
        this.pathLinkRepository = pathLinkRepository;
        this.exportTimeZone = exportTimeZone;
        this.netexIdHelper = netexIdHelper;
    }


    public org.rutebanken.tiamat.model.SiteFrame createTiamatSiteFrame(String description) {
        org.rutebanken.tiamat.model.SiteFrame siteFrame = new org.rutebanken.tiamat.model.SiteFrame();
        setFrameDefaultLocale(siteFrame);
        siteFrame.setDescription(new MultilingualStringEntity(description));
        // siteFrame.setCreated(Instant.now()); // Disabled because of OffsetDateTimeInstantConverter issues during test
        siteFrame.setVersion(1L);
        siteFrame.setNetexId(netexIdHelper.getNetexId(siteFrame, siteFrame.hashCode()));
        return siteFrame;
    }

    public void addStopsToTiamatSiteFrame(org.rutebanken.tiamat.model.SiteFrame siteFrame, Iterable<StopPlace> iterableStopPlaces) {
        StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure = new StopPlacesInFrame_RelStructure();

        if (iterableStopPlaces != null) {
            iterableStopPlaces.forEach(stopPlace -> stopPlacesInFrame_relStructure.getStopPlace().add(stopPlace));
            logger.info("Adding {} stop places", stopPlacesInFrame_relStructure.getStopPlace().size());
            siteFrame.setStopPlaces(stopPlacesInFrame_relStructure);
            if (siteFrame.getStopPlaces().getStopPlace().isEmpty()) {
                siteFrame.setStopPlaces(null);
            }
        }
    }

    public void addAllTariffZones(org.rutebanken.tiamat.model.SiteFrame siteFrame) {
        addTariffZones(siteFrame, tariffZoneRepository.findAll());
    }

    public void addTariffZones(org.rutebanken.tiamat.model.SiteFrame siteFrame, List<TariffZone> tariffZones) {
        if (!tariffZones.isEmpty()) {
            siteFrame.setTariffZones(new TariffZonesInFrame_RelStructure(tariffZones));
            logger.info("Added {} tariffZones", tariffZones.size());
        } else {
            logger.info("No tariff zones found");
        }
    }

    public void addRelevantPathLinks(Set<Long> stopPlaceIds, SiteFrame siteFrame) {
        List<org.rutebanken.tiamat.model.PathLink> pathLinks = pathLinkRepository.findByStopPlaceIds(stopPlaceIds);
        if (!pathLinks.isEmpty()) {
            logger.info("Adding {} path links", pathLinks);
            siteFrame.setPathLinks(new org.rutebanken.tiamat.model.PathLinksInFrame_RelStructure());
            siteFrame.getPathLinks().getPathLink().addAll(pathLinks);
        } else {
            logger.info("There are no path links to export with the current filter");
        }
    }


    public void setFrameDefaultLocale(SiteFrame siteFrame) {

        LocaleStructure localeStructure = new LocaleStructure();
        localeStructure.setTimeZone(exportTimeZone.getDefaultTimeZoneId().toString());
        VersionFrameDefaultsStructure versionFrameDefaultsStructure = new VersionFrameDefaultsStructure();
        versionFrameDefaultsStructure.setDefaultLocale(localeStructure);
        siteFrame.setFrameDefaults(versionFrameDefaultsStructure);
    }

}
