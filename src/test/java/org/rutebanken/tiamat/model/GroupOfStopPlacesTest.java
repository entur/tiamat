package org.rutebanken.tiamat.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.springframework.orm.jpa.JpaSystemException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.catchThrowableOfType;


public class GroupOfStopPlacesTest extends TiamatIntegrationTest {


    @Test
    public void addStopPlacesToGroupOfStopPlaces() {

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Stop place 1"));
        stopPlace.setVersion(1L);
        stopPlace = stopPlaceRepository.save(stopPlace);

        StopPlace stopPlace2 = new StopPlace(new EmbeddableMultilingualString("Stop place 2"));
        stopPlace2.setVersion(1L);
        stopPlace2 = stopPlaceRepository.save(stopPlace2);

        Instant validityStart = Instant.now();
        Instant validityEnd = validityStart.plusSeconds(60*60*24*7); // Week of seconds

        String groupName = "group of stop places";
        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces(new EmbeddableMultilingualString(groupName));

        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace2.getNetexId()));

        groupOfStopPlaces.setValidBetween(new ValidBetween(validityStart, validityEnd));

        groupOfStopPlaces = groupOfStopPlacesRepository.save(groupOfStopPlaces);

        assertThat(groupOfStopPlaces.getName().getValue()).isEqualTo(groupName);

        assertThat(groupOfStopPlaces.getMembers())
                .extracting(StopPlaceReference::getRef)
                .contains(stopPlace.getNetexId(), stopPlace2.getNetexId());

        assertThat(groupOfStopPlaces.getValidBetween()).isEqualTo(new ValidBetween(validityStart, validityEnd));
    }

    @Test
    public void stopPlaceCouldBelongToMultipleGroups() {

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Stop place 1"));
        stopPlace.setVersion(1L);
        stopPlace = stopPlaceRepository.save(stopPlace);

        String groupName = "group of stop places 1";
        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces(new EmbeddableMultilingualString(groupName));
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));
        groupOfStopPlaces = groupOfStopPlacesRepository.save(groupOfStopPlaces);

        String groupName2 = "group of stop places 2";
        GroupOfStopPlaces groupOfStopPlaces2 = new GroupOfStopPlaces(new EmbeddableMultilingualString(groupName2));
        groupOfStopPlaces2.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));
        groupOfStopPlaces2 = groupOfStopPlacesRepository.save(groupOfStopPlaces2);

        assertThat(groupOfStopPlaces.getMembers())
                .extracting(StopPlaceReference::getRef)
                .contains(stopPlace.getNetexId());
        assertThat(groupOfStopPlaces2.getMembers())
                .extracting(StopPlaceReference::getRef)
                .contains(stopPlace.getNetexId());
    }


    @Test
    public void groupOfStopPlacesUniqueNameTest() {

        Instant validityStart = Instant.now();
        Instant validityEnd = validityStart.plusSeconds(60*60*24*7); // Week of seconds

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Stop place 1"));
        stopPlace.setVersion(1L);
        stopPlace = stopPlaceRepository.save(stopPlace);

        StopPlace stopPlace2 = new StopPlace(new EmbeddableMultilingualString("Stop place 2"));
        stopPlace2.setVersion(1L);
        stopPlace2 = stopPlaceRepository.save(stopPlace2);

        String groupName = "Unique name for group of stop places";
        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces(new EmbeddableMultilingualString(groupName));
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));
        groupOfStopPlaces.setValidBetween(new ValidBetween(validityStart, validityEnd));
        groupOfStopPlacesRepository.save(groupOfStopPlaces);

        GroupOfStopPlaces groupOfStopPlaces2 = new GroupOfStopPlaces(new EmbeddableMultilingualString(groupName));
        groupOfStopPlaces2.getMembers().add(new StopPlaceReference(stopPlace2.getNetexId()));
        groupOfStopPlaces2.setValidBetween(new ValidBetween(validityStart, validityEnd));
        var thrown = catchThrowableOfType(() -> groupOfStopPlacesRepository.save(groupOfStopPlaces2), JpaSystemException.class);

        assertThat(thrown.getMessage()).contains("Name " + groupName + " already exists for groups of stop places.");
    }

    @Test
    public void groupOfStopPlacesUniqueDescriptionTest() {

        Instant validityStart = Instant.now();
        Instant validityEnd = validityStart.plusSeconds(60*60*24*7); // Week of seconds

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Stop place 1"));
        stopPlace.setVersion(1L);
        stopPlace = stopPlaceRepository.save(stopPlace);

        StopPlace stopPlace2 = new StopPlace(new EmbeddableMultilingualString("Stop place 2"));
        stopPlace2.setVersion(1L);
        stopPlace2 = stopPlaceRepository.save(stopPlace2);

        String groupName = "Stop area 1";
        String description = "Unique description";
        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces(new EmbeddableMultilingualString(groupName));
        groupOfStopPlaces.setDescription(new EmbeddableMultilingualString(description));
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));
        groupOfStopPlaces.setValidBetween(new ValidBetween(validityStart, validityEnd));
        groupOfStopPlacesRepository.save(groupOfStopPlaces);

        String groupName2 = "Stop area 2";
        GroupOfStopPlaces groupOfStopPlaces2 = new GroupOfStopPlaces(new EmbeddableMultilingualString(groupName2));
        groupOfStopPlaces2.setDescription(new EmbeddableMultilingualString(description));
        groupOfStopPlaces2.getMembers().add(new StopPlaceReference(stopPlace2.getNetexId()));
        groupOfStopPlaces2.setValidBetween(new ValidBetween(validityStart, validityEnd));
        var thrown = catchThrowableOfType(() -> groupOfStopPlacesRepository.save(groupOfStopPlaces2), JpaSystemException.class);

        assertThat(thrown.getMessage()).contains("Description " + description + " already exists for groups of stop places.");
    }

    @Test
    public void allowSameNameForOldVersions() {
        Instant validityStart = Instant.now();
        Instant validityEnd = validityStart.plusSeconds(60*60*24*7); // Week of seconds

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Stop place 1"));
        stopPlace.setVersion(1L);
        stopPlace = stopPlaceRepository.save(stopPlace);

        StopPlace stopPlace2 = new StopPlace(new EmbeddableMultilingualString("Stop place 2"));
        stopPlace2.setVersion(1L);
        stopPlace2 = stopPlaceRepository.save(stopPlace2);

        StopPlace stopPlace3 = new StopPlace(new EmbeddableMultilingualString("Stop place 3"));
        stopPlace3.setVersion(1L);
        stopPlace3 = stopPlaceRepository.save(stopPlace3);

        String oldGroupName = "Old Stop Area";
        String oldGroupDescription = "Old Stop Description";
        GroupOfStopPlaces oldGroupOfStopPlaces = new GroupOfStopPlaces(new EmbeddableMultilingualString(oldGroupName));
        oldGroupOfStopPlaces.setDescription(new EmbeddableMultilingualString(oldGroupDescription));
        oldGroupOfStopPlaces.setVersion(1L);
        oldGroupOfStopPlaces.setNetexId("NSR:GroupOfStopAreas:50");
        oldGroupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));
        oldGroupOfStopPlaces.setValidBetween(new ValidBetween(validityStart, validityEnd));
        groupOfStopPlacesRepository.save(oldGroupOfStopPlaces);

        GroupOfStopPlaces newGroupOfStopPlaces = new GroupOfStopPlaces(new EmbeddableMultilingualString(oldGroupName));
        newGroupOfStopPlaces.setName(new EmbeddableMultilingualString("New Stop Area"));
        newGroupOfStopPlaces.setDescription(new EmbeddableMultilingualString("New Stop Description"));
        newGroupOfStopPlaces.setVersion(2L);
        newGroupOfStopPlaces.setNetexId("NSR:GroupOfStopAreas:50");
        newGroupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace2.getNetexId()));
        newGroupOfStopPlaces.setValidBetween(new ValidBetween(validityStart, validityEnd));
        groupOfStopPlacesRepository.save(newGroupOfStopPlaces);

        GroupOfStopPlaces groupOfStopPlaces2 = new GroupOfStopPlaces(new EmbeddableMultilingualString(oldGroupName));
        groupOfStopPlaces2.setDescription(new EmbeddableMultilingualString(oldGroupDescription));
        groupOfStopPlaces2.getMembers().add(new StopPlaceReference(stopPlace3.getNetexId()));
        groupOfStopPlaces2.setValidBetween(new ValidBetween(validityStart, validityEnd));

        assertThatNoException().isThrownBy(() -> groupOfStopPlacesRepository.save(groupOfStopPlaces2));
    }

    @Test
    public void allowSameNameForNonOverlappingDates() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Stop place 1"));
        stopPlace.setVersion(1L);
        stopPlace = stopPlaceRepository.save(stopPlace);

        StopPlace stopPlace2 = new StopPlace(new EmbeddableMultilingualString("Stop place 2"));
        stopPlace2.setVersion(1L);
        stopPlace2 = stopPlaceRepository.save(stopPlace2);

        String groupName = "Timed area";
        String groupDescription = "Timed description";

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime startDate = currentDate.minusDays(10);
        LocalDateTime endDate = currentDate.plusDays(10);

        GroupOfStopPlaces groupOfStopPlaces1 = new GroupOfStopPlaces(new EmbeddableMultilingualString(groupName));
        groupOfStopPlaces1.setDescription(new EmbeddableMultilingualString(groupDescription));
        groupOfStopPlaces1.setVersion(1L);
        groupOfStopPlaces1.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));
        groupOfStopPlaces1.setValidBetween(new ValidBetween(startDate.toInstant(ZoneOffset.UTC), currentDate.toInstant(ZoneOffset.UTC)));
        groupOfStopPlacesRepository.save(groupOfStopPlaces1);

        GroupOfStopPlaces groupOfStopPlaces2 = new GroupOfStopPlaces(new EmbeddableMultilingualString(groupName));
        groupOfStopPlaces2.setDescription(new EmbeddableMultilingualString(groupDescription));
        groupOfStopPlaces2.setVersion(1L);
        groupOfStopPlaces2.getMembers().add(new StopPlaceReference(stopPlace2.getNetexId()));
        groupOfStopPlaces2.setValidBetween(new ValidBetween(currentDate.toInstant(ZoneOffset.UTC), endDate.toInstant(ZoneOffset.UTC)));

        assertThatNoException().isThrownBy(() -> groupOfStopPlacesRepository.save(groupOfStopPlaces2));
    }
}
