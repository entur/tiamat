package org.rutebanken.tiamat.dtoassembling.dto;

import org.junit.Assert;
import org.junit.Test;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.time.ExportTimeZone;

import java.time.Instant;

public class IdMappingDtoCsvMapperTest {

    private IdMappingDtoCsvMapper mapper = new IdMappingDtoCsvMapper(new ExportTimeZone());


    @Test
    public void whenNotIncludeStopTypeOrInterval_ignoreStopTypeAndInterval() {
        IdMappingDto dto = new IdMappingDto("orgId", "netexId", Instant.now(), Instant.now(), StopTypeEnumeration.AIRPORT);
        Assert.assertEquals("orgId,netexId", mapper.toCsvString(dto, false, false));
    }

    @Test
    public void whenIncludeStopTypeOrInterval_printStopTypeAndInterval() {
        IdMappingDto dto = new IdMappingDto("orgId", "netexId", Instant.EPOCH, Instant.EPOCH.plusSeconds(1), StopTypeEnumeration.AIRPORT);
        Assert.assertEquals("orgId,airport,netexId,1970-01-01T01:00:00,1970-01-01T01:00:01", mapper.toCsvString(dto, true, true));
    }

    @Test
    public void whenOptionalFieldsAreNull_printEmptyString() {
        IdMappingDto dto = new IdMappingDto("orgId", "netexId", null, null, null);
        Assert.assertEquals("orgId,,netexId,,", mapper.toCsvString(dto, true, true));
    }
}
