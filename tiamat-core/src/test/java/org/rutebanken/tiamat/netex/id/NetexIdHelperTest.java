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

package org.rutebanken.tiamat.netex.id;

import org.junit.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;


public class NetexIdHelperTest {


    private NetexIdHelper netexIdHelper = new NetexIdHelper(new ValidPrefixList("PRE", new HashMap<>()));

    @Test
    public void extractIdPostfix() throws Exception {
        long last = netexIdHelper.extractIdPostfixNumeric("NOR:TariffZone:19215 ");
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
    public void idWithMoreThanThreeColonIsNotNetexId() {
        assertThat(NetexIdHelper.isNetexId("AVI:StopPlace:123:2")).isFalse();
    }

    @Test
    public void idWithLessThanThreeColonIsNotNetexId() {
        assertThat(NetexIdHelper.isNetexId("AVI:StopPlace321")).isFalse();
    }
}