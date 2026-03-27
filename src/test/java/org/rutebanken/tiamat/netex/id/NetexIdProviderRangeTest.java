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
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link NetexIdProvider} with configured ID ranges.
 * Existing tests in {@link NetexIdProviderTest} cover the default (no range) behaviour.
 */
public class NetexIdProviderRangeTest {

    private static NetexIdRangeConfiguration createRangeConfig() {
        NetexIdRangeConfiguration config = new NetexIdRangeConfiguration();
        Map<String, NetexIdRangeConfiguration.IdRange> ranges = new HashMap<>();
        ranges.put("StopPlace", new NetexIdRangeConfiguration.IdRange(100, 200));
        ranges.put("Quay", new NetexIdRangeConfiguration.IdRange(500, 600));
        config.setRange(ranges);
        return config;
    }

    private static ValidPrefixList createValidPrefixList() {
        Map<String, List<String>> validPrefixesPerType = new HashMap<>();
        validPrefixesPerType.put("StopPlace", List.of("NSR"));
        return new ValidPrefixList("NSR", validPrefixesPerType);
    }

    @Test
    public void claimStopPlaceIdWithinRangeSucceeds() {
        GaplessIdGeneratorService gaplessIdGeneratorServiceMock = mock(GaplessIdGeneratorService.class);
        NetexIdRangeConfiguration rangeConfig = createRangeConfig();
        ValidPrefixList validPrefixList = createValidPrefixList();
        NetexIdHelper netexIdHelper = new NetexIdHelper(validPrefixList);
        NetexIdProvider provider = new NetexIdProvider(gaplessIdGeneratorServiceMock, validPrefixList, netexIdHelper, rangeConfig);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:150");

        provider.claimId(stopPlace);
        verify(gaplessIdGeneratorServiceMock, times(1)).getNextIdForEntity("StopPlace", 150L);
    }

    @Test
    public void claimStopPlaceIdOutsideRangeThrowsException() {
        GaplessIdGeneratorService gaplessIdGeneratorServiceMock = mock(GaplessIdGeneratorService.class);
        NetexIdRangeConfiguration rangeConfig = createRangeConfig();
        ValidPrefixList validPrefixList = createValidPrefixList();
        NetexIdHelper netexIdHelper = new NetexIdHelper(validPrefixList);
        NetexIdProvider provider = new NetexIdProvider(gaplessIdGeneratorServiceMock, validPrefixList, netexIdHelper, rangeConfig);

        StopPlace stopPlace = new StopPlace();
        // ID 1 is below StopPlace range [100, 200]
        stopPlace.setNetexId("NSR:StopPlace:1");

        assertThatThrownBy(() -> provider.claimId(stopPlace))
                .isInstanceOf(IdGeneratorException.class)
                .hasMessageContaining("outside the configured range");
        verify(gaplessIdGeneratorServiceMock, times(0)).getNextIdForEntity("StopPlace", 1L);
    }

    @Test
    public void claimIdForEntityWithoutRangeSucceeds() {
        GaplessIdGeneratorService gaplessIdGeneratorServiceMock = mock(GaplessIdGeneratorService.class);
        NetexIdRangeConfiguration rangeConfig = createRangeConfig();
        ValidPrefixList validPrefixList = createValidPrefixList();
        NetexIdHelper netexIdHelper = new NetexIdHelper(validPrefixList);
        NetexIdProvider provider = new NetexIdProvider(gaplessIdGeneratorServiceMock, validPrefixList, netexIdHelper, rangeConfig);

        TopographicPlace tp = new TopographicPlace();
        tp.setNetexId("NSR:TopographicPlace:1");

        provider.claimId(tp);
        verify(gaplessIdGeneratorServiceMock, times(1)).getNextIdForEntity("TopographicPlace", 1L);
    }

    @Test
    public void generateIdWithinRangeSucceeds() {
        GaplessIdGeneratorService gaplessIdGeneratorServiceMock = mock(GaplessIdGeneratorService.class);
        when(gaplessIdGeneratorServiceMock.getNextIdForEntity("StopPlace")).thenReturn(160L);

        NetexIdRangeConfiguration rangeConfig = createRangeConfig();
        ValidPrefixList validPrefixList = createValidPrefixList();
        NetexIdHelper netexIdHelper = new NetexIdHelper(validPrefixList);
        NetexIdProvider provider = new NetexIdProvider(gaplessIdGeneratorServiceMock, validPrefixList, netexIdHelper, rangeConfig);

        StopPlace stopPlace = new StopPlace();
        String generatedId = provider.getGeneratedId(stopPlace);

        assertThat(generatedId).isEqualTo("NSR:StopPlace:160");
    }

    @Test
    public void generateIdOutsideRangeThrowsException() {
        GaplessIdGeneratorService gaplessIdGeneratorServiceMock = mock(GaplessIdGeneratorService.class);
        // Simulate generator returning an ID outside the configured range
        when(gaplessIdGeneratorServiceMock.getNextIdForEntity("StopPlace")).thenReturn(201L);

        NetexIdRangeConfiguration rangeConfig = createRangeConfig();
        ValidPrefixList validPrefixList = createValidPrefixList();
        NetexIdHelper netexIdHelper = new NetexIdHelper(validPrefixList);
        NetexIdProvider provider = new NetexIdProvider(gaplessIdGeneratorServiceMock, validPrefixList, netexIdHelper, rangeConfig);

        StopPlace stopPlace = new StopPlace();

        assertThatThrownBy(() -> provider.getGeneratedId(stopPlace))
                .isInstanceOf(IdGeneratorException.class)
                .hasMessageContaining("outside the configured range");
    }

    @Test
    public void generateIdForEntityWithoutRangeSucceeds() {
        GaplessIdGeneratorService gaplessIdGeneratorServiceMock = mock(GaplessIdGeneratorService.class);
        when(gaplessIdGeneratorServiceMock.getNextIdForEntity("TopographicPlace")).thenReturn(1L);

        NetexIdRangeConfiguration rangeConfig = createRangeConfig();
        ValidPrefixList validPrefixList = createValidPrefixList();
        NetexIdHelper netexIdHelper = new NetexIdHelper(validPrefixList);
        NetexIdProvider provider = new NetexIdProvider(gaplessIdGeneratorServiceMock, validPrefixList, netexIdHelper, rangeConfig);

        TopographicPlace tp = new TopographicPlace();
        String generatedId = provider.getGeneratedId(tp);

        assertThat(generatedId).isEqualTo("NSR:TopographicPlace:1");
    }

    @Test
    public void claimStopPlaceIdAtMinBoundarySucceeds() {
        GaplessIdGeneratorService gaplessIdGeneratorServiceMock = mock(GaplessIdGeneratorService.class);
        NetexIdRangeConfiguration rangeConfig = createRangeConfig();
        ValidPrefixList validPrefixList = createValidPrefixList();
        NetexIdHelper netexIdHelper = new NetexIdHelper(validPrefixList);
        NetexIdProvider provider = new NetexIdProvider(gaplessIdGeneratorServiceMock, validPrefixList, netexIdHelper, rangeConfig);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:100");

        provider.claimId(stopPlace);
        verify(gaplessIdGeneratorServiceMock, times(1)).getNextIdForEntity("StopPlace", 100L);
    }

    @Test
    public void claimStopPlaceIdAtMaxBoundarySucceeds() {
        GaplessIdGeneratorService gaplessIdGeneratorServiceMock = mock(GaplessIdGeneratorService.class);
        NetexIdRangeConfiguration rangeConfig = createRangeConfig();
        ValidPrefixList validPrefixList = createValidPrefixList();
        NetexIdHelper netexIdHelper = new NetexIdHelper(validPrefixList);
        NetexIdProvider provider = new NetexIdProvider(gaplessIdGeneratorServiceMock, validPrefixList, netexIdHelper, rangeConfig);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:200");

        provider.claimId(stopPlace);
        verify(gaplessIdGeneratorServiceMock, times(1)).getNextIdForEntity("StopPlace", 200L);
    }

    @Test
    public void claimStopPlaceIdOneBelowMinIsRejected() {
        GaplessIdGeneratorService gaplessIdGeneratorServiceMock = mock(GaplessIdGeneratorService.class);
        NetexIdRangeConfiguration rangeConfig = createRangeConfig();
        ValidPrefixList validPrefixList = createValidPrefixList();
        NetexIdHelper netexIdHelper = new NetexIdHelper(validPrefixList);
        NetexIdProvider provider = new NetexIdProvider(gaplessIdGeneratorServiceMock, validPrefixList, netexIdHelper, rangeConfig);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:99");

        assertThatThrownBy(() -> provider.claimId(stopPlace))
                .isInstanceOf(IdGeneratorException.class)
                .hasMessageContaining("outside the configured range");
        verify(gaplessIdGeneratorServiceMock, times(0)).getNextIdForEntity("StopPlace", 99L);
    }

    @Test
    public void claimStopPlaceIdOneAboveMaxIsRejected() {
        GaplessIdGeneratorService gaplessIdGeneratorServiceMock = mock(GaplessIdGeneratorService.class);
        NetexIdRangeConfiguration rangeConfig = createRangeConfig();
        ValidPrefixList validPrefixList = createValidPrefixList();
        NetexIdHelper netexIdHelper = new NetexIdHelper(validPrefixList);
        NetexIdProvider provider = new NetexIdProvider(gaplessIdGeneratorServiceMock, validPrefixList, netexIdHelper, rangeConfig);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:201");

        assertThatThrownBy(() -> provider.claimId(stopPlace))
                .isInstanceOf(IdGeneratorException.class)
                .hasMessageContaining("outside the configured range");
        verify(gaplessIdGeneratorServiceMock, times(0)).getNextIdForEntity("StopPlace", 201L);
    }
}

