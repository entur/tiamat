package org.rutebanken.tiamat.versioning.save;

import org.junit.Test;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.rutebanken.tiamat.service.TariffZonesLookupService;
import org.rutebanken.tiamat.versioning.validate.VersionValidator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Older versions are deleted by an external versioning save, so the caller must be authorized for them too. */
public class FareZoneSaverServiceOlderVersionsTest {

    @Test
    public void olderVersionOutsideCallersScopeIsNotDeleted() {
        FareZone older = fareZone(1L, 1);
        FareZone existing = fareZone(2L, 2);
        FareZoneRepository repository = mock(FareZoneRepository.class);
        when(repository.findFirstByNetexIdOrderByVersionDesc("NSR:FareZone:1")).thenReturn(existing);
        when(repository.findByNetexId("NSR:FareZone:1")).thenReturn(List.of(existing, older));

        AuthorizationService authorizationService = mock(AuthorizationService.class);
        doAnswer(invocation -> {
            if (invocation.<Collection<?>>getArgument(0).contains(older)) {
                throw new AccessDeniedException("older version out of scope");
            }
            return null;
        }).when(authorizationService).verifyCanEditEntities(any());

        FareZoneSaverService saver = new FareZoneSaverService(repository, mock(TariffZonesLookupService.class),
                mock(DefaultVersionedSaverService.class), mock(VersionValidator.class), mock(UsernameFetcher.class),
                authorizationService);

        assertThatThrownBy(() -> saver.saveWithExternalVersioning(fareZone(null, 3)))
                .isInstanceOf(AccessDeniedException.class);
        verify(repository, never()).deleteAll(any());
        verify(repository, never()).save(any());
    }

    private static FareZone fareZone(Long id, long version) {
        FareZone fareZone = new FareZone();
        ReflectionTestUtils.setField(fareZone, "id", id);
        fareZone.setNetexId("NSR:FareZone:1");
        fareZone.setVersion(version);
        return fareZone;
    }
}
