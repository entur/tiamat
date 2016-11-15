package org.rutebanken.tiamat.importers;

import org.rutebanken.tiamat.importers.TopographicPlaceCreator;
import org.rutebanken.tiamat.importers.TopographicPlaceFromRefFinder;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TopographicPlaceCreatorTest {

    private TopographicPlaceFromRefFinder topographicPlaceFromRefFinder = new TopographicPlaceFromRefFinder();


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
        Long existingMunicipalityId = 1L;
        TopographicPlace existingMunicipality = createTopographicPlace(existingMunicipalityId, countryRef, null, municipalityName);
        mockFindByNameValueAndCountryRefRefAndTopographicPlaceType(topographicPlaceRepository, existingMunicipality);

        // Incoming municipality
        Long incomingMunicipalityId = 2L;
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
        assertThat(actualMunicipality.getId()).isNotEqualTo(incomingMunicipality.getId());

        // Assert that the topographic place that was returned has the ID of the already saved municipality
        assertThat(actualMunicipality.getId()).isEqualTo(existingMunicipality.getId());
    }

    /**
     * Save new municipality.
     */
    @Test
    public void findOrCreateTopographicPlace_saveNew() throws Exception {
        // Arrange
        TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);
        TopographicPlaceCreator topographicPlaceCreator = topographicPlaceCreator(topographicPlaceRepository);
        Long incomingMunicipalityId = 1L;
        TopographicPlace incomingMunicipality = createTopographicPlace(incomingMunicipalityId, createCountryRef(), null, "Incoming municipality");
        TopographicPlaceRefStructure incomingMunicipalityRef = createTopographicPlaceRef(incomingMunicipality);

        mockSaveAnyTopographicPlace(topographicPlaceRepository, new AtomicLong(10));
        List<TopographicPlace> places = singletonList(incomingMunicipality);

        // Act
        TopographicPlace actualMunicipality = topographicPlaceCreator.findOrCreateTopographicPlace(places, incomingMunicipalityRef, new AtomicInteger()).get();

        // Assert
        assertThat(actualMunicipality).isNotNull();

        // Id should have been saved, hence not same as incoming.
        assertThat(actualMunicipality.getId()).isNotEqualTo(incomingMunicipality.getId());
        assertThat(actualMunicipality.getId()).isNotNull();
        assertThat(actualMunicipality.getId()).isNotEqualTo(0L);
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
        Long incomingCountyId = 10L;
        TopographicPlace incomingCounty = createTopographicPlace(incomingCountyId, createCountryRef(), null, "Incoming, unsaved county");
        TopographicPlaceRefStructure incomingCountyRef = createTopographicPlaceRef(incomingCounty);

        // Municipality
        Long incomingMunicipalityId = 11L;
        TopographicPlace incomingMunicipality = createTopographicPlace(incomingMunicipalityId, createCountryRef(), incomingCountyRef, "Incoming municipality with reference to County");
        TopographicPlaceRefStructure incomingMunicipalityRef = createTopographicPlaceRef(incomingMunicipality);


        final List<TopographicPlace> savedPlaces = new ArrayList<>(2);
        when(topographicPlaceRepository.save(any(TopographicPlace.class)))
                .then(invocationOnMock -> {

                    TopographicPlace topographicPlace = (TopographicPlace) invocationOnMock.getArguments()[0];
                    System.out.println("Saving topographical place '" + topographicPlace.getName()+"'");
                    topographicPlace.setId(idCounter.incrementAndGet());
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
        assertThat(actualMunicipality.getParentTopographicPlaceRef()).isNotNull();
        TopographicPlaceRefStructure actualCountyRef = actualMunicipality.getParentTopographicPlaceRef();

        assertThat(idCounter.get()).isEqualTo(2);
        assertThat(savedPlaces).hasSize(2);
        assertThat(savedPlaces).extracting(EntityStructure::getId).contains(Long.valueOf(actualCountyRef.getRef()));
        assertThat(savedPlaces).extracting(topographicPlace -> topographicPlace.getName().getValue()).contains(incomingCounty.getName().getValue());
    }

    @Test
    public void findOrCreateTopographicPlace_saveNewWithExistingCountyRef() throws InterruptedException, ExecutionException {
        // Arrange
        TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);
        TopographicPlaceCreator topographicPlaceCreator = topographicPlaceCreator(topographicPlaceRepository);

        // Existing county
        Long existingCountyId = 11L;
        TopographicPlace existingCounty = createTopographicPlace(existingCountyId, createCountryRef(), null, "Akershus");
        mockFindByNameValueAndCountryRefRefAndTopographicPlaceType(topographicPlaceRepository, existingCounty);

        // Incoming county
        Long incomingCountyId = 13L;
        TopographicPlace incomingCounty = createTopographicPlace(incomingCountyId, createCountryRef(), null, "Akershus");
        TopographicPlaceRefStructure incomingCountyRef = createTopographicPlaceRef(incomingCounty);

        // Incoming municipality
        Long incomingMunicipalityId = 14L;
        TopographicPlace incomingMunicipality  = createTopographicPlace(incomingMunicipalityId, createCountryRef(), incomingCountyRef, "Asker");
        TopographicPlaceRefStructure incomingMunicipalityRef = createTopographicPlaceRef(incomingMunicipality);

        List<TopographicPlace> places = Arrays.asList(incomingMunicipality, incomingCounty);

        // Act
        TopographicPlace actualMunicipality = topographicPlaceCreator.findOrCreateTopographicPlace(places, incomingMunicipalityRef, new AtomicInteger()).get();

        // Assert
        assertThat(actualMunicipality.getParentTopographicPlaceRef().getRef()).isEqualTo(existingCounty.getId().toString());
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
        Long existingCountyId = 11L;
        TopographicPlace existingMunicipality = createTopographicPlace(existingCountyId, countryRef, null, municipalityName);
        mockFindByNameValueAndCountryRefRefAndTopographicPlaceType(topographicPlaceRepository, existingMunicipality);

        Long incomingMunicipalityId = 14L;
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
                    topographicPlace.setId(idCounter.incrementAndGet());
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

    private TopographicPlace createTopographicPlace(Long id, CountryRef countryRef,
                                                    TopographicPlaceRefStructure parentTopographicPlaceRef,
                                                    String name) {
        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setId(id);
        topographicPlace.setName(new MultilingualString(name, "no"));
        topographicPlace.setParentTopographicPlaceRef(parentTopographicPlaceRef);
        topographicPlace.setCountryRef(countryRef);
        return topographicPlace;
    }

    private TopographicPlaceRefStructure createTopographicPlaceRef(TopographicPlace county) {
        TopographicPlaceRefStructure topographicPlaceRef = new TopographicPlaceRefStructure();
        topographicPlaceRef.setRef(String.valueOf(county.getId()));
        return topographicPlaceRef;
    }


    private CountryRef createCountryRef() {
        CountryRef countryRef = new CountryRef();
        countryRef.setRef(IanaCountryTldEnumeration.NO);
        return countryRef;
    }


}