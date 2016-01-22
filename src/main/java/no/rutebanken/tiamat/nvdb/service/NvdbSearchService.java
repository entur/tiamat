package no.rutebanken.tiamat.nvdb.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Envelope;
import no.rutebanken.tiamat.nvdb.model.VegObjekt;
import no.rutebanken.tiamat.nvdb.model.search.*;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.StringJoiner;

import static java.util.Arrays.asList;

@Service
public class NvdbSearchService {

    private static final int EGENSKAP_HOLDEPLASS_NAVN = 3957;
    private static final Logger logger = LoggerFactory.getLogger(NvdbSearchService.class);

    private JsonFactory factory = new JsonFactory();
    private ObjectMapper mapper = new ObjectMapper(factory);

    public VegObjekt search(String name, Envelope envelope) throws JsonProcessingException, UnsupportedEncodingException {

        Filter filter = new Filter(EGENSKAP_HOLDEPLASS_NAVN, "=", asList(name));

        ObjektType objectType = new ObjektType(487, 1, asList(filter));

        String bbox = new StringJoiner(",")
                .add(Double.toString(envelope.getMinX()))
                .add(Double.toString(envelope.getMinY()))
                .add(Double.toString(envelope.getMaxX()))
                .add(Double.toString(envelope.getMaxY()))
                .toString();


        SokeLokasjon sokeLokasjon = new SokeLokasjon(bbox, "WGS84");
        Kriterie search = new Kriterie(sokeLokasjon, asList(objectType));

        String json = mapper.writeValueAsString(search);

        logger.trace("Generated json {}", json);
        String urlEncodedJson = URLEncoder.encode(json, "UTF-8");

        logger.trace("Url encoded json {}", urlEncodedJson);

        try {
            InputStream inputStream = Request.Get("https://www.vegvesen.no/nvdb/api/sok?kriterie="+urlEncodedJson)
                    .connectTimeout(20000)
                    .socketTimeout(20000)
                    .execute().returnContent().asStream();

            SearchResult result = mapper.readValue(inputStream, SearchResult.class);
            logger.debug("Got {} objects", result.getResultater().size());

            VegObjekt roadObject = result.getResultater().stream()
                    .map(Resultater::getVegObjekter)
                    .flatMap(Collection::stream)
                    .findFirst().orElse(null);

            logger.trace("got this 'vegobjekt' back: {}", mapper.writeValueAsString(roadObject));

            return roadObject;


        } catch (IOException e) {
            logger.warn("Could not fetch data from nvdb: {}", e.getMessage(), e);
            return null;
        }

    }

}
