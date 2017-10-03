/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.rest.graphql


import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import org.junit.Before
import org.rutebanken.tiamat.TiamatIntegrationTest
import org.rutebanken.tiamat.model.StopPlace

import javax.transaction.Transactional

import static io.restassured.RestAssured.given
import static org.rutebanken.tiamat.config.JerseyConfig.SERVICES_STOP_PLACE_PATH

abstract class AbstractGraphQLResourceIntegrationTest extends TiamatIntegrationTest {

    protected static final String BASE_URI_GRAPHQL = SERVICES_STOP_PLACE_PATH + "/graphql/"

    @Before
    void configureRestAssured() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
    }

    protected ValidatableResponse executeGraphQL(String graphQlJsonQuery) {
        return executeGraphQL(graphQlJsonQuery,200)
    }

    /**
     * When sending empty parameters, specify 'query' directly.
     * Escapes quotes and newlines
     */
    protected ValidatableResponse executeGraphqQLQueryOnly(String query) {

        String graphQlJsonQuery = "{" +
                "\"query\":\"" +
                query.replaceAll("\"", "\\\\\"")
                        .replaceAll("\n", "\\\\n") +
                "\",\"variables\":\"\"}"

        return executeGraphQL(graphQlJsonQuery)
    }

    protected ValidatableResponse executeGraphQL(String graphQlJsonQuery,int httpStatusCode) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(graphQlJsonQuery)
                .log().body()
           .when()
                .post(BASE_URI_GRAPHQL)
                .then()
                .log().body()
                .statusCode(httpStatusCode)
                .assertThat()
    }

    /*
    * Wrapping save-operation in separate method to complete transaction before GraphQL-request is called
    */
    @Transactional
    protected StopPlace saveStopPlaceTransactional(StopPlace stopPlace) {
        return stopPlaceVersionedSaverService.saveNewVersion(stopPlace)
    }
}
