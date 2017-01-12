package org.rutebanken.tiamat.dtoassembling.dto;


import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;

@GraphQLType
public class QuayDto extends BaseDto {

    @GraphQLField
    public String quayType;
    @GraphQLField
    public Integer compassBearing;
}
