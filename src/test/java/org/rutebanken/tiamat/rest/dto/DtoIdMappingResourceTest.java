package org.rutebanken.tiamat.rest.dto;

import org.junit.Test;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;


public class DtoIdMappingResourceTest {

    private StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);
    private DtoIdMappingResource dtoIdMappingResource = new DtoIdMappingResource(stopPlaceRepository);

    @Test
    public void keyValueMappingWithWithSize() throws IOException {
        int keyValueMappingCount = 10;
        int size = 1;
        when(stopPlaceRepository.findKeyValueMappings(anyInt(), anyInt()))
                .thenReturn(Arrays.asList(new IdMappingDto("idtype", "original id", BigInteger.ONE)));
        when(stopPlaceRepository.findKeyValueMappingCount()).thenReturn(new Integer(keyValueMappingCount));
        Response response = dtoIdMappingResource.getIdMapping(size);
        StreamingOutput output = (StreamingOutput) response.getEntity();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        output.write(baos);
        verify(stopPlaceRepository, times(keyValueMappingCount/size)).findKeyValueMappings(anyInt(), anyInt());
    }

}