package org.rutebanken.tiamat.netex.id;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class NetexIdHelperTest {
    @Test
    public void extractIdPostfix() throws Exception {
        long last = NetexIdHelper.extractIdPostfixNumeric("NOR:TariffZone:19215 ");
        assertThat(last).isEqualTo(19215L);
    }


    @Test
    public void stopPlaceIdIsNetexId() {
        assertThat(NetexIdHelper.isNetexId("RUT:StopPlace:313")).isTrue();
    }

    @Test
    public void quayIdIsNetexId() {
        assertThat(NetexIdHelper.isNetexId("RUT:Quay:313")).isTrue();
    }

    @Test
    public void idWithStringPostfixIsNetexId() {
        assertThat(NetexIdHelper.isNetexId("AVI:StopPlace:OSL")).isTrue();
    }

    @Test
    public void idWithThreeColonIsNotNetexId() {
        assertThat(NetexIdHelper.isNetexId("AVI:StopPlace:OSL")).isTrue();
    }
}