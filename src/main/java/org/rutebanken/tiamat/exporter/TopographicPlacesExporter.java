package org.rutebanken.tiamat.exporter;

import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.rutebanken.tiamat.exporter.params.ExportParams.ExportMode.ALL;
import static org.rutebanken.tiamat.exporter.params.ExportParams.ExportMode.RELEVANT;

@Transactional
@Component
public class TopographicPlacesExporter {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlacesExporter.class);

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    private NetexMapper netexMapper;

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
                    .collect(Collectors.toList());

            List<org.rutebanken.tiamat.model.TopographicPlace> counties = municipalities.stream()
                    .filter(municipality -> municipality.getParentTopographicPlaceRef() != null)
                    .map(municipality -> municipality.getParentTopographicPlaceRef())
                    .map(county -> topographicPlaceRepository.findFirstByNetexIdAndVersion(county.getRef(), Long.parseLong(county.getVersion())))
                    .distinct()
                    .collect(Collectors.toList());

            return Stream.of(counties, municipalities)
                    .flatMap(topographicPlaces -> topographicPlaces.stream())
                    .distinct()
                    .map(topographicPlace -> netexMapper.mapToNetexModel(topographicPlace))
                    .collect(Collectors.toList());
        }

        return null;
    }


    public void addTopographicPlacesToTiamatSiteFrame(ExportParams.ExportMode topographicPlaceExportMode, org.rutebanken.tiamat.model.SiteFrame siteFrame) {
        Collection<org.rutebanken.tiamat.model.TopographicPlace> topographicPlacesForExport = getTopographicPlacesForExport(topographicPlaceExportMode, siteFrame.getStopPlaces());

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
        if (ALL.equals(topographicPlaceExportMode)) {
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

    private void gatherTopographicPlaceTree(org.rutebanken.tiamat.model.TopographicPlace topographicPlace, Set<org.rutebanken.tiamat.model.TopographicPlace> target) {
        if (topographicPlace != null && target.add(topographicPlace)) {
            TopographicPlaceRefStructure parentRef = topographicPlace.getParentTopographicPlaceRef();
            if (parentRef != null) {
                org.rutebanken.tiamat.model.TopographicPlace parent = topographicPlaceRepository.findFirstByNetexIdAndVersion(parentRef.getRef(), Long.valueOf(parentRef.getVersion()));
                gatherTopographicPlaceTree(parent, target);
            }

        }
    }
}
