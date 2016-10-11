package org.rutebanken.tiamat.dtoassembling.assembler;

import org.rutebanken.tiamat.dtoassembling.dto.CountyOrMunicipalityDto;
import org.rutebanken.tiamat.model.MultilingualString;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceRefStructure;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CountyOrMunicipalityAssemblerTest {

    @Test
    public void municipalityMustContainCountyReference() {

        TopographicPlace akershus = new TopographicPlace(new MultilingualString("Akershus"));
        akershus.setId(123456L);
        akershus.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);

        TopographicPlace asker = new TopographicPlace(new MultilingualString("Asker"));
        asker.setTopographicPlaceType(TopographicPlaceTypeEnumeration.TOWN);

        TopographicPlaceRefStructure akershusReference = new TopographicPlaceRefStructure();
        akershusReference.setRef(akershus.getId().toString());

        asker.setParentTopographicPlaceRef(akershusReference);

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