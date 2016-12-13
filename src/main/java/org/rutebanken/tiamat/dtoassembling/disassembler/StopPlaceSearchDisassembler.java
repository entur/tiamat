package org.rutebanken.tiamat.dtoassembling.disassembler;

import com.google.common.base.MoreObjects;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
import org.rutebanken.tiamat.rest.dto.DtoStopPlaceSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

        StopPlaceSearch stopPlaceSearch = new StopPlaceSearch();

        List<StopTypeEnumeration> stopTypeEnums = new ArrayList<>();
        if (dtoStopPlaceSearch.stopPlaceTypes != null) {
            dtoStopPlaceSearch.stopPlaceTypes.forEach(string ->
                    stopTypeEnums.add(StopTypeEnumeration.fromValue(string)));
            stopPlaceSearch.setStopTypeEnumerations(stopTypeEnums);
        }

        if(dtoStopPlaceSearch.idList != null) {
            stopPlaceSearch.setIdList(dtoStopPlaceSearch.idList.stream()
                    .filter(nsrId -> nsrId.startsWith(NetexIdMapper.NSR))
                    .map(nsrId -> netexIdMapper.extractLongAfterLastColon(nsrId))
                    .collect(Collectors.toList()));
        }
        stopPlaceSearch.setCountyIds(dtoStopPlaceSearch.countyReferences);
        stopPlaceSearch.setMunicipalityIds(dtoStopPlaceSearch.municipalityReferences);
        stopPlaceSearch.setQuery(dtoStopPlaceSearch.query);
        stopPlaceSearch.setPageable(new PageRequest(dtoStopPlaceSearch.page, dtoStopPlaceSearch.size));

        logger.info("Disassembled stop place search '{}'", stopPlaceSearch);

        return stopPlaceSearch;
    }

}
