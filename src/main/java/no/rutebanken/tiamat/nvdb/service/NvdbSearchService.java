package no.rutebanken.tiamat.nvdb.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.rutebanken.tiamat.nvdb.model.VegObjekt;
import no.rutebanken.tiamat.nvdb.model.search.*;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;

import static java.util.Arrays.asList;

@Service
public class NvdbSearchService {

    private static final Logger logger = LoggerFactory.getLogger(NvdbSearchService.class);
    private static final int EGENSKAP_HOLDEPLASS_NAVN = 3957;

    JsonFactory factory = new JsonFactory();
    ObjectMapper mapper = new ObjectMapper(factory);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    public void search(String name) throws JsonProcessingException, UnsupportedEncodingException {

        Filter filter = new Filter(EGENSKAP_HOLDEPLASS_NAVN, "=", asList(name));

        ObjektType objectType = new ObjektType(487, 1, asList(filter));

        Kriterie search = new Kriterie(asList(objectType));

        String json = mapper.writeValueAsString(search);

        logger.trace("Generated json {}", json);
        String urlEncodedJson = URLEncoder.encode(json, "UTF-8");

        logger.trace("Url encoded json {}", urlEncodedJson);

        try {
            InputStream inputStream = Request.Get("https://www.vegvesen.no/nvdb/api/sok?kriterie="+urlEncodedJson)
                    .connectTimeout(5000)
                    .socketTimeout(5000)
                    .execute().returnContent().asStream();

            SearchResult result = mapper.readValue(inputStream, SearchResult.class);
            logger.info("Got {} objects", result.getResultater().size());

            VegObjekt roadObject = result.getResultater().stream()
                    .map(Resultater::getVegObjekter)
                    .flatMap(Collection::stream)
                    .findFirst().get();

            logger.info("got: {}", mapper.writeValueAsString(roadObject));



        } catch (IOException e) {
            logger.warn("Could not fetch data from nvdb: {}", e.getMessage(), e);
        }
    }

}
