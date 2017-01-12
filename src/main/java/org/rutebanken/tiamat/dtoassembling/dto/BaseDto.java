package org.rutebanken.tiamat.dtoassembling.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@GraphQLType
public class BaseDto {

    @GraphQLField
    public String name;
    @GraphQLField
    public String shortName;
    @GraphQLField
    public String description;
    @GraphQLField
    public String id;
    @GraphQLField
    public SimplePointDto centroid;
    @GraphQLField
    public boolean allAreasWheelchairAccessible;

}
