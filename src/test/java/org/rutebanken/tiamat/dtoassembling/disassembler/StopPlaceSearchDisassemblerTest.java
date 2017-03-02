package org.rutebanken.tiamat.dtoassembling.disassembler;

import org.junit.Test;
import org.rutebanken.tiamat.dtoassembling.dto.StopPlaceSearchDto;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.StopPlaceSearch;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class StopPlaceSearchDisassemblerTest {

    private StopPlaceSearchDisassembler stopPlaceDisassembler = new StopPlaceSearchDisassembler(new NetexIdMapper());

    @Test
    public void disassembleIdList() {

        List<String> ids = Arrays.asList("NSR:StopPlace:123", "NSR:StopPlace:321");

        StopPlaceSearchDto stopPlaceSearchDto = new StopPlaceSearchDto.Builder()
                .setIdList(ids)
                .build();

        StopPlaceSearch stopPlaceSearch = stopPlaceDisassembler.disassemble(stopPlaceSearchDto);
        assertThat(stopPlaceSearch.getNetexIdList()).containsAll(ids);
    }

    @Test
    public void disassembleStopPlaceTypes() {
        StopPlaceSearchDto stopPlaceSearchDto = new StopPlaceSearchDto.Builder()
                .setStopPlaceTypes(Arrays.asList("onstreetBus")).build();
        StopPlaceSearch stopPlaceSearch = stopPlaceDisassembler.disassemble(stopPlaceSearchDto);
        assertThat(stopPlaceSearch.getStopTypeEnumerations()).contains(StopTypeEnumeration.ONSTREET_BUS);
    }

    @Test
    public void disassembleMunicipalityReference() {
        StopPlaceSearchDto stopPlaceSearchDto = new StopPlaceSearchDto.Builder()
                .setMunicipalityReferences(Arrays.asList("123")).build();
        StopPlaceSearch stopPlaceSearch = stopPlaceDisassembler.disassemble(stopPlaceSearchDto);
        assertThat(stopPlaceSearch.getMunicipalityIds()).contains("123");
    }
}