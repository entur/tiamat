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

import com.google.common.collect.Sets;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceRefStructure;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.model.Value;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.rutebanken.tiamat.model.tag.Tag;
import org.rutebanken.tiamat.repository.search.ChangedStopPlaceSearch;
import org.rutebanken.tiamat.service.stopplace.MultiModalStopPlaceEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.rutebanken.tiamat.exporter.params.ExportParams.newExportParamsBuilder;
import static org.rutebanken.tiamat.exporter.params.StopPlaceSearch.newStopPlaceSearchBuilder;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.MERGED_ID_KEY;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

@Transactional
public class StopPlaceRepositoryImplTest extends TiamatIntegrationTest {

    @Autowired
    private MultiModalStopPlaceEditor multiModalStopPlaceEditor;

    @Autowired
    private TagRepository tagRepository;

    private Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    @Test
    public void scrollableResult() throws InterruptedException {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("new stop place to be saved and scrolled back"));
        stopPlace.setNetexId("NSR:StopPlace:123");
        stopPlace.getKeyValues().put("key", new Value("value"));

        Quay quay = new Quay(new EmbeddableMultilingualString("Quay"));
        stopPlace.getQuays().add(quay);

        stopPlace = stopPlaceRepository.save(stopPlace);
        stopPlaceRepository.flush();

        Iterator<StopPlace> iterator = stopPlaceRepository.scrollStopPlaces();
        assertThat(iterator.hasNext()).isTrue();
        StopPlace actual = iterator.next();
        assertThat(actual.getNetexId()).isEqualTo(stopPlace.getNetexId());
        assertThat(iterator.hasNext()).isFalse();

    }

    @Test
    public void findStopPlaceFromKeyValue() {
        StopPlace stopPlace = new StopPlace();

        stopPlace.getKeyValues().put("key", new Value("value"));
        stopPlaceRepository.save(stopPlace);

        String netexId = stopPlaceRepository.findFirstByKeyValues("key", Sets.newHashSet("value"));
        StopPlace actual = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(netexId);
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getKeyValues()).containsKey("key");

        Assertions.assertThat(actual.getKeyValues().get("key").getItems()).contains("value");
    }

    @Test
    public void keyValuesForAddressablePlaceNoMixup() {
        Quay quay = new Quay();
        quay.getOrCreateValues("key").add("value");

        quayRepository.save(quay);

        StopPlace stopPlace = new StopPlace();
        stopPlace.getOrCreateValues("key").add("value");
        stopPlaceRepository.save(stopPlace);


        String netexId = stopPlaceRepository.findFirstByKeyValues("key", Sets.newHashSet("value"));
        assertThat(netexId).isEqualTo(stopPlace.getNetexId());
        StopPlace actual = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(netexId);
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getKeyValues()).containsKey("key");

        Assertions.assertThat(actual.getKeyValues().get("key").getItems()).contains("value");
    }


    @Test
    public void noStopPlaceFromKeyValue() {
        StopPlace firstStopPlace = new StopPlace();
        firstStopPlace.getKeyValues().put("key", new Value("value"));
        stopPlaceRepository.save(firstStopPlace);

        String id = stopPlaceRepository.findFirstByKeyValues("key", Sets.newHashSet("anotherValue"));
        assertThat(id).isNull();
    }

    @Test
    public void findCorrectStopPlaceFromKeyValue() {
        StopPlace anotherStopPlaceWithAnotherValue = new StopPlace();
        anotherStopPlaceWithAnotherValue.getKeyValues().put("key", new Value("anotherValue"));
        stopPlaceRepository.save(anotherStopPlaceWithAnotherValue);

        StopPlace matchingStopPlace = new StopPlace();
        matchingStopPlace.getKeyValues().put("key", new Value("value"));
        stopPlaceRepository.save(matchingStopPlace);

        String id = stopPlaceRepository.findFirstByKeyValues("key", Sets.newHashSet("value"));

        assertThat(id).isEqualTo(matchingStopPlace.getNetexId());

        StopPlace actual = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(id);
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getKeyValues()).containsKey("key");
        Assertions.assertThat(actual.getKeyValues().get("key").getItems()).contains("value");
    }

    @Test
    public void findCorrectStopPlaceFromValues() {
        StopPlace stopPlaceWithSomeValues = new StopPlace();
        stopPlaceWithSomeValues.getKeyValues().put("key", new Value("One value", "Second value", "Third value"));
        stopPlaceRepository.save(stopPlaceWithSomeValues);

        String id = stopPlaceRepository.findFirstByKeyValues("key", Sets.newHashSet("Third value"));

        assertThat(id).isEqualTo(stopPlaceWithSomeValues.getNetexId());

        StopPlace actual = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(id);
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getKeyValues()).containsKey("key");
        Assertions.assertThat(actual.getKeyValues().get("key").getItems()).contains("Third value");
    }

    @Test
    public void findStopPlacesWithin() throws Exception {

        double southEastLatitude = 59.875649;
        double southEastLongitude = 10.500340;

        double northWestLatitude = 59.875924;
        double northWestLongitude = 10.500699;

        StopPlace stopPlace = createStopPlace(59.875679, 10.500430);
        stopPlaceRepository.save(stopPlace);

        Pageable pageable = PageRequest.of(0, 10);

        Page<StopPlace> result = stopPlaceRepository.findStopPlacesWithin(southEastLongitude, southEastLatitude, northWestLongitude, northWestLatitude, null, pageable);
        assertThat(result.getContent()).extracting(EntityStructure::getNetexId).contains(stopPlace.getNetexId());
    }

    @Test
    public void findStopPlacesWithinParent() throws Exception {

        double southEastLatitude = 59.875649;
        double southEastLongitude = 10.500340;

        double northWestLatitude = 59.875924;
        double northWestLongitude = 10.500699;

        StopPlace parent = createStopPlace(59.875679, 10.500430);
        parent.setParentStopPlace(true);
        stopPlaceRepository.save(parent);

        StopPlace child = new StopPlace();
        child.setParentStopPlace(false);
        child.setParentSiteRef(new SiteRefStructure(parent.getNetexId(), String.valueOf(parent.getVersion())));
        stopPlaceRepository.save(child);

        Pageable pageable = PageRequest.of(0, 10);

        Page<StopPlace> result = stopPlaceRepository.findStopPlacesWithin(southEastLongitude, southEastLatitude, northWestLongitude, northWestLatitude, null, pageable);
        assertThat(result.getContent()).extracting(EntityStructure::getNetexId).contains(child.getNetexId());
    }

    @Test
    public void findStopPlacesWithinMaxVersion() throws Exception {

        double southEastLatitude = 59.875649;
        double southEastLongitude = 10.500340;

        double northWestLatitude = 59.875924;
        double northWestLongitude = 10.500699;

        StopPlace version1 = createStopPlace(59.875679, 10.500430);
        version1.setVersion(1L);
        version1.setNetexId("NSR:StopPlace:977777");
        version1.setValidBetween(new ValidBetween(Instant.EPOCH, now.minusMillis(1000000)));
        StopPlace version2 = createStopPlace(59.875679, 10.500430);
        version2.setVersion(2L);
        version2.setNetexId(version1.getNetexId());
        version2.setValidBetween(new ValidBetween(now.minusMillis(1000001), now.plusMillis(1000000)));
        stopPlaceRepository.save(version1);
        stopPlaceRepository.save(version2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<StopPlace> result = stopPlaceRepository.findStopPlacesWithin(southEastLongitude, southEastLatitude, northWestLongitude, northWestLatitude, null, pageable);
        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().getVersion()).isEqualTo(version2.getVersion());

    }

    @Test
    public void findStopPlaceWithinNoStopsInBoundingBox() throws Exception {
        double southEastLatitude = 59.875649;
        double southEastLongitude = 10.500340;

        double northWestLatitude = 59.875924;
        double northWestLongitude = 10.500699;

        // Outside boundingBox
        StopPlace stopPlace = createStopPlace(60.00, 11);
        Pageable pageable = PageRequest.of(0, 10);

        stopPlaceRepository.save(stopPlace);

        Page<StopPlace> result = stopPlaceRepository.findStopPlacesWithin(southEastLongitude, southEastLatitude, northWestLongitude, northWestLatitude, null, pageable);

        assertThat(result.getContent()).extracting(IdentifiedEntity::getNetexId).isEmpty();
    }

    @Test
    public void findStopPlaceWithinIgnoringStopPlace() throws Exception {
        double southEastLatitude = 59;
        double southEastLongitude = 10;

        double northWestLatitude = 60;
        double northWestLongitude = 11;

        StopPlace stopPlace = createStopPlace(59.5, 10.5);
        Pageable pageable = PageRequest.of(0, 10);

        stopPlaceRepository.save(stopPlace);

        Page<StopPlace> result = stopPlaceRepository.findStopPlacesWithin(southEastLongitude, southEastLatitude, northWestLongitude, northWestLatitude, stopPlace.getNetexId(), pageable);

        assertThat(result.getContent())
                .extracting(IdentifiedEntity::getNetexId)
                .as("Ignored stop place shall not be part of the result")
                .isEmpty();
    }

    @Test
    public void findStopPlacesWithinIgnoringStopPlaceButOtherShouldMatch() throws Exception {

        double southEastLatitude = 59;
        double southEastLongitude = 10;

        double northWestLatitude = 60;
        double northWestLongitude = 11;

        StopPlace ignoredStopPlace = createStopPlace(59.5, 10.5);
        stopPlaceRepository.save(ignoredStopPlace);

        StopPlace otherStopPlace = createStopPlace(59.5, 10.5);
        stopPlaceRepository.save(otherStopPlace);

        Pageable pageable = PageRequest.of(0, 10);

        Page<StopPlace> result = stopPlaceRepository.findStopPlacesWithin(southEastLongitude, southEastLatitude, northWestLongitude, northWestLatitude, ignoredStopPlace.getNetexId(), pageable);

        assertThat(result.getContent())
                .extracting(IdentifiedEntity::getNetexId)
                .as("Ignored stop place shall not be part of the result")
                .doesNotContain(ignoredStopPlace.getNetexId())
                .contains(otherStopPlace.getNetexId());
    }


    @Test
    public void findStopPlacesWithinIncludeExpiredVersions() throws Exception {

        double xMin = 10.1;
        double yMin = 59.1;

        double xMax = 10.9;
        double yMax = 59.9;

        StopPlace expiredStopPlace = createStopPlace(59.3, 10.5);

        ValidBetween expiredValidBetween = new ValidBetween(now.minusSeconds(1000), now.minusSeconds(100));
        expiredStopPlace.setValidBetween(expiredValidBetween);

        StopPlace openEndedStopPlace = createStopPlace(59.4, 10.6);
        ValidBetween openEndedValidBetween = new ValidBetween(expiredValidBetween.getToDate());
        openEndedStopPlace.setValidBetween(openEndedValidBetween);

        expiredStopPlace = stopPlaceRepository.save(expiredStopPlace);
        openEndedStopPlace = stopPlaceRepository.save(openEndedStopPlace);

        Pageable pageable = PageRequest.of(0, 10);

        Page<StopPlace> result = stopPlaceRepository.findStopPlacesWithin(xMin, yMin, xMax, yMax, null, pageable);
        assertThat(result).hasSize(1);
        // Default behaviour should omit expired StopPlaces - i.e. only return openEndedStopPlace
        assertThat(result.getContent().getFirst().getNetexId())
                .isEqualTo(openEndedStopPlace.getNetexId());


        Instant pointInTime = expiredValidBetween.getFromDate().plusSeconds(1);
        Page<StopPlace> expirySearchResult = stopPlaceRepository.findStopPlacesWithin(xMin, yMin, xMax, yMax, null, pointInTime, pageable);
        assertThat(expirySearchResult).hasSize(1); // timestamp set to *before* openEndedStopPlace

        assertThat(expirySearchResult.getContent().getFirst().getNetexId())
                .isEqualTo(expiredStopPlace.getNetexId());
    }


    @Test
    public void findNearbyStopPlace() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("name", ""));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);

        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.500430, 59.875679)));
        stopPlaceRepository.save(stopPlace);

        Envelope envelope = new Envelope(10.500340, 59.875649, 10.500699, 59.875924);

        String result = stopPlaceRepository.findNearbyStopPlace(envelope, stopPlace.getName().getValue(), StopTypeEnumeration.ONSTREET_BUS);
        assertThat(result).isNotNull();
        StopPlace actual = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(result);
        assertThat(actual.getName().getValue()).isEqualTo(stopPlace.getName().getValue());
    }

    @Test
    public void findNearbyStopPlaceFuzzyMatch() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Nesbru nord", ""));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);

        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.500430, 59.875679)));
        stopPlaceRepository.save(stopPlace);

        Envelope envelope = new Envelope(10.500340, 59.875649, 10.500699, 59.875924);

        String result = stopPlaceRepository.findNearbyStopPlace(envelope, "Nesbru No", StopTypeEnumeration.ONSTREET_BUS);
        assertThat(result).isNotNull();
        StopPlace actual = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(result);
        assertThat(actual.getName().getValue()).isEqualTo(stopPlace.getName().getValue());
    }

    @Test
    public void noNearbyStopPlace() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("stop place", ""));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(15, 60)));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlaceRepository.save(stopPlace);

        Envelope envelope = new Envelope(10.500340, 59.875649, 10.500699, 59.875924);

        String result = stopPlaceRepository.findNearbyStopPlace(envelope, stopPlace.getName().getValue(), StopTypeEnumeration.ONSTREET_BUS);
        assertThat(result).isNull();
    }

    @Test
    public void noNearbyStopPlaceIfNameIsDifferent() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("This name is different", ""));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(15, 60)));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlaceRepository.save(stopPlace);


        // Stop place coordinates within envelope
        Envelope envelope = new Envelope(14, 16, 50, 70);

        String result = stopPlaceRepository.findNearbyStopPlace(envelope, "Another stop place which does not exist", StopTypeEnumeration.ONSTREET_BUS);
        assertThat(result).isNull();

    }

    @Test
    public void multipleNearbyStopPlaces() throws Exception {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("name"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(15, 60)));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        StopPlace stopPlace2 = new StopPlace(new EmbeddableMultilingualString("name"));
        stopPlace2.setCentroid(geometryFactory.createPoint(new Coordinate(15.0001, 60.0002)));

        stopPlaceRepository.save(stopPlace);
        stopPlaceRepository.save(stopPlace2);

        // Stop place coordinates within envelope
        Envelope envelope = new Envelope(14, 16, 50, 70);

        String result = stopPlaceRepository.findNearbyStopPlace(envelope, "name", StopTypeEnumeration.ONSTREET_BUS);
        assertThat(result).isNotNull();
    }

    @Test
    public void findStopPlaceByMunicipalityAndTypeBusThenExpectNoResult() {
        String stopPlaceName = "Falsens plass";
        String municipalityName = "Gjøvik";
        TopographicPlace municipality = createMunicipality(municipalityName, null);
        StopPlace stopPlace = createStopPlaceWithMunicipality(stopPlaceName, municipality);
        stopPlace.setStopPlaceType(StopTypeEnumeration.AIRPORT);
        stopPlaceRepository.save(stopPlace);

        List<StopTypeEnumeration> stopTypeEnumerations = Arrays.asList(StopTypeEnumeration.BUS_STATION);

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(newExportParamsBuilder()
                .setStopPlaceSearch(newStopPlaceSearchBuilder()
                        .setQuery(stopPlaceName)
                        .setStopTypeEnumerations(stopTypeEnumerations)
                        .build())
                .setMunicipalityReference(stopPlace.getTopographicPlace().getNetexId())
                .build());

        assertThat(result).isEmpty();
    }

    @Test
    public void findStopPlaceByMunicipalityAndName() throws Exception {
        String stopPlaceName = "Nesbru";
        String municipalityName = "Asker";
        TopographicPlace municipality = createMunicipality(municipalityName, null);
        StopPlace stopPlace = createStopPlaceWithMunicipality(stopPlaceName, municipality);
        stopPlaceRepository.save(stopPlace);

        ExportParams exportParams = newExportParamsBuilder()
                .setStopPlaceSearch(newStopPlaceSearchBuilder()
                        .setQuery(stopPlaceName)
                        .setVersionValidity(ExportParams.VersionValidity.ALL)
                        .build())
                .setMunicipalityReference(stopPlace.getTopographicPlace().getNetexId())
                .build();
        Page<StopPlace> result = stopPlaceRepository.findStopPlace(exportParams);
        assertThat(result).isNotEmpty();
        System.out.println(result.getContent().getFirst());
    }

    @Test
    public void searchingForThreeLettersMustOnlyReturnNamesStartingWithLetters() throws Exception {
        StopPlace nesbru = createStopPlaceWithMunicipality("Nesbru", null);
        StopPlace bru = createStopPlaceWithMunicipality("Bru", null);

        stopPlaceRepository.save(nesbru);
        stopPlaceRepository.save(bru);


        ExportParams exportParams = newExportParamsBuilder()
                .setStopPlaceSearch(newStopPlaceSearchBuilder()
                        .setQuery("bru")
                        .setVersionValidity(ExportParams.VersionValidity.ALL)
                        .build())
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(exportParams);
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).extracting(stop -> stop.getName().getValue())
                .contains(bru.getName().getValue())
                .doesNotContain(nesbru.getName().getValue());
    }

    @Test
    public void searchingForMoreThanThreeLettersMustReturnNamesContainingLetters() throws Exception {

        StopPlace nesset = createStopPlaceWithMunicipality("Nesset", null);
        StopPlace brunesset = createStopPlaceWithMunicipality("Brunesset", null);

        stopPlaceRepository.save(nesset);
        stopPlaceRepository.save(brunesset);

        ExportParams exportParams = newExportParamsBuilder().setStopPlaceSearch(newStopPlaceSearchBuilder()
                .setQuery("nesset")
                .setVersionValidity(ExportParams.VersionValidity.ALL)
                .build())
                .build();
        Page<StopPlace> result = stopPlaceRepository.findStopPlace(exportParams);
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).extracting(stop -> stop.getName().getValue())
                .contains(brunesset.getName().getValue())
                .contains(nesset.getName().getValue());
    }

    @Test
    public void findStopPlaceByMunicipalityCountyAndName() throws Exception {
        String stopPlaceName = "Bergerveien";
        String municipalityName = "Asker";
        String countyName = "Akershus";

        TopographicPlace county = createCounty(countyName);
        TopographicPlace municipality = createMunicipality(municipalityName, county);
        createStopPlaceWithMunicipality(stopPlaceName, municipality);

        ExportParams exportParams = newExportParamsBuilder()
                .setMunicipalityReference(municipality.getNetexId())
                .setCountyReference(county.getNetexId())
                .setStopPlaceSearch(newStopPlaceSearchBuilder()
                        .setQuery(stopPlaceName)
                        .setVersionValidity(ExportParams.VersionValidity.ALL)
                        .build())
                .build();
        Page<StopPlace> result = stopPlaceRepository.findStopPlace(exportParams);
        assertThat(result).isNotEmpty();
        System.out.println(result.getContent().getFirst());
    }

    @Test
    public void findStopPlaceByCountyAndName() throws Exception {
        String stopPlaceName = "IKEA Slependen";
        String municipalityName = "Asker";
        String countyName = "Akershus";

        TopographicPlace county = createCounty(countyName);
        TopographicPlace municipality = createMunicipality(municipalityName, county);
        createStopPlaceWithMunicipality(stopPlaceName, municipality);

        ExportParams exportParams = newExportParamsBuilder()
                .setCountyReference(county.getNetexId())
                .setStopPlaceSearch(newStopPlaceSearchBuilder()
                        .setQuery(stopPlaceName)
                        .setVersionValidity(ExportParams.VersionValidity.ALL)
                        .build())
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(exportParams);

        assertThat(result).isNotEmpty();
        System.out.println(result.getContent().getFirst());
    }

    @Test
    public void findStopPlacesByVersion() {

        //Searching for StopPlaces with arguments {page=0, size=50, allVersions=true, id=null, version=null, stopPlaceType=null, countyReference=null, tags=null, municipalityReference=null, query=NSR:StopPlace:6505, importedId=null, pointInTime=null, key=null, withoutLocationOnly=false, withoutQuaysOnly=false, withDuplicatedQuayImportedIds=false, withNearbySimilarDuplicates=false, values=null, withTags=false, code=null}

        StopPlace v1 = new StopPlace(new EmbeddableMultilingualString("v1"));
        v1.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        v1.setVersion(1L);

        Instant yesterday = now.minus(1, ChronoUnit.DAYS);
        v1.setValidBetween(new ValidBetween(Instant.EPOCH, yesterday));
        stopPlaceRepository.save(v1);

        StopPlace v2 = new StopPlace(new EmbeddableMultilingualString("v2"));
        v2.setValidBetween(new ValidBetween(yesterday));
        v2.setVersion(2L);
        v2.setNetexId(v1.getNetexId());
        v2.setStopPlaceType(StopTypeEnumeration.BUS_STATION);

        stopPlaceRepository.save(v2);

        ExportParams exportParams = newExportParamsBuilder().setStopPlaceSearch(newStopPlaceSearchBuilder()
                .setQuery(v1.getNetexId())
                .setStopTypeEnumerations(Arrays.asList(StopTypeEnumeration.BUS_STATION))
                .setAllVersions(true)
                .build())
                .build();

        List<StopPlace> content = stopPlaceRepository.findStopPlace(exportParams).getContent();

        assertThat(content).isNotEmpty();
        assertThat(content).hasSize(2);
    }

    @Test
    public void findStopPlacesDefaultsToVersionValidityCurrent() {

        Instant yesterday = now.minus(1, ChronoUnit.DAYS);

        StopPlace v1 = new StopPlace(new EmbeddableMultilingualString("stop"));
        v1.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        v1.setVersion(1L);
        v1.setValidBetween(new ValidBetween(Instant.EPOCH, yesterday));
        stopPlaceRepository.save(v1);

        StopPlace v2 = new StopPlace(new EmbeddableMultilingualString("v2"));
        v2.setVersion(2L);
        v2.setNetexId(v1.getNetexId());
        v2.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        v2.setValidBetween(new ValidBetween(yesterday));
        stopPlaceRepository.save(v2);

        stopPlaceRepository.flush();

        ExportParams exportParams = newExportParamsBuilder().setStopPlaceSearch(newStopPlaceSearchBuilder()
                .setQuery(v2.getNetexId())
                .setStopTypeEnumerations(Arrays.asList(StopTypeEnumeration.BUS_STATION))
                .build())
                .build();

        List<StopPlace> content = stopPlaceRepository.findStopPlace(exportParams).getContent();

        assertThat(content).isNotEmpty();
        assertThat(content).hasSize(1);
        assertThat(content.getFirst().getVersion()).isEqualTo(2L);
    }

    @Test
    public void findStopPlaceByCounties() throws Exception {
        String stopPlaceName = "Slependen";
        String municipalityName = "Bærum";
        String countyName = "Akershus";

        TopographicPlace akershus = createCounty(countyName);
        TopographicPlace municipality = createMunicipality(municipalityName, akershus);
        StopPlace stopPlace = createStopPlaceWithMunicipality(stopPlaceName, municipality);
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        stopPlaceRepository.save(stopPlace);

        TopographicPlace buskerud = createCounty("Buskerud");

        ExportParams exportParams = newExportParamsBuilder().setStopPlaceSearch(newStopPlaceSearchBuilder()
                .setQuery(stopPlaceName)
                .setStopTypeEnumerations(Arrays.asList(StopTypeEnumeration.BUS_STATION))
                .setVersionValidity(ExportParams.VersionValidity.ALL)
                .build())
                .setMunicipalityReference(municipality.getNetexId())
                .setCountyReference(buskerud.getNetexId())
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(exportParams);
        assertThat(result).isNotEmpty();
        assertThat(result).extracting(actual -> actual.getNetexId()).contains(stopPlace.getNetexId());
    }

    @Test
    public void findStopPlacNameContainsIgnoreCase() throws Exception {
        String stopPlaceName = "IKEA Slependen";

        createStopPlaceWithMunicipality(stopPlaceName, null);

        ExportParams exportParams = newExportParamsBuilder().setStopPlaceSearch(
                newStopPlaceSearchBuilder()
                        .setQuery("lEpEnden")
                        .setVersionValidity(ExportParams.VersionValidity.ALL)
                        .build())
                .build();
        Page<StopPlace> result = stopPlaceRepository.findStopPlace(exportParams);
        assertThat(result).isNotEmpty();
        System.out.println(result.getContent().getFirst());
    }

    /**
     * Expect no result beacuse stop type is not matching.
     */
    @Test
    public void findStopPlaceByCountyAndMunicipalityAndStopPlaceType() throws Exception {
        String municipalityName = "Asker";
        String countyName = "Akershus";

        TopographicPlace county = createCounty(countyName);
        TopographicPlace municipality = createMunicipality(municipalityName, county);
        createStopPlaceWithMunicipality("Does not matter", municipality);

        ExportParams exportParams = newExportParamsBuilder()
                .setStopPlaceSearch(newStopPlaceSearchBuilder()
                        .setStopTypeEnumerations(Arrays.asList(StopTypeEnumeration.COACH_STATION))
                        .build())
                .setMunicipalityReference(municipality.getNetexId())
                .setCountyReference(county.getNetexId())
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(exportParams);

        assertThat(result).isEmpty();
    }

    /**
     * Expect no result because name should be anded with other parts of query
     */
    @Test
    public void findStopPlaceByCountyAndMunicipalityAndNameExpectNoResult() throws Exception {
        String municipalityName = "Asker";
        String countyName = "Akershus";

        TopographicPlace county = createCounty(countyName);
        TopographicPlace municipality = createMunicipality(municipalityName, county);
        createStopPlaceWithMunicipality("XYZ", municipality);


        ExportParams exportParams = newExportParamsBuilder()
                .setStopPlaceSearch(newStopPlaceSearchBuilder()
                        .setQuery("Name")
                        .build())
                .setMunicipalityReference(municipality.getNetexId())
                .setCountyReference(county.getNetexId())
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(exportParams);

        System.out.println(result);
        assertThat(result).isEmpty();
    }

    @Test
    public void findStopPlaceByCountyAndNameThenExpectEmptyResult() throws Exception {
        String municipalityName = "Asker";
        String countyName = "Akershus";

        TopographicPlace county = createCounty(countyName);
        TopographicPlace municipality = createMunicipality(municipalityName, county);
        createStopPlaceWithMunicipality("No matching stop name", municipality);

        ExportParams exportParams = newExportParamsBuilder().setStopPlaceSearch(newStopPlaceSearchBuilder()
                .setQuery("Somewhere else")
                .build())
                .setCountyReference(county.getNetexId())
                .build();
        Page<StopPlace> result = stopPlaceRepository.findStopPlace(exportParams);
        assertThat(result).isEmpty();
    }

    @Test
    public void findStopPlaceByMunicipalityAndNameAndExpectEmptyResult() throws Exception {
        TopographicPlace municipality = createMunicipality("Asker", createCounty("Akershus"));
        createStopPlaceWithMunicipality("No matching stop name", municipality);

        ExportParams exportParams = newExportParamsBuilder().setStopPlaceSearch(newStopPlaceSearchBuilder()
                .setQuery("Somewhere else")
                .build())
                .setMunicipalityReference(municipality.getNetexId())
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(exportParams);
        assertThat(result).isEmpty();
    }

    @Test
    public void findStopPlacesByListOfIds() throws Exception {

        StopPlace stopPlace1 = new StopPlace();
        stopPlaceRepository.save(stopPlace1);

        StopPlace stopPlace2 = new StopPlace();
        stopPlaceRepository.save(stopPlace2);

        StopPlace stopPlaceThatShouldNotBeReturned = new StopPlace();
        stopPlaceRepository.save(stopPlaceThatShouldNotBeReturned);

        List<String> stopPlaceIds = Arrays.asList(stopPlace1.getNetexId(), stopPlace2.getNetexId());

        ExportParams exportParams = newExportParamsBuilder()
                .setStopPlaceSearch(newStopPlaceSearchBuilder()
                        .setNetexIdList(stopPlaceIds)
                        .setVersionValidity(ExportParams.VersionValidity.ALL)
                        .build())
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(exportParams);
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(StopPlace::getNetexId)
                .contains(stopPlace1.getNetexId(), stopPlace2.getNetexId())
                .doesNotContain(stopPlaceThatShouldNotBeReturned.getNetexId());
    }

    @Test
    public void emptyIdListShouldReturnStops() throws Exception {

        StopPlace stopPlace1 = new StopPlace();
        stopPlaceRepository.save(stopPlace1);

        StopPlace stopPlace2 = new StopPlace();
        stopPlaceRepository.save(stopPlace2);

        List<String> stopPlaceIds = new ArrayList<>();

        ExportParams exportParams = newExportParamsBuilder()
                .setStopPlaceSearch(newStopPlaceSearchBuilder()
                        .setNetexIdList(stopPlaceIds)
                        .setVersionValidity(ExportParams.VersionValidity.ALL).build())
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(exportParams);
        assertThat(result).isNotEmpty();
    }


    @Test
    public void searchingForIdListShouldNotUseQueryMunicipalityOrCounty() throws Exception {

        TopographicPlace county = createCounty("Hedmark");
        TopographicPlace municipality = createMunicipality("Hamar", county);
        createStopPlaceWithMunicipality("FromMunicipality", municipality);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("OnlyThis"));
        stopPlaceRepository.save(stopPlace);

        List<String> stopPlaceIds = new ArrayList<>();
        stopPlaceIds.add(stopPlace.getNetexId());

        StopPlaceSearch stopPlaceSearch = newStopPlaceSearchBuilder()
                .setQuery("FromMu")
                .setNetexIdList(stopPlaceIds)
                .setVersionValidity(ExportParams.VersionValidity.ALL)
                .build();

        ExportParams exportParams = newExportParamsBuilder().setStopPlaceSearch(stopPlaceSearch)
                .setMunicipalityReference(municipality.getNetexId())
                .setCountyReference(county.getNetexId())
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(exportParams);
        assertThat(result).extracting(StopPlace::getName).extracting(EmbeddableMultilingualString::getValue)
                .contains("OnlyThis")
                .doesNotContain("FromMunicipality");
    }

    @Test
    public void findStopPlaceByTypeAirport() {
        StopPlace stopPlace = new StopPlace();

        stopPlace.setStopPlaceType(StopTypeEnumeration.AIRPORT);

        stopPlaceRepository.save(stopPlace);

        StopPlaceSearch search = newStopPlaceSearchBuilder()
                .setStopTypeEnumerations(Arrays.asList(StopTypeEnumeration.AIRPORT))
                .setVersionValidity(ExportParams.VersionValidity.ALL)
                .build();
        Page<StopPlace> result = stopPlaceRepository.findStopPlace(newExportParamsBuilder().setStopPlaceSearch(search).build());

        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    public void findOnlyMaxVersion() {
        StopPlace versionOne = new StopPlace();
        versionOne.setVersion(1L);
        versionOne.setNetexId("NSR:StopPlace:999");
        StopPlace versionTwo = new StopPlace();
        versionTwo.setVersion(2L);
        versionTwo.setNetexId(versionOne.getNetexId());

        versionOne = stopPlaceRepository.save(versionOne);
        versionTwo = stopPlaceRepository.save(versionTwo);

        StopPlaceSearch stopPlaceSearch = newStopPlaceSearchBuilder()
                .setNetexIdList(Arrays.asList(versionOne.getNetexId()))
                .setVersionValidity(ExportParams.VersionValidity.MAX_VERSION)
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(newExportParamsBuilder().setStopPlaceSearch(stopPlaceSearch).build());

        assertThat(result.getContent())
                .describedAs("Expecting only one stop place in return. Because only the highest version should be returned.")
                .hasSize(1);

    }

    @Test
    public void findStopPlaceByParentName() {

        StopPlace child = new StopPlace();
        child.setVersion(1L);
        child = stopPlaceRepository.save(child);

        String parentStopPlaceName = "new parent stop place";
        multiModalStopPlaceEditor.createMultiModalParentStopPlace(Arrays.asList(child.getNetexId()), new EmbeddableMultilingualString(parentStopPlaceName));

        Page<StopPlace> actual = stopPlaceRepository.findStopPlace(ExportParams.newExportParamsBuilder().setStopPlaceSearch(StopPlaceSearch.newStopPlaceSearchBuilder().setQuery(parentStopPlaceName).build()).build());
        assertThat(actual.getContent().getFirst().getNetexId()).as("The child is expected to be returned").isEqualTo(child.getNetexId());
    }

    @Test
    public void findParentStopPlaceById() {

        StopPlace child = new StopPlace();
        child.setVersion(1L);
        child = stopPlaceRepository.save(child);

        String parentStopPlaceName = "new parent stop place";
        StopPlace parent = multiModalStopPlaceEditor.createMultiModalParentStopPlace(Arrays.asList(child.getNetexId()), new EmbeddableMultilingualString(parentStopPlaceName));

        Page<StopPlace> actual = stopPlaceRepository.findStopPlace(
                ExportParams.newExportParamsBuilder()
                        .setStopPlaceSearch(
                                StopPlaceSearch.newStopPlaceSearchBuilder()
                                        .setNetexIdList(Arrays.asList(parent.getNetexId()))
                                        .build())
                        .build());
        assertThat(actual.getContent().getFirst().getNetexId()).as("The child is expected to be returned when searching for netex_id").isEqualTo(child.getNetexId());
    }

    @Test
    public void findParentStopByKeyValues() {

        StopPlace child = new StopPlace();
        child.setVersion(1L);
        child = stopPlaceRepository.save(child);

        String parentStopPlaceName = "new parent stop place";
        StopPlace parent = multiModalStopPlaceEditor.createMultiModalParentStopPlace(Arrays.asList(child.getNetexId()), new EmbeddableMultilingualString(parentStopPlaceName));

        String mergedIdValue = "KOL:StopPlace:1";
        parent.getKeyValues().put(MERGED_ID_KEY, new Value(mergedIdValue));

        stopPlaceRepository.save(parent);

        Page<StopPlace> actual = stopPlaceRepository.findStopPlace(
                ExportParams.newExportParamsBuilder()
                        .setStopPlaceSearch(
                                StopPlaceSearch.newStopPlaceSearchBuilder()
                                        .setQuery(mergedIdValue)
                                        .build())
                        .build());
        assertThat(actual.getContent()).isNotEmpty();
        assertThat(actual.getContent().getFirst().getNetexId())
                .as("The child is expected to be returned when searching for parent " + MERGED_ID_KEY)
                .isEqualTo(child.getNetexId());
    }

    @Test
    public void findStopPlaceByParentNameAndChildType() {

        StopPlace child = new StopPlace();
        child.setVersion(1L);
        child.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        child = stopPlaceRepository.save(child);

        String parentStopPlaceName = "new parent stop place. child is bus";
        StopPlace parent = multiModalStopPlaceEditor.createMultiModalParentStopPlace(Arrays.asList(child.getNetexId()), new EmbeddableMultilingualString(parentStopPlaceName));

        Page<StopPlace> actual = stopPlaceRepository.findStopPlace(
                ExportParams.newExportParamsBuilder().setStopPlaceSearch(
                        StopPlaceSearch.newStopPlaceSearchBuilder()
                                .setQuery(parentStopPlaceName)
                                .setStopTypeEnumerations(Arrays.asList(child.getStopPlaceType()))
                                .build())
                        .build());
        assertThat(actual.getTotalElements()).isEqualTo(1L);
        assertThat(actual.getContent().getFirst().getNetexId()).isEqualTo(child.getNetexId());
    }

    @Test
    public void findOnlyGivenVersion() {
        StopPlace versionOne = new StopPlace();
        versionOne.setVersion(1L);
        versionOne.setNetexId("NSR:StopPlace:999");
        StopPlace versionTwo = new StopPlace();
        versionTwo.setVersion(2L);
        versionTwo.setNetexId(versionOne.getNetexId());

        versionOne = stopPlaceRepository.save(versionOne);
        versionTwo = stopPlaceRepository.save(versionTwo);

        StopPlaceSearch stopPlaceSearch = newStopPlaceSearchBuilder()
                .setNetexIdList(Arrays.asList(versionOne.getNetexId())).setVersion(1L)
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(newExportParamsBuilder().setStopPlaceSearch(stopPlaceSearch).build());

        assertThat(result)
                .describedAs("Expecting only one stop place in return. Because only the given version should be returned.")
                .hasSize(1);

        assertThat(result).extracting(StopPlace::getVersion)
                .contains(1L);

    }

    @Test
    public void scrollStopsWithEffectiveChangesInPeriod() {
        Instant endOfPeriod = now;
        Instant startOfPeriod = endOfPeriod.minusSeconds(100);

        StopPlace historicVersion = saveStop("NSR:StopPlace:900", 1L, startOfPeriod.minusSeconds(10), startOfPeriod.minusSeconds(2));
        StopPlace changedInPeriodButNotCurrent = saveStop("NSR:StopPlace:900", 2L, startOfPeriod.minusSeconds(2), startOfPeriod.plusSeconds(5));
        StopPlace currentVersion = saveStop("NSR:StopPlace:900", 3L, startOfPeriod.plusSeconds(5), endOfPeriod.plusSeconds(2));
        StopPlace futureVersion = saveStop("NSR:StopPlace:900", 4L, endOfPeriod.plusSeconds(2), null);

        StopPlace expiredInPeriod = saveStop("NSR:StopPlace:901", 1L, startOfPeriod.minusSeconds(2), startOfPeriod.plusSeconds(30));
        StopPlace newInPeriod = saveStop("NSR:StopPlace:902", 1L, startOfPeriod.plusSeconds(2), null);
        StopPlace notChangedInPeriod = saveStop("NSR:StopPlace:903", 1L, startOfPeriod.minusSeconds(10), null);


        Page<StopPlace> changedStopsP0 = stopPlaceRepository.findStopPlacesWithEffectiveChangeInPeriod(new ChangedStopPlaceSearch(startOfPeriod, endOfPeriod, PageRequest.of(0, 2)));

        Assert.assertTrue(changedStopsP0.hasNext());
        Page<StopPlace> changedStopsP1 = stopPlaceRepository.findStopPlacesWithEffectiveChangeInPeriod(new ChangedStopPlaceSearch(startOfPeriod, endOfPeriod, PageRequest.of(1, 2)));
        Assert.assertFalse(changedStopsP1.hasNext());

        List<StopPlace> accumulatedStops = new ArrayList<>(changedStopsP0.getContent());
        accumulatedStops.addAll(changedStopsP1.getContent());
        assertContainsOnlyInExactVersion(accumulatedStops, currentVersion, expiredInPeriod, newInPeriod);

    }

    @Test
    public void findStopsWithEffectiveChangesInPeriodWithParent() {

        String importedIdPosix = "321";
        String importedId = "XXX:StopPlace:" + importedIdPosix;

        StopPlace childStop = new StopPlace();
        childStop.setVersion(1L);

        Quay quay = new Quay();
        quay.getOrCreateValues(ORIGINAL_ID_KEY).add(importedId);
        quayRepository.save(quay);

        childStop.getQuays().add(quay);

        StopPlace parentStop = new StopPlace();
        parentStop.setParentStopPlace(true);
        parentStop.setVersion(2L);

        // Valid between only set on parent
        parentStop.setValidBetween(new ValidBetween(now.minusSeconds(10)));
        parentStop.getChildren().add(childStop);

        stopPlaceRepository.save(parentStop);

        childStop.setParentSiteRef(new SiteRefStructure(parentStop.getNetexId(), String.valueOf(parentStop.getVersion())));
        stopPlaceRepository.save(childStop);

        ChangedStopPlaceSearch changedStopPlaceSearch = new ChangedStopPlaceSearch(now.minusSeconds(20), now.plusSeconds(20), PageRequest.of(0, 10));
        Page<StopPlace> result = stopPlaceRepository.findStopPlacesWithEffectiveChangeInPeriod(changedStopPlaceSearch);
        assertThat(result.getContent()).extracting(StopPlace::getNetexId).contains(parentStop.getNetexId());
        assertThat(result.getContent()).extracting(StopPlace::getNetexId).doesNotContain(childStop.getNetexId());
    }

    @Test
    public void findStopPlaceForSpecificPointInTime() throws Exception {
        String stopPlaceName = "Nesbru";

        ValidBetween expiredValidBetween = new ValidBetween(now.minusSeconds(1000), now.minusSeconds(100));
        StopPlace expiredStopPlace = createStopPlaceWithMunicipality(stopPlaceName, null);
        expiredStopPlace.setValidBetween(expiredValidBetween);
        stopPlaceRepository.save(expiredStopPlace);

        ValidBetween openendedValidBetween = new ValidBetween(now.minusSeconds(100));
        StopPlace newestStopPlace = createStopPlaceWithMunicipality(stopPlaceName, null);
        newestStopPlace.setValidBetween(openendedValidBetween);
        stopPlaceRepository.save(newestStopPlace);

        StopPlaceSearch search = newStopPlaceSearchBuilder()
                .setQuery(stopPlaceName)
                .setPointInTime(now.minusSeconds(1000))
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(newExportParamsBuilder().setStopPlaceSearch(search).build());
        assertThat(result)
                .extracting(IdentifiedEntity::getNetexId)
                .contains(expiredStopPlace.getNetexId())
                .doesNotContain(newestStopPlace.getNetexId());

        search = newStopPlaceSearchBuilder()
                .setQuery(stopPlaceName)
                .setPointInTime(now)
                .build();

        result = stopPlaceRepository.findStopPlace(newExportParamsBuilder().setStopPlaceSearch(search).build());
        assertThat(result)
                .extracting(IdentifiedEntity::getNetexId)
                .contains(newestStopPlace.getNetexId())
                .doesNotContain(expiredStopPlace.getNetexId())
        ;
    }

    @Test
    public void findStopPlaceFromQuayOriginalIdReturnsOnlyStopsValidAtPointInTime() {
        String orgIdSuffix = "2";
        String orgId = "XXX:Quay:" + orgIdSuffix;

        StopPlace historicMatchingStopV1 = saveStop("NSR:StopPlace:1", 1l, now.minusSeconds(200), now.minusSeconds(10));
        saveQuay(historicMatchingStopV1, "NSR:Quay:1", 1l, ORIGINAL_ID_KEY, orgId);
        StopPlace historicMatchingStopV2 = saveStop(historicMatchingStopV1.getNetexId(), 2l, now.minusSeconds(10), null);
        saveQuay(historicMatchingStopV2, "NSR:Quay:1", 2l, ORIGINAL_ID_KEY, null);

        StopPlace currentMatchingStop = saveStop("NSR:StopPlace:2", 1l, now.minusSeconds(10), null);
        saveQuay(currentMatchingStop, "NSR:Quay:2", 1l, ORIGINAL_ID_KEY, orgId);

        List<String> currentMatchingStopPlaceIds = stopPlaceRepository.findStopPlaceFromQuayOriginalId(orgIdSuffix, now);
        Assert.assertEquals(Arrays.asList(currentMatchingStop.getNetexId()), currentMatchingStopPlaceIds);

        List<String> historicMatchingStopPlaceIds = stopPlaceRepository.findStopPlaceFromQuayOriginalId(orgIdSuffix, now.minusSeconds(100));
        Assert.assertEquals(Arrays.asList(historicMatchingStopV1.getNetexId()), historicMatchingStopPlaceIds);

        // No imported-ids are valid for point in time 300 seconds ago
        Assert.assertTrue(CollectionUtils.isEmpty(stopPlaceRepository.findStopPlaceFromQuayOriginalId(orgIdSuffix, now.minusSeconds(300))));
    }

    @Test
    public void listStopPlaceIdsAndQuayIds() {
        Instant startOfPeriod = now.minusSeconds(100);
        Instant endOfPeriod = now.plusSeconds(100);
        StopPlace stopPlace1 = saveStop("NSR:StopPlace:1", 1l, startOfPeriod, endOfPeriod);
        saveQuay(stopPlace1, "NSR:Quay:11", 1l, ORIGINAL_ID_KEY, "XXX:Quay:11");
        saveQuay(stopPlace1, "NSR:Quay:12", 1l, ORIGINAL_ID_KEY, "XXX:Quay:12");
        saveQuay(stopPlace1, "NSR:Quay:13", 1l, ORIGINAL_ID_KEY, "XXX:Quay:13");

        StopPlace stopPlace2 = saveStop("NSR:StopPlace:2", 1l, startOfPeriod, endOfPeriod);
        saveQuay(stopPlace2, "NSR:Quay:21", 1l, ORIGINAL_ID_KEY, "XXX:Quay:21");
        saveQuay(stopPlace2, "NSR:Quay:22", 1l, ORIGINAL_ID_KEY, "XXX:Quay:22");
        saveQuay(stopPlace2, "NSR:Quay:23", 1l, ORIGINAL_ID_KEY, "XXX:Quay:23");
        saveQuay(stopPlace2, "NSR:Quay:24", 1l, ORIGINAL_ID_KEY, "XXX:Quay:24");

        StopPlace stopPlace3 = saveStop("NSR:StopPlace:3", 1l, startOfPeriod, endOfPeriod);
        saveQuay(stopPlace3, "NSR:Quay:31", 1l, ORIGINAL_ID_KEY, "XXX:Quay:31");

        Map<String, Set<String>> stops = stopPlaceRepository.listStopPlaceIdsAndQuayIds(now, null);
        Assert.assertEquals(3, stops.size());
        Assert.assertTrue(stops.containsKey("NSR:StopPlace:1"));
        Assert.assertTrue(stops.containsKey("NSR:StopPlace:2"));
        Assert.assertTrue(stops.containsKey("NSR:StopPlace:3"));
        Assert.assertEquals(3, stops.get("NSR:StopPlace:1").size());
        Assert.assertEquals(4, stops.get("NSR:StopPlace:2").size());
        Assert.assertEquals(1, stops.get("NSR:StopPlace:3").size());

    }

    @Test
    public void findKeyValueMappingsForStopPlaceReturnsStopPlacesWithParentValidAtPointIntimeForMergedId() {

        String mergedId = "XXX:StopPlace:321";

        StopPlace childStop = new StopPlace();
        childStop.getKeyValues().put(MERGED_ID_KEY, new Value(mergedId));
        childStop.setVersion(1L);

        StopPlace parentStop = new StopPlace();
        parentStop.setParentStopPlace(true);
        parentStop.setVersion(2L);
        parentStop.setValidBetween(new ValidBetween(now.minusSeconds(10)));
        parentStop.getChildren().add(childStop);

        stopPlaceRepository.save(parentStop);

        childStop.setParentSiteRef(new SiteRefStructure(parentStop.getNetexId(), String.valueOf(parentStop.getVersion())));
        stopPlaceRepository.save(childStop);

        List<IdMappingDto> idMapping = stopPlaceRepository.findKeyValueMappingsForStop(now, now, 0, 2000);
        assertThat(idMapping).extracting(idMappingDto -> idMappingDto.netexId).contains(childStop.getNetexId());
    }

    @Test
    public void findKeyValueMappingsForStopPlaceQuayReturnsStopPlacesWithParentValidAtPointIntimeForImportedId() {

        String importedIdPosix = "321";
        String importedId = "XXX:StopPlace:" + importedIdPosix;

        StopPlace childStop = new StopPlace();
        childStop.setVersion(1L);

        Quay quay = new Quay();
        quay.getOrCreateValues(ORIGINAL_ID_KEY).add(importedId);
        quayRepository.save(quay);

        childStop.getQuays().add(quay);

        StopPlace parentStop = new StopPlace();
        parentStop.setParentStopPlace(true);
        parentStop.setVersion(2L);
        parentStop.setValidBetween(new ValidBetween(now.minusSeconds(10)));
        parentStop.getChildren().add(childStop);

        stopPlaceRepository.save(parentStop);

        childStop.setParentSiteRef(new SiteRefStructure(parentStop.getNetexId(), String.valueOf(parentStop.getVersion())));
        stopPlaceRepository.save(childStop);

        List<String> idMapping = stopPlaceRepository.findStopPlaceFromQuayOriginalId(importedIdPosix, now);
        assertThat(idMapping).contains(childStop.getNetexId());
    }


    @Test
    public void findKeyValueMappingsForStopPlaceQuayReturnsNoStopPlacesWithParentValidAtPointIntimeForImportedId() {

        String importedIdPosix = "322";
        String importedId = "XXX:StopPlace:" + importedIdPosix;

        StopPlace childStop = new StopPlace();
        childStop.setVersion(1L);

        Quay quay = new Quay();
        quay.getOrCreateValues(ORIGINAL_ID_KEY).add(importedId);
        quayRepository.save(quay);

        childStop.getQuays().add(quay);

        StopPlace parentStop = new StopPlace();
        parentStop.setParentStopPlace(true);
        parentStop.setVersion(2L);
        parentStop.setValidBetween(new ValidBetween(now.minusSeconds(10)));
        parentStop.getChildren().add(childStop);

        stopPlaceRepository.save(parentStop);

        childStop.setParentSiteRef(new SiteRefStructure(parentStop.getNetexId(), String.valueOf(parentStop.getVersion())));
        stopPlaceRepository.save(childStop);

        List<String> idMapping = stopPlaceRepository.findStopPlaceFromQuayOriginalId(importedIdPosix, now.minusSeconds(20));
        assertThat(idMapping).isEmpty();
    }

    @Test
    public void findByTagHashQuery() {

        StopPlace stopPlace = new StopPlace();

        stopPlace.setVersion(2L);

        stopPlace.setValidBetween(new ValidBetween(now.minusSeconds(10)));

        stopPlaceRepository.save(stopPlace);

        Tag tag = new Tag();
        tag.setName("tagname");
        tag.setIdreference(stopPlace.getNetexId());
        tag = tagRepository.save(tag);
        tagRepository.flush();

        StopPlaceSearch stopPlaceSearch = StopPlaceSearch.newStopPlaceSearchBuilder()
                .setQuery("#" + tag.getName())
                .build();

        Page<StopPlace> searchResult = stopPlaceRepository.findStopPlace(ExportParams.newExportParamsBuilder().setStopPlaceSearch(stopPlaceSearch).build());
        assertThat(searchResult.getContent()).extracting(s -> s.getNetexId()).contains(stopPlace.getNetexId());
    }

    @Test
    public void findByParentTagHashQuery() {

        StopPlace parentStopPlace = new StopPlace();
        parentStopPlace.setParentStopPlace(true);
        parentStopPlace.setVersion(2L);

        parentStopPlace.setValidBetween(new ValidBetween(now.minusSeconds(10)));

        stopPlaceRepository.save(parentStopPlace);

        StopPlace childStopPlace = new StopPlace();
        childStopPlace.setParentSiteRef(new SiteRefStructure(parentStopPlace.getNetexId(), String.valueOf(parentStopPlace.getVersion())));

        stopPlaceRepository.save(childStopPlace);

        Tag tag = new Tag();
        tag.setName("followup");
        tag.setIdreference(childStopPlace.getNetexId());
        tag = tagRepository.save(tag);
        tagRepository.flush();

        StopPlaceSearch stopPlaceSearch = StopPlaceSearch.newStopPlaceSearchBuilder()
                .setQuery("#" + tag.getName())
                .build();

        Page<StopPlace> searchResult = stopPlaceRepository.findStopPlace(ExportParams.newExportParamsBuilder().setStopPlaceSearch(stopPlaceSearch).build());
        assertThat(searchResult.getContent()).extracting(s -> s.getNetexId()).contains(childStopPlace.getNetexId());
    }

    @Test
    public void findByParentTagParam() {

        StopPlace parentStopPlace = new StopPlace();
        parentStopPlace.setParentStopPlace(true);
        parentStopPlace.setVersion(2L);

        parentStopPlace.setValidBetween(new ValidBetween(now.minusSeconds(10)));

        stopPlaceRepository.save(parentStopPlace);

        StopPlace childStopPlace = new StopPlace();
        childStopPlace.setParentSiteRef(new SiteRefStructure(parentStopPlace.getNetexId(), String.valueOf(parentStopPlace.getVersion())));

        stopPlaceRepository.save(childStopPlace);

        Tag tag = new Tag();
        tag.setName("followup");
        tag.setIdreference(childStopPlace.getNetexId());
        tag = tagRepository.save(tag);
        tagRepository.flush();

        StopPlaceSearch stopPlaceSearch = StopPlaceSearch.newStopPlaceSearchBuilder()
                .setTags(Arrays.asList(tag.getName()))
                .build();

        Page<StopPlace> searchResult = stopPlaceRepository.findStopPlace(ExportParams.newExportParamsBuilder().setStopPlaceSearch(stopPlaceSearch).build());
        assertThat(searchResult.getContent()).extracting(s -> s.getNetexId()).contains(childStopPlace.getNetexId());
    }

    @Test
    public void findByTagParam() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(2L);

        stopPlace.setValidBetween(new ValidBetween(now.minusSeconds(10)));

        stopPlaceRepository.save(stopPlace);

        Tag tag = new Tag();
        tag.setName("tagname");
        tag.setIdreference(stopPlace.getNetexId());
        tag = tagRepository.save(tag);

        StopPlaceSearch stopPlaceSearch = StopPlaceSearch.newStopPlaceSearchBuilder()
                .setQuery("#" + tag.getName())
                .build();

        Page<StopPlace> searchResult = stopPlaceRepository.findStopPlace(ExportParams.newExportParamsBuilder().setStopPlaceSearch(stopPlaceSearch).build());
        assertThat(searchResult.getContent()).extracting(s -> s.getNetexId()).contains(stopPlace.getNetexId());
    }

    @Test
    public void doNotFindStopPlacesByRemovedTagsHashQuery() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(2L);


        stopPlace.setValidBetween(new ValidBetween(now.minusSeconds(10)));

        stopPlaceRepository.save(stopPlace);

        Tag removedTag = new Tag();
        removedTag.setName("tagname");
        removedTag.setIdreference(stopPlace.getNetexId());
        removedTag.setRemoved(now);
        removedTag.setRemovedBy("me");

        removedTag = tagRepository.save(removedTag);

        StopPlaceSearch stopPlaceSearch = StopPlaceSearch.newStopPlaceSearchBuilder()
                .setQuery("#" + removedTag.getName())
                .build();

        Page<StopPlace> searchResult = stopPlaceRepository.findStopPlace(ExportParams.newExportParamsBuilder().setStopPlaceSearch(stopPlaceSearch).build());
        assertThat(searchResult.getContent()).as("search result").isEmpty();
    }

    @Test
    public void doNotFindStopPlacesByRemovedTagsParams() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(2L);

        stopPlace.setValidBetween(new ValidBetween(now.minusSeconds(10)));

        stopPlaceRepository.save(stopPlace);

        Tag removedTag = new Tag();
        removedTag.setName("tagname");
        removedTag.setIdreference(stopPlace.getNetexId());
        removedTag.setRemoved(now);
        removedTag.setRemovedBy("me");

        removedTag = tagRepository.save(removedTag);

        StopPlaceSearch stopPlaceSearch = StopPlaceSearch.newStopPlaceSearchBuilder()
                .setTags(Arrays.asList(removedTag.getName()))
                .build();

        Page<StopPlace> searchResult = stopPlaceRepository.findStopPlace(ExportParams.newExportParamsBuilder().setStopPlaceSearch(stopPlaceSearch).build());
        assertThat(searchResult.getContent()).as("search result").isEmpty();
    }


    @Test
    public void doNotFindParentStopPlacesByRemovedTagsParams() {

        StopPlace parentStopPlace = new StopPlace();
        parentStopPlace.setParentStopPlace(true);
        parentStopPlace.setVersion(2L);

        parentStopPlace.setValidBetween(new ValidBetween(now.minusSeconds(10)));

        stopPlaceRepository.save(parentStopPlace);

        StopPlace childStopPlace = new StopPlace();
        childStopPlace.setParentSiteRef(new SiteRefStructure(parentStopPlace.getNetexId(), String.valueOf(parentStopPlace.getVersion())));

        stopPlaceRepository.save(childStopPlace);

        Tag removedTag = new Tag();
        removedTag.setName("tagname");
        removedTag.setIdreference(parentStopPlace.getNetexId());
        removedTag.setRemoved(now);
        removedTag.setRemovedBy("me");

        removedTag = tagRepository.save(removedTag);

        StopPlaceSearch stopPlaceSearch = StopPlaceSearch.newStopPlaceSearchBuilder()
                .setTags(Arrays.asList(removedTag.getName()))
                .build();

        Page<StopPlace> searchResult = stopPlaceRepository.findStopPlace(ExportParams.newExportParamsBuilder().setStopPlaceSearch(stopPlaceSearch).build());
        assertThat(searchResult.getContent()).as("search result").isEmpty();
    }

    @Test
    public void findKeyValueMappingsForStopPlaceReturnsOnlyStopPlacesValidAtPointInTimeForImportedId() {
        testFindKeyValueMappingsForStopPlaceReturnsOnlyStopPlacesValidAtPointInTime(ORIGINAL_ID_KEY);
    }

    @Test
    public void findKeyValueMappingsForStopPlaceReturnsOnlyStopPlacesValidAtPointInTimeForMergedId() {
        testFindKeyValueMappingsForStopPlaceReturnsOnlyStopPlacesValidAtPointInTime(MERGED_ID_KEY);
    }


    @Test
    public void findStopPlaceNameIgnoreCommonWords() throws Exception {
        String stopPlaceName = "Gare de Dax";

        createStopPlaceWithMunicipality(stopPlaceName, null);

        ExportParams exportParams = newExportParamsBuilder().setStopPlaceSearch(
                newStopPlaceSearchBuilder()
                        .setQuery("gare dax")
                        .setAllVersions(true)
                        .build())
                .build();
        Page<StopPlace> result = stopPlaceRepository.findStopPlace(exportParams);
        assertThat(result).isNotEmpty();
        System.out.println(result.getContent().getFirst());
    }


    public void testFindKeyValueMappingsForStopPlaceReturnsOnlyStopPlacesValidAtPointInTime(String orgIdKey) {
        String orgIdSuffix = "2";
        String orgId = "XXX:StopPlace:" + orgIdSuffix;

        StopPlace historicMatchingStopV1 = saveStop("NSR:StopPlace:1", 1l, now.minusSeconds(200), now.minusSeconds(10));
        historicMatchingStopV1.getKeyValues().put(orgIdKey, new Value(orgId));
        stopPlaceRepository.save(historicMatchingStopV1);

        StopPlace currentMatchingStop = saveStop("NSR:StopPlace:2", 1l, now.minusSeconds(10), null);
        currentMatchingStop.getKeyValues().put(orgIdKey, new Value(orgId));
        stopPlaceRepository.save(currentMatchingStop);

        List<IdMappingDto> currentMapping = stopPlaceRepository.findKeyValueMappingsForStop(now, now, 0, 2000);
        Assert.assertEquals(1, currentMapping.size());
        Assert.assertEquals(orgId, currentMapping.getFirst().originalId);
        Assert.assertEquals(currentMatchingStop.getNetexId(), currentMapping.getFirst().netexId);
        Assert.assertEquals(currentMatchingStop.getValidBetween().getFromDate(), currentMapping.getFirst().validFrom);
        Assert.assertEquals(currentMatchingStop.getValidBetween().getToDate(), currentMapping.getFirst().validTo);

        Instant hundredSecondsAgo = now.minusSeconds(100);
        List<IdMappingDto> historicMapping = stopPlaceRepository.findKeyValueMappingsForStop(hundredSecondsAgo, hundredSecondsAgo, 0, 2000);
        Assert.assertEquals(1, historicMapping.size());
        Assert.assertEquals(orgId, historicMapping.getFirst().originalId);
        Assert.assertEquals(historicMatchingStopV1.getNetexId(), historicMapping.getFirst().netexId);
        Assert.assertEquals(historicMatchingStopV1.getValidBetween().getFromDate(), historicMapping.getFirst().validFrom);
        Assert.assertEquals(historicMatchingStopV1.getValidBetween().getToDate(), historicMapping.getFirst().validTo);

        // No imported-ids or merged-ids are valid for point in time 300 seconds ago
        Instant threeHundredSecondsAgo = now.minusSeconds(300);
        Assert.assertTrue(stopPlaceRepository.findKeyValueMappingsForStop(threeHundredSecondsAgo, threeHundredSecondsAgo, 0, 2000).isEmpty());

        List<IdMappingDto> allMappings = stopPlaceRepository.findKeyValueMappingsForStop(hundredSecondsAgo, now, 0, 2000);
        Assert.assertEquals(2, allMappings.size());
    }

    @Test
    public void findByCodeParamEmpty() {

        // GIVEN
        StopPlace stopPlace = new StopPlace();
        String stopPlaceName = "exemple";
        stopPlace.setVersion(2L);
        stopPlace.setName(new EmbeddableMultilingualString(stopPlaceName, ""));

        stopPlaceRepository.save(stopPlace);

        String code = "test";

        ExportParams exportParams = newExportParamsBuilder().setStopPlaceSearch(newStopPlaceSearchBuilder()
                .setQuery(stopPlaceName)
                .build())
                .setCodeSpace(code)
                .build();

        // WHEN
        Page<StopPlace> searchResult = stopPlaceRepository.findStopPlace(exportParams);

        // THEN
        assertThat(searchResult).isEmpty();
    }


    private Quay saveQuay(StopPlace stopPlace, String id, Long version, String orgIdKeyName, String orgId) {
        Quay quay = new Quay(new EmbeddableMultilingualString("Quay"));
        stopPlace.getQuays().add(quay);


        quay.setNetexId(id);
        quay.setVersion(version);
        quay.getKeyValues().put(orgIdKeyName, new Value(orgId));
        stopPlaceRepository.save(stopPlace);
        return quay;
    }

    private void assertContainsOnlyInExactVersion(Collection<StopPlace> actual, StopPlace... expected) {
        List<StopPlace> expectedList = Arrays.asList(expected);
        Assert.assertEquals(expectedList.size(), actual.size());
        Assert.assertTrue(expectedList.stream().allMatch(expectedStop -> actual.stream().anyMatch(actualStop -> actualStop.getNetexId().equals(expectedStop.getNetexId()) && actualStop.getVersion() == expectedStop.getVersion())));
    }

    private StopPlace saveStop(String id, Long version, Instant startOfPeriod, Instant endOfPeriod) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(version);
        stopPlace.setNetexId(id);
        stopPlace.setValidBetween(new ValidBetween(startOfPeriod, endOfPeriod));
        return stopPlaceRepository.save(stopPlace);
    }

    private TopographicPlace createMunicipality(String municipalityName, TopographicPlace parentCounty) {
        TopographicPlace municipality = new TopographicPlace();
        municipality.setName(new EmbeddableMultilingualString(municipalityName, ""));

        if (parentCounty != null) {
            municipality.setParentTopographicPlaceRef(new TopographicPlaceRefStructure(parentCounty.getNetexId(), String.valueOf(parentCounty.getVersion())));
        }

        topographicPlaceRepository.save(municipality);
        return municipality;
    }

    private TopographicPlace createCounty(String countyName) {

        TopographicPlace county = new TopographicPlace();
        county.setName(new EmbeddableMultilingualString(countyName, ""));
        topographicPlaceRepository.save(county);

        return county;
    }

    private StopPlace createStopPlaceWithMunicipality(String name, TopographicPlace municipality) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString(name, ""));

        if (municipality != null) {
            stopPlace.setTopographicPlace(municipality);
        }

        stopPlaceRepository.save(stopPlace);

        return stopPlace;
    }

    private StopPlace createStopPlace(double latitude, double longitude) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
        return stopPlace;
    }
}
