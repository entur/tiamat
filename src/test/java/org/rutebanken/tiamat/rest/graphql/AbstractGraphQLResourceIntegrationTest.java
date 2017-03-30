package org.rutebanken.tiamat.rest.graphql;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Before;
import org.junit.BeforeClass;
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

    @Before
    public void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
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
