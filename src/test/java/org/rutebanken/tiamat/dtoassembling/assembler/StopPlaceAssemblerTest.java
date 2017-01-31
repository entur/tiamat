package org.rutebanken.tiamat.dtoassembling.assembler;

import org.junit.Test;
import org.rutebanken.tiamat.dtoassembling.dto.StopPlaceDto;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StopPlaceAssemblerTest {

    @Test
    public void testAssembleMunicipality() throws Exception {

        TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);

        StopPlaceAssembler stopPlaceAssembler = new StopPlaceAssembler(mock(PointAssembler.class), topographicPlaceRepository, mock(QuayAssembler.class));

        String municipalityName = "Asker";

        Long id = 123L;
        TopographicPlace municipality = topographicPlace(id, municipalityName);

        StopPlace stopPlace = stopPlaceWithTopographicPlace(municipality);

        when(topographicPlaceRepository.findOne(Long.valueOf(municipality.getId()))).thenReturn(municipality);

        List<TopographicPlace> topographicPlaces = new ArrayList<>(Arrays.asList(municipality));
        when(topographicPlaceRepository.findByNameValueAndCountryRefRefAndTopographicPlaceType(municipalityName, IanaCountryTldEnumeration.NO, TopographicPlaceTypeEnumeration.TOWN)).thenReturn(topographicPlaces);

        StopPlaceDto stopPlaceDto = stopPlaceAssembler.assembleMunicipalityAndCounty(new StopPlaceDto(), stopPlace);

        assertThat(stopPlaceDto.municipality).isEqualTo(municipalityName);
    }

    @Test
    public void testAssembleMunicipalityWithCounty() throws Exception {

        TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);
        StopPlaceAssembler stopPlaceAssembler = new StopPlaceAssembler(mock(PointAssembler.class), topographicPlaceRepository, mock(QuayAssembler.class));

        String municipalityName = "Bærum";

        Long id = 124L;
        TopographicPlace municipality = topographicPlace(id, municipalityName);

        StopPlace stopPlace = stopPlaceWithTopographicPlace(municipality);

        when(topographicPlaceRepository.findOne(Long.valueOf(municipality.getId()))).thenReturn(municipality);

        String countyName = "Akershus";

        Long id2 = 125L;
        TopographicPlace county = topographicPlace(id2, countyName);

        municipality.setParentTopographicPlace(county);

        when(topographicPlaceRepository.findOne(county.getId())).thenReturn(county);

        List<TopographicPlace> municipalities = new ArrayList<>(Arrays.asList(municipality));
        when(topographicPlaceRepository.findByNameValueAndCountryRefRefAndTopographicPlaceType(municipalityName, IanaCountryTldEnumeration.NO, TopographicPlaceTypeEnumeration.TOWN)).thenReturn(municipalities);

        List<TopographicPlace> counties = new ArrayList<>(Arrays.asList(county));
        when(topographicPlaceRepository.findByNameValueAndCountryRefRefAndTopographicPlaceType(countyName, IanaCountryTldEnumeration.NO, TopographicPlaceTypeEnumeration.COUNTY)).thenReturn(counties);


        StopPlaceDto stopPlaceDto = stopPlaceAssembler.assembleMunicipalityAndCounty(new StopPlaceDto(), stopPlace);

        assertThat(stopPlaceDto.county).isEqualTo(countyName);
    }

    @Test
    public void testAssembleMunicipalityNullName() throws Exception {

        TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);
        StopPlaceAssembler stopPlaceAssembler = new StopPlaceAssembler(mock(PointAssembler.class), topographicPlaceRepository, mock(QuayAssembler.class));

        TopographicPlace municipality = new TopographicPlace();
        municipality.setId(1L);

        StopPlace stopPlace = stopPlaceWithTopographicPlace(municipality);

        when(topographicPlaceRepository.findOne(Long.valueOf(municipality.getId()))).thenReturn(municipality);

        stopPlaceAssembler.assembleMunicipalityAndCounty(new StopPlaceDto(), stopPlace);
    }

    @Test
    public void testAssembleMunicipalityNull() throws Exception {

        TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);
        StopPlaceAssembler stopPlaceAssembler = new StopPlaceAssembler(mock(PointAssembler.class), topographicPlaceRepository, mock(QuayAssembler.class));

        TopographicPlace municipality = topographicPlace(123, "Municipality");

        StopPlace stopPlace = stopPlaceWithTopographicPlace(municipality);

        when(topographicPlaceRepository.findOne(municipality.getId())).thenReturn(null);

        stopPlaceAssembler.assembleMunicipalityAndCounty(new StopPlaceDto(), stopPlace);
    }


    private StopPlace stopPlaceWithTopographicPlace(TopographicPlace topographicPlace) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setTopographicPlace(topographicPlace);
        return stopPlace;
    }

    private TopographicPlaceRefStructure topographicRef(String ref) {
        TopographicPlaceRefStructure topographicPlaceRefStructure = new TopographicPlaceRefStructure();
        topographicPlaceRefStructure.setRef(ref);
        return topographicPlaceRefStructure;
    }

    private TopographicPlace topographicPlace(long id, String name) {
        TopographicPlace municipality = new TopographicPlace();
        municipality.setId(id);
        municipality.setName(new EmbeddableMultilingualString(name, ""));
        return municipality;
    }

}