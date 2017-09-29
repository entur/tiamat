/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.model;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Transactional
public class TopographicPlaceTest extends TiamatIntegrationTest {

    @Test
    public void stopPlaceShouldBeReferencingTopographicalPlace() {

        TopographicPlace nedreEiker = new TopographicPlace();
        nedreEiker.setName(new EmbeddableMultilingualString("Nedre Eiker", "no"));

        topographicPlaceRepository.save(nedreEiker);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Steinberg", "no"));
        stopPlace.setTopographicPlace(nedreEiker);

        stopPlaceRepository.save(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());
        assertThat(actualStopPlace.getTopographicPlace()).isNotNull();
        assertThat(actualStopPlace.getTopographicPlace().getNetexId()).isEqualTo(nedreEiker.getNetexId());
        assertThat(actualStopPlace.getTopographicPlace().getName()).isEqualTo(nedreEiker.getName());
    }

    @Test
    public void topographicPlacesShouldBeNestable() {

        // County
        TopographicPlace buskerud = new TopographicPlace();
        buskerud.setName(new EmbeddableMultilingualString("Buskerud", "no"));

        topographicPlaceRepository.save(buskerud);

        // Municipality
        TopographicPlace nedreEiker = new TopographicPlace();
        nedreEiker.setName(new EmbeddableMultilingualString("Nedre Eiker", "no"));
        nedreEiker.setParentTopographicPlaceRef(new TopographicPlaceRefStructure(buskerud));

        topographicPlaceRepository.save(nedreEiker);

        TopographicPlace actualNedreEiker = topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(nedreEiker.getNetexId());

        assertThat(actualNedreEiker).isNotNull();
        assertThat(actualNedreEiker.getParentTopographicPlaceRef()).isNotNull();
        assertThat(actualNedreEiker.getParentTopographicPlaceRef().getRef()).isEqualTo(buskerud.getNetexId());

    }

    @Test
    public void topographicPlacesShouldBePartOfCountry() {

        CountryRef countryRef = new CountryRef();
        countryRef.setRef(IanaCountryTldEnumeration.NO);

        TopographicPlace akershus = new TopographicPlace();
        akershus.setName(new EmbeddableMultilingualString("Akershus", "no"));
        akershus.setCountryRef(countryRef);

        topographicPlaceRepository.save(akershus);

        TopographicPlace actual = topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(akershus.getNetexId());
        assertThat(actual.getCountryRef()).isNotNull();
        assertThat(actual.getCountryRef().getRef()).isEqualTo(IanaCountryTldEnumeration.NO);
    }

}