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
import graphql.*;
import org.rutebanken.helper.organisation.NotAuthenticatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Path("/graphql")
@Transactional
public class GraphQLResource {

    /**
     * Exception classes that should cause data fetching exceptions to be rethrown and mapped to corresponding HTTP status code outside transaction.
     */
    private static final Set<Class<? extends RuntimeException>> RETHROW_EXCEPTION_TYPES
            = Sets.newHashSet(NotAuthenticatedException.class, NotAuthorizedException.class, AccessDeniedException.class, DataIntegrityViolationException.class);

    @Autowired
    private StopPlaceRegisterGraphQLSchema stopPlaceRegisterGraphQLSchema;

    public GraphQLResource() {
    }

    @PostConstruct
    public void init() {
        graphQL = new GraphQL(stopPlaceRegisterGraphQLSchema.stopPlaceRegisterSchema);
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

        } else if (query.get("variables") instanceof String && !((String) query.get("variables")).isEmpty()) {
            String s = (String) query.get("variables");

            ObjectMapper mapper = new ObjectMapper();

            // convert JSON string to Map
            try {
                variables = mapper.readValue(s, TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, Object.class));
            } catch (IOException e) {
                HashMap<String, Object> content = new HashMap<>();
                content.put("errors", e.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(content).build();
            }

        } else {
            variables = new HashMap<>();
        }
        return getGraphQLResponse((String) query.get("query"), variables);
    }

    @POST
    @Consumes("application/graphql")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGraphQL(String query) {

        return getGraphQLResponse(query, new HashMap<>());

    }

    public Response getGraphQLResponse(String query, Map<String, Object> variables) {
        Response.ResponseBuilder res = Response.status(Response.Status.OK);
        HashMap<String, Object> content = new HashMap<>();
        try {
            ExecutionResult executionResult = graphQL.execute(query, null, null, variables);

            if (!executionResult.getErrors().isEmpty()) {
                List<GraphQLError> errors = executionResult.getErrors();

                Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
                for (GraphQLError error : errors) {
                    switch (error.getErrorType()) {
                        case InvalidSyntax:
                            status = Response.Status.BAD_REQUEST;
                            break;
                        case ValidationError:
                            status = Response.Status.BAD_REQUEST;
                            break;
                        case DataFetchingException:
                            ExceptionWhileDataFetching exceptionWhileDataFetching = ((ExceptionWhileDataFetching) error);
                            if (exceptionWhileDataFetching.getException() != null) {
                                status = getStatusCodeFromThrowable(exceptionWhileDataFetching.getException());
                                break;
                            }
                            status = Response.Status.OK;
                            break;
                    }
                }

                res = Response.status(status);

                content.put("errors", errors.stream().map(error -> error.getMessage()).collect(Collectors.toList()));
            }
            if (executionResult.getData() != null) {
                content.put("data", executionResult.getData());
            }
        } catch (GraphQLException e) {
            res = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            content.put("errors", e.getMessage());
        }
        return res.entity(content).build();
    }


    private Response.Status getStatusCodeFromThrowable(Throwable e) {
        Throwable rootCause = getRootCause(e);

        if (RETHROW_EXCEPTION_TYPES.stream().anyMatch(c -> c.isAssignableFrom(rootCause.getClass()))) {
            throw (RuntimeException) rootCause;
        }


        return Response.Status.OK;
    }

    private Throwable getRootCause(Throwable e) {
        Throwable rootCause = e;

        if (e instanceof NestedRuntimeException) {
            NestedRuntimeException nestedRuntimeException = ((NestedRuntimeException) e);
            if (nestedRuntimeException.getRootCause() != null) {
                rootCause = nestedRuntimeException.getRootCause();
            }
        }
        return rootCause;
    }


}
