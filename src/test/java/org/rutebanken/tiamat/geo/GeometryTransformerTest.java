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

package org.rutebanken.tiamat.geo;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GeometryTransformerTest {

    @Test
    public void findUtmZone32() {
        // Nesbru, Asker: 59.858690, 10.493860
        String zone = GeometryTransformer.findUtmCrs(10.493860);
        assertThat(zone).isEqualTo("EPSG:32632");
    }

    @Test
    public void findUtmZone33() {
        // Somewhere in Narvik: 68.437437, 17.426283
        String zone = GeometryTransformer.findUtmCrs(17.426283);
        assertThat(zone).isEqualTo("EPSG:32633");
    }

    @Test
    public void findUtmZone35() {
        // Mehamn: 71.035717, 27.848786
        String zone = GeometryTransformer.findUtmCrs(27.848786);
        assertThat(zone).isEqualTo("EPSG:32635");
    }
}