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

package org.rutebanken.tiamat.rest.graphql;

import graphql.ErrorType;
import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;

/**
 * Wrapper to suppress exceptions and stack traces.
 * These are logged anyway and can be quite large for the consumer of graphql.
 */
public class TiamatExceptionWhileDataFetching implements GraphQLError {

    private final ExceptionWhileDataFetching exceptionWhileDataFetching;

    public TiamatExceptionWhileDataFetching(ExceptionWhileDataFetching exceptionWhileDataFetching) {
        this.exceptionWhileDataFetching = exceptionWhileDataFetching;
    }

    @Override
    public String getMessage() {
        return exceptionWhileDataFetching.getMessage();
    }

    @Override
    public List<SourceLocation> getLocations() {
        return exceptionWhileDataFetching.getLocations();
    }

    @Override
    public ErrorType getErrorType() {
        return exceptionWhileDataFetching.getErrorType();
    }
}
