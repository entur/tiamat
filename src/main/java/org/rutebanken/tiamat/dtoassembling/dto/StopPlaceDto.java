package org.rutebanken.tiamat.dtoassembling.dto;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.annotations.GraphQLType;

import java.util.List;

@GraphQLType
@GraphQLName("stopPlace")
public class StopPlaceDto extends BaseDto {

    @GraphQLField
    public String stopPlaceType;
    @GraphQLField
    public List<QuayDto> quays;
    @GraphQLField
    public String municipality;
    @GraphQLField
    public String county;
}
