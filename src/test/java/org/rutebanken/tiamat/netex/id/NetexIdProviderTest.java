package org.rutebanken.tiamat.netex.id;

import org.junit.Test;
import org.rutebanken.tiamat.model.TariffZone;
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

        NetexIdProvider netexIdProvider = new NetexIdProvider(gaplessIdGeneratorService, new ValidPrefixList(validPrefixesPerType));

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

        NetexIdProvider netexIdProvider = new NetexIdProvider(gaplessIdGeneratorService, new ValidPrefixList(validPrefixesPerType));

        netexIdProvider.claimId(topographicPlace);
        verify(gaplessIdGeneratorService, times(0)).getNextIdForEntity(TopographicPlace.class.getSimpleName(),1L);
    }
}