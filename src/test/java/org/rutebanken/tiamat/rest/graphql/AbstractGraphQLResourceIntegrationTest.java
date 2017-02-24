package org.rutebanken.tiamat.rest.graphql;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Before;
import org.rutebanken.tiamat.CommonSpringBootTest;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static com.jayway.restassured.RestAssured.given;


public abstract class AbstractGraphQLResourceIntegrationTest extends CommonSpringBootTest {

    protected static final String BASE_URI_GRAPHQL = "/jersey/graphql/";

    @Autowired
    protected StopPlaceRepository stopPlaceRepository;

    @Autowired
    protected TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    protected PathLinkRepository pathLinkRepository;

    @Autowired
    protected GeometryFactory geometryFactory;

    @Value("${local.server.port}")
    protected int port;

    @Before
    public void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Before
    public void clearRepositories() {
        pathLinkRepository.deleteAll();
        stopPlaceRepository.deleteAll();
        topographicPlaceRepository.deleteAll();
    }

    protected ValidatableResponse executeGraphQL(String graphQlJsonQuery) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(graphQlJsonQuery)
           .when()
                .post(BASE_URI_GRAPHQL)
                .then()
                .log().body()
                .statusCode(200)
                .assertThat();
    }

}
