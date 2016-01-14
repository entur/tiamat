package no.rutebanken.tiamat.rest.ifopt;

import no.rutebanken.tiamat.TiamatIntegrationTestApplication;
import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.org.netex.netex.MultilingualString;
import uk.org.netex.netex.Quay;
import uk.org.netex.netex.StopPlace;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TiamatIntegrationTestApplication.class)
@WebIntegrationTest
@ActiveProfiles("geodb")
@Ignore
public class StopPlaceResourceTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private QuayRepository quayRepository;



    @Test
    public void testXmlExportOfStopPlace() throws Exception {
        Quay quay = new Quay();
        quay.setName(new MultilingualString("q", "en", ""));

        quayRepository.save(quay);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setQuays(new ArrayList<>());
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        assertThat(stopPlaceRepository.findOne(stopPlace.getId())).isNotNull();
        String url = "http://localhost:1888/jersey/stop_place/xml/"+stopPlace.getId();

        RestResponse response =  Request.Get(url)
                .connectTimeout(10000)
                .socketTimeout(10000)
                .execute()
                .handleResponse(httpResponse -> {

                    RestResponse restResponse = new RestResponse();

                    restResponse.statusLine = httpResponse.getStatusLine();

                    System.out.println(httpResponse.getStatusLine());
                    if(httpResponse.getEntity() != null) {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(httpResponse.getEntity().getContent(),"UTF-8"));

                        restResponse.responseBody = reader.lines().collect(Collectors.joining());
                    }

                    return restResponse;
                });

        System.out.println(response.responseBody);
        assertThat(response).isNotNull();

        assertThat(response.statusLine).isNotNull();
        assertThat(response.statusLine.getStatusCode()).isEqualTo(200);

        System.out.println(response);

    }

    private class RestResponse {
        public String responseBody;
        public StatusLine statusLine;
    }
}