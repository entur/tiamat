package org.rutebanken.tiamat.rest.dto;

import org.junit.Before;
import org.junit.Test;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.repository.QuayRepository;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;


public class DtoQuayResourceTest {

    private QuayRepository quayRepository = mock(QuayRepository.class);
    private DtoQuayResource dtoQuayResource;

    @Before
    public void setUp() {
        dtoQuayResource = new DtoQuayResource(quayRepository);
    }

    @Test
    public void keyValueQuayMappingWithSize() throws IOException {
        int keyValueMappingCount = 3;
        int size = 1;

        when(quayRepository.findKeyValueMappingsForQuay(any(Instant.class), anyInt(), anyInt()))
                .thenReturn(Arrays.asList(new IdMappingDto("original id", BigInteger.ONE.toString())))
                .thenReturn(Arrays.asList(new IdMappingDto("original id", BigInteger.TEN.toString())))
                .thenReturn(Arrays.asList(new IdMappingDto("original id", BigInteger.ZERO.toString())))
                .thenReturn(new ArrayList<>());

        Response response = dtoQuayResource.getIdMapping(size, false);
        StreamingOutput output = (StreamingOutput) response.getEntity();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        output.write(baos);
        // plus one for the last empty call.
        verify(quayRepository, times((keyValueMappingCount/size)+1)).findKeyValueMappingsForQuay(any(Instant.class), anyInt(), anyInt());
    }

}