package org.rutebanken.tiamat.netex.id;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class NetexIdHelperTest {
    @Test
    public void extractIdPostfix() throws Exception {
        long last = NetexIdHelper.extractIdPostfixNumeric("NOR:TariffZone:19215 ");
        assertThat(last).isEqualTo(19215L);
    }

}