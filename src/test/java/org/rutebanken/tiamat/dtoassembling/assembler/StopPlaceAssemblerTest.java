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

        StopPlaceAssembler stopPlaceAssembler = new StopPlaceAssembler(mock(PointAssembler.class), mock(QuayAssembler.class));

        String municipalityName = "Asker";

        String id = "123";
        TopographicPlace municipality = topographicPlace(id, municipalityName);

        StopPlace stopPlace = stopPlaceWithTopographicPlace(municipality);

        when(topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(municipality.getNetexId())).thenReturn(municipality);

        List<TopographicPlace> topographicPlaces = new ArrayList<>(Arrays.asList(municipality));
        when(topographicPlaceRepository.findByNameValueAndCountryRefRefAndTopographicPlaceType(municipalityName, IanaCountryTldEnumeration.NO, TopographicPlaceTypeEnumeration.TOWN)).thenReturn(topographicPlaces);

        StopPlaceDto stopPlaceDto = stopPlaceAssembler.assembleMunicipalityAndCounty(new StopPlaceDto(), stopPlace);

        assertThat(stopPlaceDto.municipality).isEqualTo(municipalityName);
    }

    @Test
    public void testAssembleMunicipalityWithCounty() throws Exception {

        TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);
        StopPlaceAssembler stopPlaceAssembler = new StopPlaceAssembler(mock(PointAssembler.class), mock(QuayAssembler.class));

        String municipalityName = "Bærum";

        String id = "124";
        TopographicPlace municipality = topographicPlace(id, municipalityName);

        StopPlace stopPlace = stopPlaceWithTopographicPlace(municipality);

        when(topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(municipality.getNetexId())).thenReturn(municipality);

        String countyName = "Akershus";

        String id2 = "125";
        TopographicPlace county = topographicPlace(id2, countyName);

        municipality.setParentTopographicPlace(county);

        when(topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(county.getNetexId())).thenReturn(county);

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
        StopPlaceAssembler stopPlaceAssembler = new StopPlaceAssembler(mock(PointAssembler.class), mock(QuayAssembler.class));

        TopographicPlace municipality = new TopographicPlace();
        municipality.setNetexId("123");

        StopPlace stopPlace = stopPlaceWithTopographicPlace(municipality);

        when(topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(municipality.getNetexId())).thenReturn(municipality);

        stopPlaceAssembler.assembleMunicipalityAndCounty(new StopPlaceDto(), stopPlace);
    }

    @Test
    public void testAssembleMunicipalityNull() throws Exception {

        TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);
        StopPlaceAssembler stopPlaceAssembler = new StopPlaceAssembler(mock(PointAssembler.class), mock(QuayAssembler.class));

        TopographicPlace municipality = topographicPlace("123", "Municipality");

        StopPlace stopPlace = stopPlaceWithTopographicPlace(municipality);

        when(topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(municipality.getNetexId())).thenReturn(null);

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

    private TopographicPlace topographicPlace(String id, String name) {
        TopographicPlace municipality = new TopographicPlace();
        municipality.setNetexId(id);
        municipality.setName(new EmbeddableMultilingualString(name, ""));
        return municipality;
    }

}