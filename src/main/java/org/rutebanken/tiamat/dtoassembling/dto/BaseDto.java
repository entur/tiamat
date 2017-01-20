package org.rutebanken.tiamat.dtoassembling.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseDto {

    public String name;
    public String shortName;
    public String description;
    public String id;
    public SimplePointDto centroid;
    public boolean allAreasWheelchairAccessible;

}
