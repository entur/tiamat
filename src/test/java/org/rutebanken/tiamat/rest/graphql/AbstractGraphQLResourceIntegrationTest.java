package org.rutebanken.tiamat.rest.graphql;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;
import static org.rutebanken.tiamat.config.JerseyConfig.SERVICES_STOP_PLACE_PATH;

public abstract class AbstractGraphQLResourceIntegrationTest extends TiamatIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGraphQLResourceIntegrationTest.class);

    protected static final String BASE_URI_GRAPHQL = SERVICES_STOP_PLACE_PATH + "/graphql/";

    @Before
    public void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    protected ValidatableResponse executeGraphQL(String graphQlJsonQuery) {
        return executeGraphQL(graphQlJsonQuery,200);
    }

    protected ValidatableResponse executeGraphQLQueryOnly(String query) {
        return executeGraphQLQueryOnly(query, 200);
    }

    /**
     * When sending empty parameters, specify 'query' directly.
     * Escape quotes and newlines
     */
    protected ValidatableResponse executeGraphQLQueryOnly(String query, int httpStatusCode) {

        String graphQlJsonQuery = "{" +
                "\"query\":\"" +
                query.replaceAll("\"", "\\\\\"")
                        .replaceAll("\n", "\\\\n") +
                "\",\"variables\":\"\"}";

        LOGGER.debug(query);
        return executeGraphQL(graphQlJsonQuery, httpStatusCode);
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

    /*
     * Wrapping save-operation in separate method to complete transaction before GraphQL-request is called
     */
    @Transactional
    protected StopPlace saveStopPlaceTransactional(StopPlace stopPlace) {
        return stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
    }
}
