package org.rutebanken.tiamat.importer;

import org.junit.Ignore;
import org.junit.Test;
import org.rutebanken.tiamat.importer.finder.TopographicPlaceFromRefFinder;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TopographicPlaceCreatorTest {

    /**
     * When importing a municipality that is already saved, return the existing one.
     */
    @Test
    public void findOrCreateTopographicPlace_returnSaved() throws Exception {

        // Arrange
        TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);
        TopographicPlaceCreator topographicPlaceCreator = topographicPlaceCreator(topographicPlaceRepository);

        CountryRef countryRef = createCountryRef();

        String municipalityName = "Asker";

        // Already saved municipality.
        String existingMunicipalityId = "1";
        TopographicPlace existingMunicipality = createTopographicPlace(existingMunicipalityId, countryRef, null, municipalityName);
        mockFindByNameValueAndCountryRefRefAndTopographicPlaceType(topographicPlaceRepository, existingMunicipality);

        // Incoming municipality
        String incomingMunicipalityId = "2";
        TopographicPlace incomingMunicipality = createTopographicPlace(incomingMunicipalityId, countryRef, null, municipalityName);
        TopographicPlaceRefStructure incomingMunicipalityRef = createTopographicPlaceRef(incomingMunicipality);
        mockSaveAnyTopographicPlace(topographicPlaceRepository, new AtomicLong());

        List<TopographicPlace> places = singletonList(incomingMunicipality);

        // Act
        AtomicInteger topographicPlacesCreatedCounter = new AtomicInteger();
        TopographicPlace actualMunicipality = topographicPlaceCreator.findOrCreateTopographicPlace(places, incomingMunicipalityRef, topographicPlacesCreatedCounter).get();

        // Assert

        assertThat(topographicPlacesCreatedCounter.get()).isEqualTo(0);

        // The name should be the same
        assertThat(actualMunicipality.getName().getValue()).isEqualTo(incomingMunicipality.getName().getValue());

        // Assert that the ID of the municipality is not the same as the ID from import.
        assertThat(actualMunicipality.getNetexId()).isNotEqualTo(incomingMunicipality.getNetexId());

        // Assert that the topographic place that was returned has the ID of the already saved municipality
        assertThat(actualMunicipality.getNetexId()).isEqualTo(existingMunicipality.getNetexId());
    }

    /**
     * Save new municipality.
     */
    @Test
    public void findOrCreateTopographicPlace_saveNew() throws Exception {
        // Arrange
        TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);
        TopographicPlaceCreator topographicPlaceCreator = topographicPlaceCreator(topographicPlaceRepository);
        String incomingMunicipalityId = "1";
        TopographicPlace incomingMunicipality = createTopographicPlace(incomingMunicipalityId, createCountryRef(), null, "Incoming municipality");
        TopographicPlaceRefStructure incomingMunicipalityRef = createTopographicPlaceRef(incomingMunicipality);

        mockSaveAnyTopographicPlace(topographicPlaceRepository, new AtomicLong(10));
        List<TopographicPlace> places = singletonList(incomingMunicipality);

        // Act
        TopographicPlace actualMunicipality = topographicPlaceCreator.findOrCreateTopographicPlace(places, incomingMunicipalityRef, new AtomicInteger()).get();

        // Assert
        assertThat(actualMunicipality).isNotNull();

        // Id should have been saved, hence not same as incoming.
        assertThat(actualMunicipality.getNetexId()).isNotEqualTo(incomingMunicipality.getNetexId());
        assertThat(actualMunicipality.getNetexId()).isNotNull();
        assertThat(actualMunicipality.getNetexId()).isNotEqualTo(0L);
    }

    @Test
    public void findOrCreateTopographicPlace_handleNull() throws Exception {
        // Arrange
        TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);
        TopographicPlaceCreator topographicPlaceCreator = topographicPlaceCreator(topographicPlaceRepository);

        TopographicPlaceRefStructure topographicPlace = new TopographicPlaceRefStructure();
        topographicPlace.setRef(null);

        // Act
        Optional<TopographicPlace> actual = topographicPlaceCreator.findOrCreateTopographicPlace(Arrays.asList(), topographicPlace, new AtomicInteger());

        // Assert
        assertThat(actual).isEmpty();
    }


    /**
     * Save both municipality and county.
     */
    @Test
    public void findOrCreateTopographicPlace_saveNewWithCountyRef() throws InterruptedException, ExecutionException {
        // Arrange
        TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);
        TopographicPlaceCreator topographicPlaceCreator = topographicPlaceCreator(topographicPlaceRepository);
        AtomicLong idCounter = new AtomicLong();

        // County
        String incomingCountyId = "10";
        TopographicPlace incomingCounty = createTopographicPlace(incomingCountyId, createCountryRef(), null, "Incoming, unsaved county");

        // Municipality
        String incomingMunicipalityId = "11";
        TopographicPlace incomingMunicipality = createTopographicPlace(incomingMunicipalityId, createCountryRef(), incomingCounty, "Incoming municipality with reference to County");
        TopographicPlaceRefStructure incomingMunicipalityRef = createTopographicPlaceRef(incomingMunicipality);


        final List<TopographicPlace> savedPlaces = new ArrayList<>(2);
        when(topographicPlaceRepository.save(any(TopographicPlace.class)))
                .then(invocationOnMock -> {

                    TopographicPlace topographicPlace = (TopographicPlace) invocationOnMock.getArguments()[0];
                    System.out.println("Saving topographical place '" + topographicPlace.getName()+"'");
                    topographicPlace.setNetexId(String.valueOf(idCounter.incrementAndGet()));
                    savedPlaces.add(topographicPlace);
                    return topographicPlace;
                });

        List<TopographicPlace> places = Arrays.asList(incomingCounty, incomingMunicipality);

        AtomicInteger topographicPlacesCreatedCounter = new AtomicInteger();

        // Act
        TopographicPlace actualMunicipality = topographicPlaceCreator.findOrCreateTopographicPlace(places, incomingMunicipalityRef, topographicPlacesCreatedCounter).get();

        // Assert
        assertThat(actualMunicipality).isNotNull();

        // Id should have been saved, so it should not be the same as the incoming place.
        assertThat(actualMunicipality.getParentTopographicPlace()).isNotNull();
        TopographicPlace actualCountyRef = actualMunicipality.getParentTopographicPlace();

        assertThat(idCounter.get()).isEqualTo(2);
        assertThat(savedPlaces).hasSize(2);
        assertThat(savedPlaces).extracting(EntityStructure::getNetexId).contains(actualCountyRef.getNetexId());
        assertThat(savedPlaces).extracting(topographicPlace -> topographicPlace.getName().getValue()).contains(incomingCounty.getName().getValue());
    }

    @Test
    public void findOrCreateTopographicPlace_saveNewWithExistingCountyRef() throws InterruptedException, ExecutionException {
        // Arrange
        TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);
        TopographicPlaceCreator topographicPlaceCreator = topographicPlaceCreator(topographicPlaceRepository);

        // Existing county
        String existingCountyId = "11";
        TopographicPlace existingCounty = createTopographicPlace(existingCountyId, createCountryRef(), null, "Akershus");
        mockFindByNameValueAndCountryRefRefAndTopographicPlaceType(topographicPlaceRepository, existingCounty);

        // Incoming county
        String incomingCountyId = "13";
        TopographicPlace incomingCounty = createTopographicPlace(incomingCountyId, createCountryRef(), null, "Akershus");

        // Incoming municipality
        String incomingMunicipalityId = "14";
        TopographicPlace incomingMunicipality  = createTopographicPlace(incomingMunicipalityId, createCountryRef(), incomingCounty, "Asker");
        TopographicPlaceRefStructure incomingMunicipalityRef = createTopographicPlaceRef(incomingMunicipality);

        List<TopographicPlace> places = Arrays.asList(incomingMunicipality, incomingCounty);

        // Act
        TopographicPlace actualMunicipality = topographicPlaceCreator.findOrCreateTopographicPlace(places, incomingMunicipalityRef, new AtomicInteger()).get();

        // Assert
        assertThat(actualMunicipality.getParentTopographicPlace().getNetexId()).isEqualTo(existingCounty.getNetexId());
    }


    @Ignore
    @Test
    public void findOrCreateTopographicPlace_hammer() throws Exception {
        // Arrange
        TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);
        TopographicPlaceCreator topographicPlaceCreator = topographicPlaceCreator(topographicPlaceRepository);

        CountryRef countryRef = createCountryRef();

        String municipalityName = "Asker";

        // Already saved municipality.
        String existingCountyId = "11";
        TopographicPlace existingMunicipality = createTopographicPlace(existingCountyId, countryRef, null, municipalityName);
        mockFindByNameValueAndCountryRefRefAndTopographicPlaceType(topographicPlaceRepository, existingMunicipality);

        String incomingMunicipalityId = "14";
        TopographicPlace incomingMunicipality = createTopographicPlace(incomingMunicipalityId, countryRef, null, municipalityName);

        List<TopographicPlace> places = singletonList(incomingMunicipality);
        AtomicInteger topographicPlacesCreatedCounter = new AtomicInteger();
        AtomicInteger actuals = new AtomicInteger();

        ExecutorService executorService = Executors.newFixedThreadPool(10000);

        int numberOfRefs = 1000000;
        for(int i = 0; i < numberOfRefs; i ++) {

            TopographicPlaceRefStructure incomingMunicipalityRef = createTopographicPlaceRef(incomingMunicipality);
            executorService.submit((Runnable) () -> {
                try {
                    Optional<TopographicPlace> actualMunicipality = topographicPlaceCreator.findOrCreateTopographicPlace(places, incomingMunicipalityRef, topographicPlacesCreatedCounter);
                    actualMunicipality.ifPresent((municipality) -> actuals.incrementAndGet());

                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.SECONDS);

        // Assert
        assertThat(topographicPlacesCreatedCounter.get()).isEqualTo(0);
        assertThat(actuals.get()).isEqualTo(numberOfRefs);
    }

    private TopographicPlaceCreator topographicPlaceCreator(TopographicPlaceRepository topographicPlaceRepository) {
        return new TopographicPlaceCreator(new TopographicPlaceFromRefFinder(),
                topographicPlaceRepository);
    }


    private void mockSaveAnyTopographicPlace(TopographicPlaceRepository repository, AtomicLong idCounter) {
        when(repository.save(any(TopographicPlace.class)))
                .then(invocationOnMock -> {

                    TopographicPlace topographicPlace = (TopographicPlace) invocationOnMock.getArguments()[0];
                    System.out.println("Saving topographical place '" + topographicPlace.getName()+"'");
                    topographicPlace.setNetexId(String.valueOf(idCounter.incrementAndGet()));
                    return topographicPlace;
                });
    }

    private void mockFindByNameValueAndCountryRefRefAndTopographicPlaceType(TopographicPlaceRepository repository, TopographicPlace topographicPlace) {
        when(repository.findByNameValueAndCountryRefRefAndTopographicPlaceType(
                topographicPlace.getName().getValue(),
                topographicPlace.getCountryRef().getRef(),
                topographicPlace.getTopographicPlaceType()))
                .thenReturn(singletonList(topographicPlace));
    }

    private TopographicPlace createTopographicPlace(String id, CountryRef countryRef,
                                                    TopographicPlace parentTopographicPlace,
                                                    String name) {
        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setNetexId(id);
        topographicPlace.setName(new EmbeddableMultilingualString(name, "no"));
        topographicPlace.setParentTopographicPlace(parentTopographicPlace);
        topographicPlace.setCountryRef(countryRef);
        return topographicPlace;
    }

    private TopographicPlaceRefStructure createTopographicPlaceRef(TopographicPlace county) {
        TopographicPlaceRefStructure topographicPlaceRef = new TopographicPlaceRefStructure();
        topographicPlaceRef.setRef(String.valueOf(county.getNetexId()));
        return topographicPlaceRef;
    }


    private CountryRef createCountryRef() {
        CountryRef countryRef = new CountryRef();
        countryRef.setRef(IanaCountryTldEnumeration.NO);
        return countryRef;
    }


}