package org.rutebanken.tiamat.dtoassembling.assembler;

import org.junit.Test;
import org.rutebanken.tiamat.dtoassembling.dto.CountyOrMunicipalityDto;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CountyOrMunicipalityAssemblerTest {

    @Test
    public void municipalityMustContainCountyReference() {

        TopographicPlace akershus = new TopographicPlace(new EmbeddableMultilingualString("Akershus"));
        akershus.setId(123456L);
        akershus.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);

        TopographicPlace asker = new TopographicPlace(new EmbeddableMultilingualString("Asker"));
        asker.setTopographicPlaceType(TopographicPlaceTypeEnumeration.TOWN);

        asker.setParentTopographicPlace(akershus);

        List<TopographicPlace> topographicPlaces = new ArrayList<>();
        topographicPlaces.add(asker);
        topographicPlaces.add(akershus);

        List<CountyOrMunicipalityDto> assembledPlaces = new CountyOrMunicipalityAssembler().assemble(topographicPlaces);

        assertThat(assembledPlaces).isNotEmpty();
        assertThat(assembledPlaces).hasSize(2);
        assertThat(assembledPlaces).extracting("name").contains("Akershus", "Asker");
        assertThat(assembledPlaces).extracting("county").contains("Akershus");

    }


}