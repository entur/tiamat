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
import org.rutebanken.tiamat.model.TopographicPlace;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class NetexIdProviderTest {




    @Test
    public void claimValidId() {

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setNetexId("NSR:"+TopographicPlace.class.getSimpleName()+":1");

        GaplessIdGeneratorService gaplessIdGeneratorService = mock(GaplessIdGeneratorService.class);

        Map<String, List<String>> validPrefixesPerType = new HashMap<>();

        validPrefixesPerType.put("TopographicPlace", Arrays.asList("NSR"));

        ValidPrefixList validPrefixList = new ValidPrefixList("NSR", validPrefixesPerType);
        NetexIdHelper netexIdHelper = new NetexIdHelper(validPrefixList);
        NetexIdProvider netexIdProvider = new NetexIdProvider(gaplessIdGeneratorService, validPrefixList, netexIdHelper);

        netexIdProvider.claimId(topographicPlace);
        verify(gaplessIdGeneratorService, times(1)).getNextIdForEntity(TopographicPlace.class.getSimpleName(),1L);
    }

    /**
     * Claim ID with invalid ID
     */
    @Test
    public void claimInvalidId() {

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setNetexId("XYZ:"+TopographicPlace.class.getSimpleName()+":1");

        GaplessIdGeneratorService gaplessIdGeneratorService = mock(GaplessIdGeneratorService.class);

        Map<String, List<String>> validPrefixesPerType = new HashMap<>();

        validPrefixesPerType.put("TopographicPlace", Arrays.asList("KVE", "VVV"));
        ValidPrefixList validPrefixList = new ValidPrefixList("NSR", validPrefixesPerType);
        NetexIdHelper netexIdHelper = new NetexIdHelper(validPrefixList);
        NetexIdProvider netexIdProvider = new NetexIdProvider(gaplessIdGeneratorService, validPrefixList, netexIdHelper);

        netexIdProvider.claimId(topographicPlace);
        verify(gaplessIdGeneratorService, times(0)).getNextIdForEntity(TopographicPlace.class.getSimpleName(),1L);
    }

    /**
     * Test claiming ID with alphanumeric ID and ENT prefix
     */
    @Test
    public void claimEnt() {

        final String prefix = "ENT";

        TopographicPlace topographicPlace = new TopographicPlace();
        topographicPlace.setNetexId(prefix + ":"+TopographicPlace.class.getSimpleName()+":SVA");

        GaplessIdGeneratorService gaplessIdGeneratorService = mock(GaplessIdGeneratorService.class);

        Map<String, List<String>> validPrefixesPerType = new HashMap<>();

        validPrefixesPerType.put("TopographicPlace", Arrays.asList(prefix));
        ValidPrefixList validPrefixList = new ValidPrefixList("NSR", validPrefixesPerType);

        NetexIdHelper netexIdHelper = new NetexIdHelper(validPrefixList);

        NetexIdProvider netexIdProvider = new NetexIdProvider(gaplessIdGeneratorService, validPrefixList, netexIdHelper);

        netexIdProvider.claimId(topographicPlace);

        verify(gaplessIdGeneratorService, times(0)).getNextIdForEntity(TopographicPlace.class.getSimpleName(),1L);
    }
}