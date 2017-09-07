package org.rutebanken.tiamat.service.stopplace;

import org.junit.Test;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.changelog.EntityChangedListener;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.stopplace.StopPlaceDeleter;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.*;

public class StopPlaceDeleterTest {

    private StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);

    private EntityChangedListener entityChangedListener = mock(EntityChangedListener.class);
    private ReflectionAuthorizationService authorizationService = mock(ReflectionAuthorizationService.class);
    private UsernameFetcher usernameFetcher = mock(UsernameFetcher.class);
    private StopPlaceDeleter stopPlaceDeleter = new StopPlaceDeleter(stopPlaceRepository, entityChangedListener, authorizationService, usernameFetcher);

    @Test(expected = IllegalArgumentException.class)
    public void doNotDeleteParent() {
        StopPlace parent = new StopPlace();
        parent.setParentStopPlace(true);
        parent.setNetexId(NetexIdHelper.generateRandomizedNetexId(parent));

        when(stopPlaceRepository.findAll(anyListOf(String.class))).thenReturn(Arrays.asList(parent));

        stopPlaceDeleter.deleteStopPlace(parent.getNetexId());
    }

    @Test
    public void deleteMonomodalStopPlace() {
        StopPlace monoModalStopPlace = new StopPlace();
        monoModalStopPlace.setNetexId(NetexIdHelper.generateRandomizedNetexId(monoModalStopPlace));

        when(stopPlaceRepository.findAll(anyListOf(String.class))).thenReturn(Arrays.asList(monoModalStopPlace));
        when(usernameFetcher.getUserNameForAuthenticatedUser()).thenReturn("Rambo");

        boolean deleted = stopPlaceDeleter.deleteStopPlace(monoModalStopPlace.getNetexId());

        assertThat(deleted).isTrue();

        verify(stopPlaceRepository, times(1)).delete(anyList());
    }

}