package org.rutebanken.tiamat.versioning;

import org.junit.Test;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.VersionIncrementor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StopPlaceVersionIncrementorTest {

    private StopPlaceVersionIncrementor stopPlaceVersionIncrementor = new StopPlaceVersionIncrementor(new VersionIncrementor());

    @Test
    public void incrementVersionIfNameChange() throws Exception {

        StopPlace existingStopPlace = new StopPlace();
        existingStopPlace.setVersion(1L);
        existingStopPlace.setNetexId("NSR:StopPlace:123");
        existingStopPlace.setName(new EmbeddableMultilingualString("Old name"));


        StopPlace changedStopPlace = new StopPlace();
        changedStopPlace.setVersion(existingStopPlace.getVersion());
        changedStopPlace.setNetexId(existingStopPlace.getNetexId());
        changedStopPlace.setName(new EmbeddableMultilingualString("New name"));

        stopPlaceVersionIncrementor.incrementVersion(existingStopPlace, changedStopPlace);


        assertThat(changedStopPlace.getVersion())
                .describedAs("The version should have been incremented, as the name has changed")
                .isEqualTo(existingStopPlace.getVersion()+1);

    }


    @Test
    public void keepVersionIfInsignificantChange() throws Exception {

        StopPlace existingStopPlace = new StopPlace();
        existingStopPlace.setVersion(1L);
        existingStopPlace.setNetexId("NSR:StopPlace:123");
        existingStopPlace.setName(new EmbeddableMultilingualString("Old Name"));

        StopPlace changedStopPlace = new StopPlace();
        changedStopPlace.setVersion(existingStopPlace.getVersion());
        changedStopPlace.setNetexId(existingStopPlace.getNetexId());
        changedStopPlace.setName(existingStopPlace.getName());

        stopPlaceVersionIncrementor.incrementVersion(existingStopPlace, changedStopPlace);


        assertThat(changedStopPlace.getVersion())
                .describedAs("The version should not have been incremented")
                .isEqualTo(existingStopPlace.getVersion());

    }
}