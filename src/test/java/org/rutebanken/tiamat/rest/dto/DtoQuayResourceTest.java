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


public class DtoQuayResourceTest {

    private StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);
    private DtoQuayResource dtoQuayResource;

    @Before
    public void setUp() {
        dtoQuayResource = new DtoQuayResource();
        dtoQuayResource.stopPlaceRepository = stopPlaceRepository;
    }

    @Test
    public void keyValueQuayMappingWithWithSize() throws IOException {
        int keyValueMappingCount = 3;
        int size = 1;

        when(stopPlaceRepository.findKeyValueMappingsForQuay(anyInt(), anyInt()))
                .thenReturn(Arrays.asList(new IdMappingDto("idtype", "original id", BigInteger.ONE)))
                .thenReturn(Arrays.asList(new IdMappingDto("idtype", "original id", BigInteger.TEN)))
                .thenReturn(Arrays.asList(new IdMappingDto("idtype", "original id", BigInteger.ZERO)))
                .thenReturn(new ArrayList<>());

        Response response = dtoQuayResource.getIdMapping(size);
        StreamingOutput output = (StreamingOutput) response.getEntity();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        output.write(baos);
        // plus one for the last empty call.
        verify(stopPlaceRepository, times((keyValueMappingCount/size)+1)).findKeyValueMappingsForQuay(anyInt(), anyInt());
    }

}