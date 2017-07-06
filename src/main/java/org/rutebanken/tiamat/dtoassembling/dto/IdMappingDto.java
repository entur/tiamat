package org.rutebanken.tiamat.dtoassembling.dto;

import org.rutebanken.tiamat.model.StopTypeEnumeration;

public class IdMappingDto {
    public String originalId;
    public String netexId;
    public StopTypeEnumeration stopType;

    private static final String SEPARATOR = ",";

    public IdMappingDto(String originalId, String netexId) {
        this(null, null, null);
    }

    public IdMappingDto(String originalId, String netexId, StopTypeEnumeration stopType) {
        this.originalId = originalId;
        this.netexId = netexId;
        this.stopType = stopType;
    }

    public String toCsvString(boolean includeStopType) {
        if (includeStopType) {
            return originalId + SEPARATOR + (stopType == null ? "" : stopType.value()) + SEPARATOR + netexId;
        }
        return originalId + SEPARATOR + netexId;
    }

}
