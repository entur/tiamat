package org.rutebanken.tiamat.dtoassembling.dto;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLType;

@GraphQLType
@GraphQLName("location")
public class LocationDto {

    @GraphQLField
    public double longitude;
    @GraphQLField
    public double latitude;

}
