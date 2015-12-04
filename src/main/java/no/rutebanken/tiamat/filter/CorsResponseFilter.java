package no.rutebanken.tiamat.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

public class CorsResponseFilter implements ContainerResponseFilter {
        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

            MultivaluedMap<String, Object> headers = responseContext.getHeaders();

            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        	headers.add("Access-Control-Max-Age", "3600");
            headers.add("Access-Control-Allow-Headers", "accept, authorization, X-Requested-With, Content-Type, X-Codingpedia");
        }
}
