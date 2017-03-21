package org.rutebanken.tiamat.rest.dto;

import org.junit.Before;
import org.junit.Test;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;


public class DtoStopPlaceResourceTest {

    private StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);
    private DtoStopPlaceResource dtoStopPlaceResource;

    @Before
    public void setUp() {
        dtoStopPlaceResource = new DtoStopPlaceResource();
        dtoStopPlaceResource.stopPlaceRepository = stopPlaceRepository;
    }

    @Test
    public void keyValueStopPlaceMappingWithWithSize() throws IOException {
        int keyValueMappingCount = 3;
        int size = 1;

        when(stopPlaceRepository.findKeyValueMappingsForStop(anyInt(), anyInt()))
                .thenReturn(Arrays.asList(new IdMappingDto("original id", BigInteger.ONE.toString(), "1")))
                .thenReturn(Arrays.asList(new IdMappingDto("original id", BigInteger.TEN.toString(), "1")))
                .thenReturn(Arrays.asList(new IdMappingDto("original id", BigInteger.ZERO.toString(), "1")))
                .thenReturn(new ArrayList<>());

        Response response = dtoStopPlaceResource.getIdMapping(size);
        StreamingOutput output = (StreamingOutput) response.getEntity();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        output.write(baos);
        // plus one for the last empty call.
        verify(stopPlaceRepository, times((keyValueMappingCount/size)+1)).findKeyValueMappingsForStop(anyInt(), anyInt());
    }

}