package org.rutebanken.tiamat.repository;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.AddressablePlaceRefStructure;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class ReferenceResolverTest extends TiamatIntegrationTest {

    @Autowired
    private ReferenceResolver referenceResolver;

    @Test
    public void testResolveStopPlace() {

        StopPlace stopPlace = stopPlaceRepository.save(new StopPlace());

        StopPlace actual = referenceResolver.resolve(new AddressablePlaceRefStructure(stopPlace));

        assertThat(actual.getNetexId()).isEqualTo(stopPlace.getNetexId());
        assertThat(actual.getVersion()).isEqualTo(stopPlace.getVersion());
    }

    @Test
    public void testResolveQuay() {

        Quay quay = quayRepository.save(new Quay());

        Quay actual = referenceResolver.resolve(new AddressablePlaceRefStructure(quay));

        assertThat(actual.getNetexId()).isEqualTo(quay.getNetexId());
        assertThat(actual.getVersion()).isEqualTo(quay.getVersion());
    }
}