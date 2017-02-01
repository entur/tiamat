package org.rutebanken.tiamat.importer.finder;

import org.junit.Test;
import org.rutebanken.tiamat.importer.finder.TopographicPlaceFromRefFinder;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceRefStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class TopographicPlaceFromRefFinderTest {

    private TopographicPlaceFromRefFinder topographicPlaceFromRefFinder = new TopographicPlaceFromRefFinder();

    @Test
    public void findTopographicPlaceFromListWhenPresent() throws Exception {
        List<TopographicPlace> topographicPlacelist = new ArrayList<>();

        Long id = 22L;

        TopographicPlaceRefStructure topographicPlaceRef = new TopographicPlaceRefStructure();
        topographicPlaceRef.setRef(String.valueOf(id));

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setId(id);

        topographicPlacelist.add(topographicPlace);

        Optional<TopographicPlace> actual = topographicPlaceFromRefFinder.findTopographicPlaceFromRef(topographicPlacelist, topographicPlaceRef);
        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(id);
    }

    @Test
    public void findTopographicPlaceFromListWhenEmpty() throws Exception {
        List<TopographicPlace> topographicPlacelist = new ArrayList<>();

        String id = "1";

        TopographicPlaceRefStructure topographicPlaceRef = new TopographicPlaceRefStructure();
        topographicPlaceRef.setRef(id);

        Optional<TopographicPlace> actual = topographicPlaceFromRefFinder.findTopographicPlaceFromRef(topographicPlacelist, topographicPlaceRef);
        assertThat(actual).isEmpty();
    }

}