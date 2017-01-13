package org.rutebanken.tiamat.dtoassembling.dto;


import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLType;

@GraphQLType
public class SimplePointDto {


    @GraphQLField
    @GraphQLName("location")
    public LocationDto location;
}
