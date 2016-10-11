package org.rutebanken.tiamat.dtoassembling.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public class CountyOrMunicipalityDto {
    public String ref;
    public String name;
    public String type;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String county;
}
