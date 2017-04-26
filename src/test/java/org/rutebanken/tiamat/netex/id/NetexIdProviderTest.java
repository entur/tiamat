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
        topographicPlace.setNetexId("KVE:"+TopographicPlace.class.getSimpleName()+":1");

        GaplessIdGeneratorService gaplessIdGeneratorService = mock(GaplessIdGeneratorService.class);

        Map<String, List<String>> validPrefixesPerType = new HashMap<>();

        validPrefixesPerType.put("TopographicPlace", Arrays.asList("KVE"));

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

    /**
     * Claim ID with any prefix
     */
    @Test
    public void claimAnyIdForType() {

        TariffZone tariffZone = new TariffZone();
        tariffZone.setNetexId("XXX:"+TopographicPlace.class.getSimpleName()+":1");

        GaplessIdGeneratorService gaplessIdGeneratorService = mock(GaplessIdGeneratorService.class);

        Map<String, List<String>> validPrefixesPerType = new HashMap<>();

        validPrefixesPerType.put(TariffZone.class.getSimpleName(), Arrays.asList("*"));

        NetexIdProvider netexIdProvider = new NetexIdProvider(gaplessIdGeneratorService, new ValidPrefixList(validPrefixesPerType));

        netexIdProvider.claimId(tariffZone);
        verify(gaplessIdGeneratorService, times(1)).getNextIdForEntity(TariffZone.class.getSimpleName(),1L);
    }
}