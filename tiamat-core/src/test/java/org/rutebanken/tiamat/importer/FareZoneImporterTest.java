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

package org.rutebanken.tiamat.importer;

import ma.glasnost.orika.MapperFacade;
import org.junit.Test;
import org.rutebanken.tiamat.config.FareZoneConfig;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.versioning.save.FareZoneSaverService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FareZoneImporterTest {

    /**
     * A replica import is a full replace: the declared set (cleanup keep-set) must contain every zone the
     * delivery declares, including one that fails to save, so deleteAllExcept does not prune the rejected
     * zone's existing version as an orphan. The saved set, used for group member validation, must contain
     * only persisted zones, so a rejected new zone cannot satisfy a dangling member reference.
     */
    @Test
    public void declaredSetKeepsRejectedZoneButSavedSetDoesNot() {
        FareZoneSaverService saver = mock(FareZoneSaverService.class);
        NetexMapper netexMapper = mock(NetexMapper.class);
        MapperFacade facade = mock(MapperFacade.class);
        when(netexMapper.getFacade()).thenReturn(facade);
        when(facade.map(any(), eq(org.rutebanken.netex.model.FareZone.class)))
                .thenReturn(new org.rutebanken.netex.model.FareZone());

        org.rutebanken.tiamat.model.FareZone good = new org.rutebanken.tiamat.model.FareZone();
        good.setNetexId("NSR:FareZone:good");
        org.rutebanken.tiamat.model.FareZone rejected = new org.rutebanken.tiamat.model.FareZone();
        rejected.setNetexId("NSR:FareZone:rejected");

        // External versioning preserves the source id across the save; the rejected zone returns null.
        when(saver.saveWithExternalVersioning(good)).thenReturn(good);
        when(saver.saveWithExternalVersioning(rejected)).thenReturn(null);

        FareZoneConfig replicaConfig = new FareZoneConfig();
        ReflectionTestUtils.setField(replicaConfig, "registerImportEnabled", true);

        FareZoneImporter importer = new FareZoneImporter();
        ReflectionTestUtils.setField(importer, "fareZoneSaverService", saver);
        ReflectionTestUtils.setField(importer, "netexMapper", netexMapper);
        ReflectionTestUtils.setField(importer, "fareZoneConfig", replicaConfig);

        FareZoneImportResult result = importer.importFareZones(List.of(good, rejected));

        // Cleanup keeps both, so the rejected zone's existing version is not pruned.
        assertThat(result.getDeclaredNetexIds())
                .containsExactlyInAnyOrder("NSR:FareZone:good", "NSR:FareZone:rejected");
        // Reference validation sees only the saved zone, so the rejected one cannot resolve a member ref.
        assertThat(result.getSavedNetexIds())
                .containsExactly("NSR:FareZone:good");
        // Only the saved zone makes it into the response frame.
        assertThat(result.getImportedFareZones()).hasSize(1);
    }
}
