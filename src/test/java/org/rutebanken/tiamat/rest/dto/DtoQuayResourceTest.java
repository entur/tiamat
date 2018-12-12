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

package org.rutebanken.tiamat.rest.dto;

import org.junit.Before;
import org.junit.Test;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDtoCsvMapper;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.time.ExportTimeZone;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;


public class DtoQuayResourceTest {

    private QuayRepository quayRepository = mock(QuayRepository.class);
    private DtoQuayResource dtoQuayResource;
    private Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    @Before
    public void setUp() {
        dtoQuayResource = new DtoQuayResource(quayRepository, mock(DtoMappingSemaphore.class), new IdMappingDtoCsvMapper(new ExportTimeZone()));
    }

    @Test
    public void keyValueQuayMappingWithSize() throws IOException, InterruptedException {
        int keyValueMappingCount = 3;
        int size = 1;

        when(quayRepository.findKeyValueMappingsForQuay(any(Instant.class), any(Instant.class), anyInt(), anyInt()))
                .thenReturn(Arrays.asList(new IdMappingDto("original id", BigInteger.ONE.toString(), now, now, StopTypeEnumeration.FERRY_STOP)))
                .thenReturn(Arrays.asList(new IdMappingDto("original id", BigInteger.TEN.toString(), now, now, StopTypeEnumeration.TRAM_STATION)))
                .thenReturn(Arrays.asList(new IdMappingDto("original id", BigInteger.ZERO.toString(), now, now, StopTypeEnumeration.BUS_STATION)))
                .thenReturn(new ArrayList<>());

        Response response = dtoQuayResource.getIdMapping(size, false, false);
        StreamingOutput output = (StreamingOutput) response.getEntity();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        output.write(baos);
        // plus one for the last empty call.
        verify(quayRepository, times((keyValueMappingCount / size) + 1)).findKeyValueMappingsForQuay(any(Instant.class), any(Instant.class), anyInt(), anyInt());
    }

}