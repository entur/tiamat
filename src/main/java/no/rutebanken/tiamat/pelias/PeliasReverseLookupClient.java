package no.rutebanken.tiamat.pelias;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.rutebanken.tiamat.pelias.model.ReverseLookupResult;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class PeliasReverseLookupClient {

    private final String peliasReverseLookupEndpoint;

    private static final Logger logger = LoggerFactory.getLogger(PeliasReverseLookupClient.class);

    private static final ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());

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
                .append("&size=").append(size);

        logger.info("Request to Pelias on {}", url.toString());

        InputStream inputStream = Request.Get(url.toString())
                .connectTimeout(3000)
                .socketTimeout(3000)
                .execute().returnContent().asStream();


        ReverseLookupResult result = objectMapper.readValue(inputStream, ReverseLookupResult.class);
        logger.info("Got {} features", result.getFeatures().size());
        return result;
    }


}
