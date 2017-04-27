package org.rutebanken.tiamat.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class VersionOfObjectRefStructureTest {
    @Test
    public void equals() throws Exception {

        VersionOfObjectRefStructure ref1 = new VersionOfObjectRefStructure("ref", "version");
        VersionOfObjectRefStructure ref2 = new VersionOfObjectRefStructure("ref", "version2");

        // Should two references with different versions be equal?
        assertThat(ref1).isEqualTo(ref2);
    }

}