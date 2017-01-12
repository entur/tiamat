package org.rutebanken.tiamat.dtoassembling.dto;


import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;

@GraphQLType
public class LocationDto {

    @GraphQLField
    public double longitude;
    @GraphQLField
    public double latitude;

}
