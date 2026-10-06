package org.rutebanken.tiamat.versioning.save;

import org.junit.Test;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.GroupOfTariffZones;
import org.rutebanken.tiamat.model.PrivateCodeStructure;
import org.rutebanken.tiamat.model.Value;
import org.rutebanken.tiamat.repository.GroupOfTariffZonesRepository;
import org.rutebanken.tiamat.versioning.validate.VersionValidator;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** A replica update replaces the stored group: one row per netexId, all fields taken from the source. */
public class GroupOffTariffZonesSaverServiceExternalVersioningTest {

    private final GroupOfTariffZonesRepository repository = mock(GroupOfTariffZonesRepository.class);
    private final AuthorizationService authorizationService = mock(AuthorizationService.class);
    private final GroupOffTariffZonesSaverService saver = new GroupOffTariffZonesSaverService(repository,
            mock(DefaultVersionedSaverService.class), mock(VersionValidator.class), mock(UsernameFetcher.class),
            authorizationService);

    @Test
    public void olderVersionsAreDeleted() {
        GroupOfTariffZones older = group(1L, 1);
        GroupOfTariffZones existing = group(2L, 2);
        when(repository.findFirstByNetexIdOrderByVersionDesc("NSR:GroupOfTariffZones:1")).thenReturn(existing);
        when(repository.findByNetexId("NSR:GroupOfTariffZones:1")).thenReturn(List.of(existing, older));
        when(repository.save(existing)).thenReturn(existing);

        saver.saveWithExternalVersioning(group(null, 1));

        verify(authorizationService).verifyCanEditEntities(List.of(older));
        verify(repository).deleteAll(List.of(older));
        verify(repository).save(existing);
    }

    @Test
    public void fieldsAreReplaced() {
        GroupOfTariffZones existing = group(2L, 1);
        existing.setDescription(new EmbeddableMultilingualString("stale"));
        existing.setPrivateCode(new PrivateCodeStructure("stale", null));
        existing.setVersionComment("stale");
        existing.getKeyValues().put("stale", new Value("a"));
        when(repository.findFirstByNetexIdOrderByVersionDesc("NSR:GroupOfTariffZones:1")).thenReturn(existing);
        when(repository.findByNetexId("NSR:GroupOfTariffZones:1")).thenReturn(List.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        GroupOfTariffZones incoming = group(null, 2);
        incoming.setShortName(new EmbeddableMultilingualString("short"));
        incoming.getKeyValues().put("kept", new Value("b"));
        saver.saveWithExternalVersioning(incoming);

        assertThat(existing.getShortName().getValue()).isEqualTo("short");
        assertThat(existing.getDescription()).isNull();
        assertThat(existing.getPrivateCode()).isNull();
        assertThat(existing.getVersionComment()).isNull();
        assertThat(existing.getKeyValues()).containsOnlyKeys("kept");
    }

    private static GroupOfTariffZones group(Long id, long version) {
        GroupOfTariffZones group = new GroupOfTariffZones();
        ReflectionTestUtils.setField(group, "id", id);
        group.setNetexId("NSR:GroupOfTariffZones:1");
        group.setVersion(version);
        return group;
    }
}
