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

package org.rutebanken.tiamat.repository;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.TopographicPlaceSearch;
import org.rutebanken.tiamat.model.*;
import org.junit.Test;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class TopographicPlaceRepositoryTest extends TiamatIntegrationTest {

    @Autowired
    private GeometryFactory geometryFactory;

    @Test
    public void findByTopographicPlaceAndCountry() {

        CountryRef countryRef = new CountryRef();
        countryRef.setRef(IanaCountryTldEnumeration.NO);

        TopographicPlace akershus = new TopographicPlace();
        akershus.setName(new EmbeddableMultilingualString("Akershus", "no"));
        akershus.setCountryRef(countryRef);
        akershus.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);

        topographicPlaceRepository.save(akershus);

        List<TopographicPlace> places = topographicPlaceRepository.findByNameValueAndCountryRefRefAndTopographicPlaceType("Akershus", IanaCountryTldEnumeration.NO, TopographicPlaceTypeEnumeration.COUNTY);
        assertThat(places).extracting(IdentifiedEntity::getNetexId).contains(akershus.getNetexId());
    }

    @Test
    public void findTopographicPlaceFromPoint() {
        TopographicPlace somePlace = new TopographicPlace();
        somePlace.setName(new EmbeddableMultilingualString("Some place", "no"));

        // Create polygon
        Geometry geometry =  geometryFactory.createPoint(new Coordinate(9.84, 59.26)).buffer(20);
        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(geometry.getCoordinates()), geometryFactory);
        somePlace.setPolygon(geometryFactory.createPolygon(linearRing, null));

        topographicPlaceRepository.save(somePlace);

        List<TopographicPlace> matches = topographicPlaceRepository.findByPoint(geometryFactory.createPoint(new Coordinate(9.84, 59.2643)));

        assertThat(matches).hasSize(1);
    }


    @Test
    public void findCurrentlyValidTopographicPlace() {

        Instant testStarted  = Instant.now();

        TopographicPlace validNow = new TopographicPlace();
        validNow.setValidBetween(new ValidBetween(testStarted.minusSeconds(200)));
        validNow.setName(new EmbeddableMultilingualString("Topographic place valid now", "no"));

        topographicPlaceRepository.save(validNow);

        TopographicPlace validInThePast = new TopographicPlace();
        validInThePast.setValidBetween(new ValidBetween(testStarted.minusSeconds(2000), testStarted.minusSeconds(200)));
        topographicPlaceRepository.save(validInThePast);

        TopographicPlace validInTheFuture = new TopographicPlace();
        validInTheFuture.setValidBetween(new ValidBetween(testStarted.plusSeconds(2000)));
        topographicPlaceRepository.save(validInTheFuture);

        List<TopographicPlace> matches = topographicPlaceRepository.findTopographicPlace(
                TopographicPlaceSearch
                        .newTopographicPlaceSearchBuilder()
                        .versionValidity(ExportParams.VersionValidity.CURRENT)
                        .build());

        assertThat(matches).hasSize(1);
    }

    @Test
    public void findCurrentAndFutureValidTopographicPlace() {

        Instant testStarted  = Instant.now();

        TopographicPlace validNow = new TopographicPlace();
        validNow.setValidBetween(new ValidBetween(testStarted.minusSeconds(200)));
        validNow.setName(new EmbeddableMultilingualString("Topographic place valid now", "no"));
        topographicPlaceRepository.save(validNow);

        TopographicPlace validInThePast = new TopographicPlace();
        validInThePast.setValidBetween(new ValidBetween(testStarted.minusSeconds(2000), testStarted.minusSeconds(200)));
        topographicPlaceRepository.save(validInThePast);

        TopographicPlace validInTheFuture = new TopographicPlace();
        validInTheFuture.setValidBetween(new ValidBetween(testStarted.plusSeconds(2000)));
        topographicPlaceRepository.save(validInTheFuture);

        List<TopographicPlace> matches = topographicPlaceRepository.findTopographicPlace(
                TopographicPlaceSearch
                        .newTopographicPlaceSearchBuilder()
                        .versionValidity(ExportParams.VersionValidity.CURRENT_FUTURE)
                        .build());

        assertThat(matches).hasSize(2);
    }
}