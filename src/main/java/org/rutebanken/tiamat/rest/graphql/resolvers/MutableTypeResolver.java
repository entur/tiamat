package org.rutebanken.tiamat.rest.graphql.resolvers;

import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;
import org.springframework.stereotype.Component;

import java.util.function.Function;

public class MutableTypeResolver implements TypeResolver {

    private Function<Object, GraphQLObjectType> resolveFunction;

    public void setResolveFunction(Function<Object, GraphQLObjectType> resolveFunction) {
        this.resolveFunction = resolveFunction;
    }

    @Override
    public GraphQLObjectType getType(TypeResolutionEnvironment typeResolutionEnvironment) {
        return resolveFunction.apply(typeResolutionEnvironment.getObject());
    }
}
