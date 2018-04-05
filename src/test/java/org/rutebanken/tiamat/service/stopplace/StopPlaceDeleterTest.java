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
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.changelog.EntityChangedListener;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.lock.MutateLock;

import java.util.Arrays;
import java.util.function.Supplier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.*;

public class StopPlaceDeleterTest {

    private StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);

    private EntityChangedListener entityChangedListener = mock(EntityChangedListener.class);
    private ReflectionAuthorizationService authorizationService = mock(ReflectionAuthorizationService.class);
    private UsernameFetcher usernameFetcher = mock(UsernameFetcher.class);
    private MutateLock mutateLock = new MutateLock(null) {
        @Override
        public <T> T executeInLock(Supplier<T> supplier) {
            return supplier.get();
        }
    };

    private StopPlaceDeleter stopPlaceDeleter = new StopPlaceDeleter(stopPlaceRepository, entityChangedListener, authorizationService, usernameFetcher, mutateLock);

    @Test(expected = IllegalArgumentException.class)
    public void doNotDeleteParent() {
        StopPlace parent = new StopPlace();
        parent.setParentStopPlace(true);
        parent.setNetexId("NSR:StopPlace:1");

        when(stopPlaceRepository.findAll(anyListOf(String.class))).thenReturn(Arrays.asList(parent));

        stopPlaceDeleter.deleteStopPlace(parent.getNetexId());
    }

    @Test
    public void deleteMonomodalStopPlace() {
        StopPlace monoModalStopPlace = new StopPlace();
        monoModalStopPlace.setNetexId("NSR:StopPlace:");

        when(stopPlaceRepository.findAll(anyListOf(String.class))).thenReturn(Arrays.asList(monoModalStopPlace));
        when(usernameFetcher.getUserNameForAuthenticatedUser()).thenReturn("Rambo");

        boolean deleted = stopPlaceDeleter.deleteStopPlace(monoModalStopPlace.getNetexId());

        assertThat(deleted).isTrue();

        verify(stopPlaceRepository, times(1)).delete(anyList());
    }

}