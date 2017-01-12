package org.rutebanken.tiamat.rest.dto;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.execution.ExecutorServiceExecutionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@Component
@Path("/graphql")
public class GraphQLResource {

	@Autowired
	private StopPlaceRegisterGraphQLSchema stopPlaceRegisterGraphQLSchema;

	public GraphQLResource() {
	}

	@PostConstruct
	public void init() {
		graphQL = new GraphQL(stopPlaceRegisterGraphQLSchema.stopPlaceRegisterSchema,
				new ExecutorServiceExecutionStrategy(Executors
						.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("GraphQLExecutor--%d").build())));

	}

	private GraphQL graphQL;


	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
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
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGraphQL(String query) {

		return getGraphQLResponse(query, new HashMap<>());

	}

	public Response getGraphQLResponse(String query, Map<String, Object> variables) {
		ExecutionResult executionResult = graphQL.execute(query, null, null, variables);
		Response.ResponseBuilder res = Response.status(Response.Status.OK);
		HashMap<String, Object> content = new HashMap<>();
		if (!executionResult.getErrors().isEmpty()) {
			List<GraphQLError> errors = executionResult.getErrors();

			Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
			for (GraphQLError error : errors) {
				switch(error.getErrorType()) {
					case InvalidSyntax:
						status = Response.Status.BAD_REQUEST;
						break;
					case ValidationError:
						status = Response.Status.BAD_REQUEST;
						break;
					case DataFetchingException:
						status = Response.Status.INTERNAL_SERVER_ERROR;
						break;
				}
			}

			res = Response.status(status);

			content.put("errors", errors);
		}
		if (executionResult.getData() != null) {
			content.put("data", executionResult.getData());
		}
		return res.entity(content).build();
	}

}
