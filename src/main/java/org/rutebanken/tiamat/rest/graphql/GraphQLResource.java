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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.Sets;
import graphql.ErrorClassification;
import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.GraphQLException;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.AbortExecutionException;
import graphql.execution.DataFetcherResult;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.rutebanken.helper.organisation.NotAuthenticatedException;
import org.rutebanken.tiamat.exception.HSLErrorCodeEnumeration;
import org.rutebanken.tiamat.rest.exception.ErrorResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static graphql.ErrorType.DataFetchingException;
import static graphql.ErrorType.InvalidSyntax;
import static graphql.ErrorType.ValidationError;
import static java.util.stream.Collectors.toList;

@Component
@Tag(name = "GraphQL Resource", description = "GraphQL Resource")
@Path("graphql")
public class GraphQLResource {

    private static final Logger logger = LoggerFactory.getLogger(GraphQLResource.class);

    /**
     * Exception classes that should cause data fetching exceptions to be rethrown and mapped to corresponding HTTP status code outside transaction.
     */
    private static final Set<Class<? extends RuntimeException>> RETHROW_EXCEPTION_TYPES
            = Sets.newHashSet(NotAuthenticatedException.class, NotAuthorizedException.class, AccessDeniedException.class, DataIntegrityViolationException.class);

    @Value("${graphql.query.max.depth:100}")
    private int MAX_DEPTH = 100;

    @Autowired
    private StopPlaceRegisterGraphQLSchema stopPlaceRegisterGraphQLSchema;


    @Autowired
    private RequestLoggingInstrumentation requestLoggingInstrumentation;


    private final TransactionTemplate transactionTemplate;


    public GraphQLResource(PlatformTransactionManager transactionManager) {
        org.springframework.util.Assert.notNull(transactionManager, "The 'transactionManager' argument must not be null.");
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @PostConstruct
    public void init() {
        List<Instrumentation> chainedList = new ArrayList<>();
        chainedList.add(new MaxQueryDepthInstrumentation(MAX_DEPTH));
        chainedList.add(requestLoggingInstrumentation);

        final ChainedInstrumentation chainedInstrumentation = new ChainedInstrumentation(chainedList);


        logger.info(String.format("max query depth is: %d", MAX_DEPTH));
        graphQL = GraphQL.newGraphQL(stopPlaceRegisterGraphQLSchema.stopPlaceRegisterSchema)
                .instrumentation(chainedInstrumentation)
                .build();

    }

    private GraphQL graphQL;


    @POST
    @SuppressWarnings("unchecked")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGraphQL(HashMap<String, Object> query) {
        Map<String, Object> variables;
        if (query.get("variables") instanceof Map) {
            variables = (Map) query.get("variables");

        } else if (query.get("variables") instanceof String s && !s.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            // convert JSON string to Map
            try {
                variables = mapper.readValue(s, TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, Object.class));
            } catch (IOException e) {
                HashMap<String, Object> content = new HashMap<>();
                content.put("errors", new ErrorResponseEntity(e.getMessage()));
                return Response.status(Response.Status.BAD_REQUEST).entity(content).build();
            }

        } else {
            variables = new HashMap<>();
        }
        return getGraphQLResponseInTransaction((String) query.get("query"), variables);
    }

    @POST
    @Consumes("application/graphql")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGraphQL(String query) {
        return getGraphQLResponseInTransaction(query, new HashMap<>());
    }

    /**
     * Use programmatic transaction because graphql catches RuntimeExceptions.
     * With multiple transaction interceptors (Transactional annotation), this causes the rolled back transaction (in case of errors) to be commed by the outer transaction interceptor.
     * NRP-1992
     */
    private Response getGraphQLResponseInTransaction(String query, Map<String, Object> variables) {
        try {
            return (Response) transactionTemplate.execute((transactionStatus) -> getGraphQLResponse(query, variables, transactionStatus));
        } catch (JpaSystemException e) {
            var rootCause = e.getRootCause();
            for (HSLErrorCodeEnumeration hslError : HSLErrorCodeEnumeration.values()) {
                if (e.getMessage().contains("ERROR: " + hslError.name())) {
                    return customHSLErrorResponse(hslError, e);
                }
                if (rootCause != null && rootCause.getMessage().contains("ERROR: " + hslError.name())) {
                    return customHSLErrorResponse(hslError, rootCause);
                }
            }
            throw e;
        }
    }

    private Response customHSLErrorResponse(HSLErrorCodeEnumeration errorCodeEnumeration, Throwable throwable) {
        var result = new DataFetcherResult(null,
                List.of(GraphQLError.newError()
                        .errorType(DataFetchingException)
                                .message(cleanSQLErrorMessage(errorCodeEnumeration, throwable.getMessage()))
                        .extensions(Map.of("errorCode", errorCodeEnumeration.toString()))
                        .build()
        ));
        return Response.status(Response.Status.OK).entity(
                result
        ).build();
    }

    private String cleanSQLErrorMessage(HSLErrorCodeEnumeration errorCodeEnumeration, String message) {
        return message.substring(message.indexOf(errorCodeEnumeration.name()), message.indexOf("\n"));
    }

    private Response getGraphQLResponse(String query, Map<String, Object> variables, TransactionStatus transactionStatus) {
        Response.ResponseBuilder res = Response.status(Response.Status.OK);
        HashMap<String, Object> content = new HashMap<>();
        try {
            final ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                    .query(query)
                    .root(null)
                    .variables(variables)
                    .build();
            ExecutionResult executionResult = graphQL.execute(executionInput);

            if (!executionResult.getErrors().isEmpty()) {
                List<GraphQLError> errors = executionResult.getErrors();

                Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
                for (GraphQLError error : errors) {
                    final ErrorClassification errorClassification = error.getErrorType();
                    if (InvalidSyntax.equals(errorClassification) || ValidationError.equals(errorClassification)) {
                        status = Response.Status.BAD_REQUEST;
                    } else if (DataFetchingException.equals(errorClassification)) {
                        ExceptionWhileDataFetching exceptionWhileDataFetching = ((ExceptionWhileDataFetching) error);
                        if (exceptionWhileDataFetching.getException() != null) {
                            status = getStatusCodeFromThrowable(exceptionWhileDataFetching.getException());
                            continue;
                        }
                        status = Response.Status.OK;
                    }
                }

                res = Response.status(status);

                if (errors.stream().anyMatch(error -> error.getErrorType().equals(DataFetchingException))) {
                    logger.warn("Detected DataFetchingException from errors: {} Setting transaction to rollback only", errors);
                    transactionStatus.setRollbackOnly();
                }

                content.put("errors", errors);
            }
            if (executionResult.getData() != null) {
                content.put("data", executionResult.getData());
            }


        } catch (GraphQLException e) {
            logger.warn("Catched graphqlException. Setting rollback only", e);
            res = Response.status(Response.Status.INTERNAL_SERVER_ERROR);

            content.put("errors", Arrays.asList(e));
            transactionStatus.setRollbackOnly();
        }
        removeErrorStacktraces(content);
        return res.entity(content).build();
    }


    private void removeErrorStacktraces(Map<String, Object> content) {
        if (content.containsKey("errors")) {
            @SuppressWarnings("unchecked")
            List<GraphQLError> errors = (List<GraphQLError>) content.get("errors");

            try {
                content.put("errors", errors.stream().map(graphQLError -> {
                    if (graphQLError instanceof ExceptionWhileDataFetching) {
                        return new TiamatExceptionWhileDataFetching((ExceptionWhileDataFetching) graphQLError);
                    } else if( graphQLError instanceof AbortExecutionException) {
                        return new TiamatExceptionMaxDepth((AbortExecutionException) graphQLError);
                    }
                    else {
                        return graphQLError;
                    }
                }).collect(toList()));
            } catch (Exception e) {
                logger.warn("Exception caught during stacktrace removal", e);
            }
        }
    }

    private Response.Status getStatusCodeFromThrowable(Throwable e) {
        Throwable rootCause = getRootCause(e);

        if (RETHROW_EXCEPTION_TYPES.stream().anyMatch(c -> c.isAssignableFrom(rootCause.getClass()))) {
            throw (RuntimeException) rootCause;
        }

        if(IllegalArgumentException.class.isAssignableFrom(rootCause.getClass())) {
            return Response.Status.BAD_REQUEST;
        }

        return Response.Status.OK;
    }

    private Throwable getRootCause(Throwable e) {
        Throwable rootCause = e;

        if (e instanceof NestedRuntimeException nestedRuntimeException) {
            if (nestedRuntimeException.getRootCause() != null) {
                rootCause = nestedRuntimeException.getRootCause();
            }
        }
        return rootCause;
    }

}
