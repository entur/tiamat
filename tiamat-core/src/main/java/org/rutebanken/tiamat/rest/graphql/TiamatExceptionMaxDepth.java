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
import graphql.GraphQLError;
import graphql.execution.AbortExecutionException;
import graphql.language.SourceLocation;

import java.util.List;

/**
 * Wrapper to suppress exceptions and stack traces.
 * These are logged anyway and can be quite large for the consumer of graphql.
 */
public class TiamatExceptionMaxDepth implements GraphQLError {

    private final AbortExecutionException abortExecutionException;

    public TiamatExceptionMaxDepth(AbortExecutionException abortExecutionException) {
        this.abortExecutionException = abortExecutionException;
    }

    @Override
    public String getMessage() {
        return abortExecutionException.getMessage();
    }

    @Override
    public List<SourceLocation> getLocations() {
        return abortExecutionException.getLocations();
    }

    @Override
    public ErrorType getErrorType() {
        return abortExecutionException.getErrorType();
    }
}
