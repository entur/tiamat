package org.rutebanken.tiamat.dtoassembling.dto;

import org.rutebanken.tiamat.model.StopTypeEnumeration;

import java.time.Instant;

public class IdMappingIntervalDto {
    public String originalId;
    public String netexId;
    public Instant validFrom;
    public Instant validTo;
    public StopTypeEnumeration stopType;

    private static final String SEPARATOR = ",";

    public IdMappingIntervalDto(String originalId, String netexId, Instant validFrom, Instant validTo, StopTypeEnumeration stopType) {
        this.originalId = originalId;
        this.netexId = netexId;
        this.stopType = stopType;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }
}
