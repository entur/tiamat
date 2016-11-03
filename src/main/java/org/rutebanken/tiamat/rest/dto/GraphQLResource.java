package org.rutebanken.tiamat.rest.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.ExecutorServiceExecutionStrategy;

@Component
@Path("/graphql")
public class GraphQLResource {

	@Autowired
	private StopPlaceRepository stopPlaceRepository;
	
	public GraphQLResource() {
	}
	
	@PostConstruct
	public void init() {
		graphQL = new GraphQL(new StopPlaceRegisterGraphQLSchema(stopPlaceRepository).stopPlaceRegisterSchema,
				new ExecutorServiceExecutionStrategy(Executors
						.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("GraphQLExecutor--%d").build())));
		
	}
	
	private GraphQL graphQL;

	
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getGraphQL (HashMap<String, Object> query) {
        Map<String, Object> variables;
        if (query.get("variables") instanceof Map) {
            variables = (Map) query.get("variables");
        } else {
            variables = new HashMap<>();
        }
        return getGraphQLResponse((String) query.get("query"), variables);
    }

	@POST
	@Consumes("application/graphql")
	@Produces("application/json")
	public Response getGraphQL(String query) {

		return getGraphQLResponse(query, new HashMap<>());

	}

	public Response getGraphQLResponse(String query, Map<String, Object> variables) {
		ExecutionResult executionResult = graphQL.execute(query, null, null, variables);
		Response.ResponseBuilder res = Response.status(Response.Status.OK);
		HashMap<String, Object> content = new HashMap<>();
		if (!executionResult.getErrors().isEmpty()) {
			// TODO: Put correct error code, eg. 400 for synax error
			res = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
			content.put("errors", executionResult.getErrors());
		}
		if (executionResult.getData() != null) {
			content.put("data", executionResult.getData());
		}
		return res.entity(content).build();
	}

}
