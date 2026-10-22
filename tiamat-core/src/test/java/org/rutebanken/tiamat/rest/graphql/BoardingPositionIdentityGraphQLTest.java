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


package org.rutebanken.tiamat.rest.graphql;

import io.restassured.path.json.JsonPath;
import org.junit.Test;
import org.rutebanken.tiamat.model.StopPlace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

/**
 * A GraphQL save keeps the netexId of each boarding position that the client sends back with its id.
 */
public class BoardingPositionIdentityGraphQLTest extends AbstractGraphQLResourceIntegrationTest {

    private String stopPlaceId;

    private String createQuayWithPosition() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new org.rutebanken.tiamat.model.EmbeddableMultilingualString("Boarding stop"));
        stopPlaceRepository.save(stopPlace);
        stopPlaceId = stopPlace.getNetexId();
        executeGraphqQLQueryOnly("""
                mutation { stopPlace: %s (StopPlace: { id: "%s"
                  quays: [ { boardingPositions: [ { publicCode: "A" } ] } ] }) { id } }"""
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlaceId));
        return stopPlaceId;
    }

    private JsonPath readQuay() {
        return executeGraphqQLQueryOnly("""
                { stopPlace: stopPlace(id: "%s") { ... on StopPlace { quays { id boardingPositions { id publicCode } } } } }"""
                .formatted(stopPlaceId)).extract().jsonPath().setRootPath("data.stopPlace[0].quays[0]");
    }

    @Test
    public void resentBoardingPositionKeepsIdAndTakesTheEditedPublicCode() {
        createQuayWithPosition();
        JsonPath before = readQuay();
        String quayId = before.getString("id");
        String positionId = before.getString("boardingPositions[0].id");
        assertThat(positionId).isNotNull();

        executeGraphqQLQueryOnly("""
                mutation { stopPlace: %s (StopPlace: { id: "%s"
                  quays: [ { id: "%s" boardingPositions: [ { id: "%s" publicCode: "B" } ] } ] }) { id } }"""
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlaceId, quayId, positionId));

        JsonPath after = readQuay();
        assertThat(after.getList("boardingPositions")).hasSize(1);
        assertThat(after.getString("boardingPositions[0].id")).isEqualTo(positionId);
        assertThat(after.getString("boardingPositions[0].publicCode")).isEqualTo("B");
    }

    @Test
    public void resentBoardingPositionWithNullGeometryClearsTheGeometry() {
        createQuayWithPosition();
        String quayId = readQuay().getString("id");
        String positionId = readQuay().getString("boardingPositions[0].id");
        executeGraphqQLQueryOnly("""
                mutation { stopPlace: %s (StopPlace: { id: "%s"
                  quays: [ { id: "%s" boardingPositions: [ { id: "%s" publicCode: "A" geometry: { type: Point coordinates: [10.5, 59.0] } } ] } ] }) { id } }"""
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlaceId, quayId, positionId));
        assertThat(readPositionGeometry()).isNotNull();

        executeGraphqQLQueryOnly("""
                mutation { stopPlace: %s (StopPlace: { id: "%s"
                  quays: [ { id: "%s" boardingPositions: [ { id: "%s" publicCode: "A" geometry: null } ] } ] }) { id } }"""
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlaceId, quayId, positionId));

        assertThat(readPositionGeometry()).isNull();
    }

    private Object readPositionGeometry() {
        return executeGraphqQLQueryOnly("""
                { stopPlace: stopPlace(id: "%s") { ... on StopPlace { quays { boardingPositions { geometry { type } } } } } }"""
                .formatted(stopPlaceId)).extract().jsonPath().get("data.stopPlace[0].quays[0].boardingPositions[0].geometry");
    }

    @Test
    public void unknownBoardingPositionIdIsRejectedWithPositionAndQuayId() {
        createQuayWithPosition();
        String quayId = readQuay().getString("id");

        executeGraphqQLQueryOnly("""
                mutation { stopPlace: %s (StopPlace: { id: "%s"
                  quays: [ { id: "%s" boardingPositions: [ { id: "NSR:BoardingPosition:999999" publicCode: "B" } ] } ] }) { id } }"""
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlaceId, quayId), 400)
                .body("errors[0].message", allOf(
                        containsString("NSR:BoardingPosition:999999"), containsString(quayId)));
    }
}
