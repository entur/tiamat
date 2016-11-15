package org.rutebanken.tiamat.dtoassembling.dto;

import org.rutebanken.tiamat.netexmapping.NetexIdMapper;

import java.math.BigInteger;

public class IdMappingDto {
    public String idType;
    public String originalId;
    public String id;


    public IdMappingDto(String idType, String originalId, BigInteger id) {
        this.originalId = originalId;
        this.idType = idType;
        this.id = id.toString();
    }

    public String toCsvString() {
        return originalId + "," + NetexIdMapper.getNetexId(idType, id);
    }

}
