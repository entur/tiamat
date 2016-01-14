package no.rutebanken.tiamat.rest.ifopt;

import com.jayway.restassured.RestAssured;
import no.rutebanken.tiamat.TiamatIntegrationTestApplication;
import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.org.netex.netex.MultilingualString;
import uk.org.netex.netex.Quay;
import uk.org.netex.netex.StopPlace;

import java.util.ArrayList;

import static com.jayway.restassured.RestAssured.get;
import static org.hamcrest.Matchers.notNullValue;

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

    @Value("${local.server.port}")
    private int port;

    @Test
    public void testXmlExportOfStopPlace() throws Exception {
        Quay quay = new Quay();
        quay.setName(new MultilingualString("q", "en", ""));

        quayRepository.save(quay);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setQuays(new ArrayList<>());
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        get("/jersey/stop_place/xml/" + stopPlace.getId())
                .then()
                .log().body()
                .statusCode(200)
                .body(notNullValue());

    }

}