package org.rutebanken.tiamat.dtoassembling.disassembler;

import org.junit.Test;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
import org.rutebanken.tiamat.rest.dto.DtoStopPlaceSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class StopPlaceSearchDisassemblerTest {

    private StopPlaceSearchDisassembler stopPlaceDisassembler = new StopPlaceSearchDisassembler(new NetexIdMapper());

    @Test
    public void disassembleIdList() {

        List<String> ids = Arrays.asList("NSR:StopPlace:123", "NSR:Quay:321");

        DtoStopPlaceSearch dtoStopPlaceSearch = new DtoStopPlaceSearch.Builder()
                .setIdList(ids)
                .build();

        StopPlaceSearch stopPlaceSearch = stopPlaceDisassembler.disassemble(dtoStopPlaceSearch);
        assertThat(stopPlaceSearch.getIdList()).contains(123L, 321L);
    }

    @Test
    public void disassembleStopPlaceTypes() {
        DtoStopPlaceSearch dtoStopPlaceSearch = new DtoStopPlaceSearch.Builder()
                .setStopPlaceTypes(Arrays.asList("onstreetBus")).build();
        StopPlaceSearch stopPlaceSearch = stopPlaceDisassembler.disassemble(dtoStopPlaceSearch);
        assertThat(stopPlaceSearch.getStopTypeEnumerations()).contains(StopTypeEnumeration.ONSTREET_BUS);
    }

    @Test
    public void disassembleMunicipalityReference() {
        DtoStopPlaceSearch dtoStopPlaceSearch = new DtoStopPlaceSearch.Builder()
                .setMunicipalityReferences(Arrays.asList("123")).build();
        StopPlaceSearch stopPlaceSearch = stopPlaceDisassembler.disassemble(dtoStopPlaceSearch);
        assertThat(stopPlaceSearch.getMunicipalityIds()).contains("123");
    }
}