package org.rutebanken.tiamat.versioning.save;

import org.junit.Test;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.rutebanken.tiamat.service.TariffZonesLookupService;
import org.rutebanken.tiamat.versioning.validate.VersionValidator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Older versions are deleted by an external versioning save, so the caller must be authorized for them too. */
public class FareZoneSaverServiceOlderVersionsTest {

    private final FareZone older = fareZone(1L, 1);
    private final FareZoneRepository repository = mock(FareZoneRepository.class);
    private final AuthorizationService authorizationService = mock(AuthorizationService.class);
    private final FareZoneSaverService saver = new FareZoneSaverService(repository, mock(TariffZonesLookupService.class),
            mock(DefaultVersionedSaverService.class), mock(VersionValidator.class), mock(UsernameFetcher.class),
            authorizationService);

    public FareZoneSaverServiceOlderVersionsTest() {
        FareZone existing = fareZone(2L, 2);
        when(repository.findFirstByNetexIdOrderByVersionDesc("NSR:FareZone:1")).thenReturn(existing);
        when(repository.findByNetexId("NSR:FareZone:1")).thenReturn(List.of(existing, older));

        doAnswer(invocation -> {
            if (invocation.<Collection<?>>getArgument(0).contains(older)) {
                throw new AccessDeniedException("older version out of scope");
            }
            return null;
        }).when(authorizationService).verifyCanEditEntities(any());
    }

    @Test
    public void olderVersionOutsideCallersScopeIsNotDeleted() {
        assertThatThrownBy(() -> saver.saveWithExternalVersioning(fareZone(null, 3)))
                .isInstanceOf(AccessDeniedException.class);
        verify(repository, never()).deleteAll(any());
        verify(repository, never()).save(any());
    }

    /** A rejected zone changes nothing, so older versions outside the caller's scope must not fail it. */
    @Test
    public void rejectedZoneIsNotFailedByOlderVersionOutsideCallersScope() {
        FareZone invalid = fareZone(null, 3);
        invalid.setValidBetween(new ValidBetween(Instant.now().plusSeconds(60), Instant.now()));

        assertThat(saver.saveWithExternalVersioning(invalid)).isNull();
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
