package org.rutebanken.tiamat.exporter;

import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@Component
public class TopographicPlacesExporter {

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

}
