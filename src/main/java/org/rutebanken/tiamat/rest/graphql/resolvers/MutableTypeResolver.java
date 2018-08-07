/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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
