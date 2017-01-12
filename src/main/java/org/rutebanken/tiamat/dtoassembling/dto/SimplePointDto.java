package org.rutebanken.tiamat.dtoassembling.dto;


import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;

@GraphQLType
public class SimplePointDto {


    @GraphQLField
    public LocationDto location;
}
