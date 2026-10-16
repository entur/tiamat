package org.rutebanken.tiamat.versioning.save;

import org.junit.Test;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.rutebanken.tiamat.service.TariffZonesLookupService;
import org.rutebanken.tiamat.versioning.validate.VersionValidator;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** The scheduled register import has no user, so it must not depend on the user edit check. */
public class FareZoneSaverServiceRegisterImportTest {

    private final FareZoneRepository repository = mock(FareZoneRepository.class);
    private final AuthorizationService denyAll = mock(AuthorizationService.class);
    private final FareZoneSaverService saver = new FareZoneSaverService(repository, mock(TariffZonesLookupService.class),
            mock(DefaultVersionedSaverService.class), mock(VersionValidator.class), mock(UsernameFetcher.class), denyAll);

    {
        doThrow(new AccessDeniedException("denied")).when(denyAll).verifyCanEditEntities(anyCollection());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.findAll()).thenReturn(List.of(fareZone("NSR:FareZone:orphan")));
    }

    @Test
    public void registerImportSavesAndPrunesWithoutAUser() {
        assertThat(saver.saveWithExternalVersioning(fareZone("NSR:FareZone:1"), true)).isNotNull();
        assertThat(saver.deleteAllExcept(Set.of("NSR:FareZone:1"), true)).isEqualTo(1);
    }

    @Test
    public void otherImportsStillNeedEditRights() {
        assertThatThrownBy(() -> saver.saveWithExternalVersioning(fareZone("NSR:FareZone:1"), false))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> saver.deleteAllExcept(Set.of("NSR:FareZone:1"), false))
                .isInstanceOf(AccessDeniedException.class);
    }

    private static FareZone fareZone(String netexId) {
        FareZone fareZone = new FareZone();
        fareZone.setNetexId(netexId);
        return fareZone;
    }
}
