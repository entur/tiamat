package org.rutebanken.tiamat.dtoassembling.disassembler;

import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
import org.rutebanken.tiamat.rest.dto.DtoStopPlaceSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StopPlaceSearchDisassembler {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceSearchDisassembler.class);

    private NetexIdMapper netexIdMapper;

    public StopPlaceSearchDisassembler(NetexIdMapper netexIdMapper) {
        this.netexIdMapper = netexIdMapper;
    }

    public StopPlaceSearch disassemble(DtoStopPlaceSearch dtoStopPlaceSearch) {

        StopPlaceSearch.Builder stopPlaceSearchBuilder = new StopPlaceSearch.Builder();

        List<StopTypeEnumeration> stopTypeEnums = new ArrayList<>();
        if (dtoStopPlaceSearch.stopPlaceTypes != null) {
            dtoStopPlaceSearch.stopPlaceTypes.forEach(string ->
                    stopTypeEnums.add(StopTypeEnumeration.fromValue(string)));
            stopPlaceSearchBuilder.setStopTypeEnumerations(stopTypeEnums);
        }

        if(dtoStopPlaceSearch.idList != null) {
            stopPlaceSearchBuilder.setIdList(dtoStopPlaceSearch.idList.stream()
                    .filter(nsrId -> nsrId.startsWith(NetexIdMapper.NSR))
                    .map(nsrId -> netexIdMapper.extractLongAfterLastColon(nsrId))
                    .collect(Collectors.toList()));
        }
        stopPlaceSearchBuilder.setCountyIds(dtoStopPlaceSearch.countyReferences);
        stopPlaceSearchBuilder.setMunicipalityIds(dtoStopPlaceSearch.municipalityReferences);
        stopPlaceSearchBuilder.setQuery(dtoStopPlaceSearch.query);
        stopPlaceSearchBuilder.setPageable(new PageRequest(dtoStopPlaceSearch.page, dtoStopPlaceSearch.size));

        StopPlaceSearch stopPlaceSearch = stopPlaceSearchBuilder.build();
        logger.info("Disassembled stop place search '{}'", stopPlaceSearch);

        return stopPlaceSearch;
    }

}
