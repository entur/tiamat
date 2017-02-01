package org.rutebanken.tiamat.pelias;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rutebanken.tiamat.pelias.model.ReverseLookupResult;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PeliasReverseLookupClient {

    private final String peliasReverseLookupEndpoint;

    private static final Logger logger = LoggerFactory.getLogger(PeliasReverseLookupClient.class);

    private static final ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());

    /*private static final HttpClient client = HttpClientBuilder
            .create()
            .setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
                @Override
                public long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext) {
                    return 0;
                }
            })
            .disableAuthCaching().build();
*/

    @Autowired
    public PeliasReverseLookupClient(@Value("${peliasReverseLookupEndpoint:http://localhost:3001/v1/reverse}") String peliasReverseLookupEndpoint) {
        this.peliasReverseLookupEndpoint = peliasReverseLookupEndpoint;
    }

    public ReverseLookupResult reverseLookup(String latitude, String longitude, int size) throws IOException {

        StringBuilder url = new StringBuilder();
        url.append(peliasReverseLookupEndpoint)
                .append('?')
                .append("point.lat=").append(latitude)
                .append("&point.lon=").append(longitude)
                .append("&size=").append(size)
                .append("&layers=kartverket-address");

        logger.info("Request to Pelias on {}", url.toString());

        HttpResponse response = Request.Get(url.toString())
                .connectTimeout(50000)
                .socketTimeout(50000)
                .execute().returnResponse();

        HttpClientUtils.closeQuietly(response);

        ReverseLookupResult result = objectMapper.readValue(response.getEntity().getContent(), ReverseLookupResult.class);
        logger.info("Got {} features", result.getFeatures().size());
        return result;
    }


}
