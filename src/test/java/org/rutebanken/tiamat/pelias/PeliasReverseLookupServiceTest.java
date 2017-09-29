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

package org.rutebanken.tiamat.pelias;

import org.rutebanken.tiamat.pelias.model.ReverseLookupResult;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Test only intended for manual run with Pelias.
 */
@Ignore
public class PeliasReverseLookupServiceTest {

    @Test
    public void testReverseLookup() throws Exception {

        PeliasReverseLookupClient peliasReverseLookupClient = new PeliasReverseLookupClient("http://localhost:3001/v1/reverse");

        ReverseLookupResult reverseLookupResult = peliasReverseLookupClient.reverseLookup("60.440860", "5.300058", 1);

        assertThat(reverseLookupResult.getFeatures()).isNotEmpty();

        String region = reverseLookupResult.getFeatures().get(0).getProperties().getRegion();

        String locality = reverseLookupResult.getFeatures().get(0).getProperties().getLocality();

        System.out.println("Got result with region: "+region + " and locality: "+locality);
    }
}