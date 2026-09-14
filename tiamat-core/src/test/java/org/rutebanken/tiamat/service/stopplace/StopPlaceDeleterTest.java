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

package org.rutebanken.tiamat.service.stopplace;

import org.junit.Test;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.changelog.EntityChangedListener;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.parking.ParkingDeleter;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class StopPlaceDeleterTest extends TiamatIntegrationTest {

    private StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);

    private EntityChangedListener entityChangedListener = mock(EntityChangedListener.class);
    private AuthorizationService authorizationService = mock(AuthorizationService.class);
    private UsernameFetcher usernameFetcher = mock(UsernameFetcher.class);
    private MutateLock mutateLock = new MutateLock(null) {
        @Override
        public <T> T executeInLock(Supplier<T> supplier) {
            return supplier.get();
        }
    };

    private ParkingRepository parkingRepository = mock(ParkingRepository.class);
    private ParkingDeleter parkingDeleter = mock(ParkingDeleter.class);

    private StopPlaceDeleter stopPlaceDeleter = new StopPlaceDeleter(stopPlaceRepository, entityChangedListener, authorizationService, usernameFetcher, mutateLock, parkingRepository, parkingDeleter);

    @Test(expected = IllegalArgumentException.class)
    public void doNotDeleteParent() {
        StopPlace parent = new StopPlace();
        parent.setParentStopPlace(true);
        parent.setNetexId("NSR:StopPlace:1");

        when(stopPlaceRepository.findAll(anyList())).thenReturn(Collections.singletonList(parent));

        stopPlaceDeleter.deleteStopPlace(parent.getNetexId());
    }

    @Test
    @Transactional
    public void deleteMonomodalStopPlace() {
        StopPlace monoModalStopPlace = new StopPlace();
        monoModalStopPlace.setNetexId("NSR:StopPlace:");

        when(stopPlaceRepository.findAll(anyList())).thenReturn(Collections.singletonList(monoModalStopPlace));
        when(usernameFetcher.getUserNameForAuthenticatedUser()).thenReturn("Rambo");

        boolean deleted = stopPlaceDeleter.deleteStopPlace(monoModalStopPlace.getNetexId());

        assertThat(deleted).isTrue();

        verify(stopPlaceRepository, times(1)).deleteAll(anyList());
    }

    @Test
    @Transactional
    public void deletingStopPlaceCascadesToReferencingParkings() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:42");

        when(stopPlaceRepository.findAll(anyList())).thenReturn(Collections.singletonList(stopPlace));
        when(usernameFetcher.getUserNameForAuthenticatedUser()).thenReturn("Rambo");
        when(parkingRepository.findByStopPlaceNetexId("NSR:StopPlace:42"))
                .thenReturn(Arrays.asList("NSR:Parking:1", "NSR:Parking:2"));

        assertThat(stopPlaceDeleter.deleteStopPlace(stopPlace.getNetexId())).isTrue();

        verify(parkingDeleter, times(1)).deleteParking("NSR:Parking:1");
        verify(parkingDeleter, times(1)).deleteParking("NSR:Parking:2");
        verify(stopPlaceRepository, times(1)).deleteAll(anyList());
    }

    @Test
    @Transactional
    public void deletingStopPlaceWithoutParkingsDeletesNoParkings() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:43");

        when(stopPlaceRepository.findAll(anyList())).thenReturn(Collections.singletonList(stopPlace));
        when(usernameFetcher.getUserNameForAuthenticatedUser()).thenReturn("Rambo");
        when(parkingRepository.findByStopPlaceNetexId("NSR:StopPlace:43")).thenReturn(Collections.emptyList());

        assertThat(stopPlaceDeleter.deleteStopPlace(stopPlace.getNetexId())).isTrue();

        verifyNoInteractions(parkingDeleter);
    }

}