package org.rutebanken.tiamat.rest.graphql.misc;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.StopPlace;

import javax.transaction.Transactional;

import static org.rutebanken.tiamat.config.JerseyConfig.SERVICES_STOP_PLACE_PATH;

public abstract class AbstractGraphQLResourceIntegrationTest extends TiamatIntegrationTest {
    @Before
    public void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    protected ValidatableResponse executeGraphQL(String graphQlJsonQuery) {
        return executeGraphQL(graphQlJsonQuery, 200);
    }

    protected ValidatableResponse executeGraphqQLQueryOnly(String query) {
        return executeGraphqQLQueryOnly(query, 200);
    }

    /**
     * When sending empty parameters, specify 'query' directly.
     * Escapes quotes and newlines
     */
    protected ValidatableResponse executeGraphqQLQueryOnly(String query, int httpStatusCode) {

        String graphQlJsonQuery = "{" +
                "\"query\":\"" +
                query.replaceAll("\"", "\\\\\"")
                        .replaceAll("\n", "\\\\n") +
                "\",\"variables\":\"\"}";
        System.out.println(query);
        return executeGraphQL(graphQlJsonQuery, httpStatusCode);
    }

    protected ValidatableResponse executeGraphQL(String graphQlJsonQuery, int httpStatusCode) {
        return RestAssured.given().port(port).contentType(ContentType.JSON).body(graphQlJsonQuery).when().post(BASE_URI_GRAPHQL).then().log().body().statusCode(httpStatusCode).assertThat();
    }

    @Transactional
    protected StopPlace saveStopPlaceTransactional(StopPlace stopPlace) {
        return stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
    }

    protected static final String BASE_URI_GRAPHQL = SERVICES_STOP_PLACE_PATH + "/graphql/";
}
