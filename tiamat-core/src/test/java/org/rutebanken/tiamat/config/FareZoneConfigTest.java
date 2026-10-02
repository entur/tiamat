package org.rutebanken.tiamat.config;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class FareZoneConfigTest {

    @Test
    public void replicaImportIsOffByDefault() {
        assertThat(new FareZoneConfig().isReplicaImport()).isFalse();
    }

    @Test
    public void legacyExternalVersioningMeansReplicaImport() {
        FareZoneConfig config = new FareZoneConfig();
        ReflectionTestUtils.setField(config, "externalVersioning", true);
        assertThat(config.isReplicaImport()).isTrue();
    }

    @Test
    public void registerImportMeansReplicaImportWithoutExternalVersioning() {
        FareZoneConfig config = new FareZoneConfig();
        ReflectionTestUtils.setField(config, "registerImportEnabled", true);
        assertThat(config.isReplicaImport()).isTrue();
        assertThat(config.isExternalVersioning()).isFalse();
    }
}
