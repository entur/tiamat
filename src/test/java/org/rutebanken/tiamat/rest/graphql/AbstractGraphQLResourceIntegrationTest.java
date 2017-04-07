package org.rutebanken.tiamat.rest.graphql;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.rutebanken.tiamat.TiamatIntegrationTest;

import static com.jayway.restassured.RestAssured.given;


public abstract class AbstractGraphQLResourceIntegrationTest extends TiamatIntegrationTest {

    protected static final String BASE_URI_GRAPHQL = "/jersey/graphql/";

    @Before
    public void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    protected ValidatableResponse executeGraphQL(String graphQlJsonQuery) {
        return executeGraphQL(graphQlJsonQuery,200);
    }

    protected ValidatableResponse executeGraphQL(String graphQlJsonQuery,int httpStatusCode) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(graphQlJsonQuery)
           .when()
                .post(BASE_URI_GRAPHQL)
                .then()
                .log().body()
                .statusCode(httpStatusCode)
                .assertThat();
    }



}
