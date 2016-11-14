package org.rutebanken.tiamat.dtoassembling.dto;

import org.rutebanken.tiamat.model.Value;
import org.rutebanken.tiamat.netexmapping.NetexIdMapper;

import java.util.Set;
import java.util.stream.Collectors;

public class IdMappingDto {
    public String id;
    public String idType;
    public Set<String> mappingValue;


    public IdMappingDto(String idType, long id, Value value) {
        this.id = NetexIdMapper.getNetexId(idType, ""+id);
        this.idType = idType;
        mappingValue = value.getItems();
    }

    public String toCsvString() {
        return id + "," + mappingValue.stream().map(String::toString).collect(Collectors.joining(",")) + "\n";
    }

}
