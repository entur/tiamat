package org.rutebanken.tiamat.netex.mapping.mapper.mapStruct;

import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QuayMapperTest {

    /**
     * Ignored because the logic for handling incoming NSR IDs must be implemented differently.
     */
    @Ignore
    @Test
    public void mapNetexQuayIdToInternal() {
        org.rutebanken.netex.model.Quay netexQuay = new org.rutebanken.netex.model.Quay();

        String netexId = "NSR:Quay:12345";
        netexQuay.setId(netexId);

        org.rutebanken.tiamat.model.Quay tiamatQuay = netexMapper.mapToTiamatModel(netexQuay);

        assertThat(tiamatQuay.getNetexId()).isEqualTo("NSR:Quay:12345");
    }

    @Test
    public void mapInternalQuayIdToNetex() {

        org.rutebanken.tiamat.model.Quay tiamatQuay = new org.rutebanken.tiamat.model.Quay();
        String netexId = "NSR:Quay:" + 1234567;
        tiamatQuay.setNetexId(netexId);

        org.rutebanken.netex.model.Quay netexQuay = netexMapper.mapToNetexModel(tiamatQuay);
        assertThat(netexQuay.getId()).isNotNull();
        assertThat(netexQuay.getId()).isEqualTo(netexId);
    }

}
