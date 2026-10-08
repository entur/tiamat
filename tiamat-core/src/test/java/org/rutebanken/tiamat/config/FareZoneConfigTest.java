package org.rutebanken.tiamat.config;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class FareZoneConfigTest {

    @Test
    public void replicaImportIsOffByDefault() {
        assertThat(new FareZoneConfig().isReplicaImport(false)).isFalse();
    }

    @Test
    public void externalVersioningMakesEveryImportAReplica() {
        FareZoneConfig config = new FareZoneConfig();
        ReflectionTestUtils.setField(config, "externalVersioning", true);
        assertThat(config.isReplicaImport(false)).isTrue();
    }

    @Test
    public void registerImportFlagDoesNotMakeOtherImportsReplicas() {
        FareZoneConfig config = new FareZoneConfig();
        ReflectionTestUtils.setField(config, "registerImportEnabled", true);
        assertThat(config.isReplicaImport(false)).isFalse();
        assertThat(config.isReplicaImport(true)).isTrue();
    }
}
