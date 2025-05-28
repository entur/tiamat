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

import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.tiamat.model.TopographicPlaceRefStructure;
import org.rutebanken.tiamat.model.TopographicPlacesInFrame;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.rutebanken.tiamat.exporter.params.ExportParams.ExportMode.ALL;
import static org.rutebanken.tiamat.exporter.params.ExportParams.ExportMode.RELEVANT;

@Transactional
@Component
public class TopographicPlacesExporter {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlacesExporter.class);

    private final TopographicPlaceRepository topographicPlaceRepository;
    private final NetexMapper netexMapper;

    @Autowired
    public TopographicPlacesExporter(TopographicPlaceRepository topographicPlaceRepository, NetexMapper netexMapper) {
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.netexMapper = netexMapper;
    }

    /**
     * Finds topographic places and parent topographic places from NetexId and version.
     * @param netexIdRefs List of netexIds and versions to receive.
     * @return list of topographic places.
     */
    public List<TopographicPlace> export(List<Pair<String, Long>> netexIdRefs) {


        if(netexIdRefs != null) {
            List<org.rutebanken.tiamat.model.TopographicPlace> municipalities = netexIdRefs.stream()
                    .filter(netexIdRef -> netexIdRef.getFirst() != null)
                    .filter(netexIdRef -> netexIdRef.getSecond() != null)
                    .distinct()
                    .map(netexIdRef -> topographicPlaceRepository.findFirstByNetexIdAndVersion(netexIdRef.getFirst(), netexIdRef.getSecond()))
                    .toList();

            List<org.rutebanken.tiamat.model.TopographicPlace> counties = municipalities.stream()
                    .filter(municipality -> municipality.getParentTopographicPlaceRef() != null)
                    .map(municipality -> municipality.getParentTopographicPlaceRef())
                    .map(county -> topographicPlaceRepository.findFirstByNetexIdAndVersion(county.getRef(), Long.parseLong(county.getVersion())))
                    .distinct()
                    .toList();

            return Stream.of(counties, municipalities)
                    .flatMap(topographicPlaces -> topographicPlaces.stream())
                    .distinct()
                    .map(topographicPlace -> netexMapper.mapToNetexModel(topographicPlace))
                    .toList();
        }

        return null;
    }

    public void addTopographicPlacesToTiamatSiteFrame(ExportParams.ExportMode topographicPlaceExportMode, org.rutebanken.tiamat.model.SiteFrame siteFrame) {
        Collection<org.rutebanken.tiamat.model.TopographicPlace> topographicPlacesForExport = getTopographicPlacesForExport(topographicPlaceExportMode, siteFrame.getStopPlaces());
        addTopographicPlacesToTiamatSiteFrame(topographicPlacesForExport, siteFrame);
    }

    // TODO: Handle allVersions attribute
    public void addTopographicPlacesToTiamatSiteFrame(Collection<org.rutebanken.tiamat.model.TopographicPlace> topographicPlacesForExport, org.rutebanken.tiamat.model.SiteFrame siteFrame) {
        if (!topographicPlacesForExport.isEmpty()) {
            Iterator<org.rutebanken.tiamat.model.TopographicPlace> topographicPlaceIterable = topographicPlacesForExport.iterator();

            TopographicPlacesInFrame topographicPlaces = new TopographicPlacesInFrame();
            topographicPlaceIterable
                    .forEachRemaining(topographicPlace -> topographicPlaces.getTopographicPlace().add(topographicPlace));

            logger.info("Adding {} topographic places", topographicPlaces.getTopographicPlace().size());
            siteFrame.setTopographicPlaces(topographicPlaces);
        } else {
            siteFrame.setTopographicPlaces(null);
        }
    }

    private Collection<org.rutebanken.tiamat.model.TopographicPlace> getTopographicPlacesForExport(ExportParams.ExportMode topographicPlaceExportMode, StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure) {
        Collection<org.rutebanken.tiamat.model.TopographicPlace> topographicPlacesForExport;
        if (ALL.equals(topographicPlaceExportMode) || topographicPlaceExportMode == null) {
            topographicPlacesForExport = topographicPlaceRepository.findAll();
            if (topographicPlacesForExport.isEmpty()) {
                logger.warn("No topographic places found to export");
            }
        } else if (RELEVANT.equals(topographicPlaceExportMode) && stopPlacesInFrame_relStructure != null) {
            Set<org.rutebanken.tiamat.model.TopographicPlace> uniqueTopographicPlaces = new HashSet<>();
            for (StopPlace stopPlace : stopPlacesInFrame_relStructure.getStopPlace()) {
                gatherTopographicPlaceTree(stopPlace.getTopographicPlace(), uniqueTopographicPlaces);
            }

            topographicPlacesForExport = new HashSet<>(uniqueTopographicPlaces);
        } else {
            topographicPlacesForExport = new ArrayList<>();
        }
        return topographicPlacesForExport;
    }

    public void gatherTopographicPlaceTree(org.rutebanken.tiamat.model.TopographicPlace topographicPlace, Set<org.rutebanken.tiamat.model.TopographicPlace> target) {
        if (topographicPlace != null && target.add(topographicPlace)) {
            TopographicPlaceRefStructure parentRef = topographicPlace.getParentTopographicPlaceRef();
            if (parentRef != null) {
                org.rutebanken.tiamat.model.TopographicPlace parent = topographicPlaceRepository.findFirstByNetexIdAndVersion(parentRef.getRef(), Long.valueOf(parentRef.getVersion()));
                gatherTopographicPlaceTree(parent, target);
            }

        }
    }
}
