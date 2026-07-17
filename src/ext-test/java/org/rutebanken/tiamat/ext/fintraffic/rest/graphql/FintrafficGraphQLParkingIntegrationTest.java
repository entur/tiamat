package org.rutebanken.tiamat.ext.fintraffic.rest.graphql;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.ext.fintraffic.FintrafficIntegrationTest;
import org.rutebanken.tiamat.ext.fintraffic.FintrafficTiamatTestApplication;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.rest.graphql.GraphQLNames;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;
import static org.rutebanken.tiamat.config.JerseyConfig.SERVICES_STOP_PLACE_PATH;
/**
 * Integration test verifying that the GraphQL {@code mutateParking} operation uses
 * {@link org.rutebanken.tiamat.model.factory.ParkingEntityFactory} to produce a
 * {@link FintrafficParking} instance when the {@code fintraffic} profile is active.
 * <p>
 * Note: {@code paymentMethods} is not yet part of the GraphQL schema; the GraphQL
 * layer tests that the correct entity subtype is created and persisted. The
 * {@code paymentMethods} DB round-trip is covered by
 * {@link org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParkingIntegrationTest}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = FintrafficTiamatTestApplication.class
)
@ActiveProfiles({"test", "gcs-blobstore", "fintraffic"})
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
public class FintrafficGraphQLParkingIntegrationTest extends FintrafficIntegrationTest {

    private static final String BASE_URI_GRAPHQL = SERVICES_STOP_PLACE_PATH + "/graphql/";

    @MockitoBean
    private AuthorizationService authorizationService;

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private ParkingRepository parkingRepository;

    @Before
    public void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @After
    public void cleanUp() {
        parkingRepository.deleteAll();
        stopPlaceRepository.deleteAll();
    }

    @Test
    public void mutateParking_createsFintrafficParkingViaFactory() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String stopNetexId = stopPlace.getNetexId();

        String mutation = """
                {
                  "query": "mutation { parking: %s (Parking: { name: { value: \\"Test parking\\" lang: \\"fi\\" } parkingType: parkAndRide parentSiteRef: \\"%s\\" }) { id } }",
                  "variables": ""
                }
                """.formatted(GraphQLNames.MUTATE_PARKING, stopNetexId);

        String parkingNetexId = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(mutation)
                .when()
                .post(BASE_URI_GRAPHQL)
                .then()
                .log().body()
                .statusCode(200)
                .body("data.parking[0].id", notNullValue())
                .extract()
                .path("data.parking[0].id");

        Parking saved = parkingRepository.findFirstByNetexIdOrderByVersionDesc(parkingNetexId);

        assertThat(saved)
                .as("ParkingEntityFactory must produce FintrafficParking via GraphQL mutateParking")
                .isInstanceOf(FintrafficParking.class);
    }

    @Test
    public void mutateParking_updatePreservesFintrafficParkingType() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String stopNetexId = stopPlace.getNetexId();

        // Create
        String createMutation = """
                {
                  "query": "mutation { parking: %s (Parking: { name: { value: \\"Original\\" lang: \\"fi\\" } parkingType: parkAndRide parentSiteRef: \\"%s\\" }) { id } }",
                  "variables": ""
                }
                """.formatted(GraphQLNames.MUTATE_PARKING, stopNetexId);

        String parkingNetexId = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(createMutation)
                .when()
                .post(BASE_URI_GRAPHQL)
                .then()
                .statusCode(200)
                .extract()
                .path("data.parking[0].id");

        // Update
        String updateMutation = """
                {
                  "query": "mutation { parking: %s (Parking: { id: \\"%s\\" name: { value: \\"Updated\\" lang: \\"fi\\" } }) { id version } }",
                  "variables": ""
                }
                """.formatted(GraphQLNames.MUTATE_PARKING, parkingNetexId);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(updateMutation)
                .when()
                .post(BASE_URI_GRAPHQL)
                .then()
                .log().body()
                .statusCode(200)
                .body("data.parking[0].id", notNullValue());

        // Both versions should be FintrafficParking
        parkingRepository.findByNetexId(parkingNetexId).forEach(p ->
                assertThat(p)
                        .as("All versions of a parking must be FintrafficParking instances")
                        .isInstanceOf(FintrafficParking.class)
        );
    }

    @Test
    public void mutateParking_paymentMethods_persistedAndReturnedInResponse() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String stopNetexId = stopPlace.getNetexId();

        String mutation = """
                {
                  "query": "mutation { parking: %s (Parking: { name: { value: \\"Test\\" lang: \\"fi\\" } parkingType: parkAndRide parentSiteRef: \\"%s\\" paymentMethods: [cash, creditCard] }) { id paymentMethods } }",
                  "variables": ""
                }
                """.formatted(GraphQLNames.MUTATE_PARKING, stopNetexId);

        String parkingNetexId = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(mutation)
                .when()
                .post(BASE_URI_GRAPHQL)
                .then()
                .log().body()
                .statusCode(200)
                .body("data.parking[0].id", notNullValue())
                .body("data.parking[0].paymentMethods", hasItems("cash", "creditCard"))
                .extract()
                .path("data.parking[0].id");

        FintrafficParking saved = (FintrafficParking)
                parkingRepository.findFirstByNetexIdOrderByVersionDesc(parkingNetexId);

        assertThat(saved.getPaymentMethods())
                .as("paymentMethods must be persisted to DB via FintrafficParkingUpdater")
                .containsExactlyInAnyOrder(
                        PaymentMethodEnumeration.CASH,
                        PaymentMethodEnumeration.CREDIT_CARD);
    }

    /**
     * Verifies that {@link FintrafficParkingUpdater#preserveExtendedFields} copies
     * {@code paymentMethods} from the existing version into the version copy when an
     * update mutation omits the field.  Without this hook, Orika's {@code createCopy}
     * would not transfer the private {@link FintrafficParking#paymentMethods} field and
     * the update would silently clear any previously saved payment methods.
     */
    @Test
    public void mutateParking_updateWithoutPaymentMethods_preservesExistingPaymentMethods() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String stopNetexId = stopPlace.getNetexId();

        // Create with paymentMethods
        String createMutation = """
                {
                  "query": "mutation { parking: %s (Parking: { name: { value: \\"Test\\" lang: \\"fi\\" } parkingType: parkAndRide parentSiteRef: \\"%s\\" paymentMethods: [cash, creditCard] }) { id paymentMethods } }",
                  "variables": ""
                }
                """.formatted(GraphQLNames.MUTATE_PARKING, stopNetexId);

        String parkingNetexId = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(createMutation)
                .when()
                .post(BASE_URI_GRAPHQL)
                .then()
                .statusCode(200)
                .extract()
                .path("data.parking[0].id");

        // Update without paymentMethods — field should be preserved
        String updateMutation = """
                {
                  "query": "mutation { parking: %s (Parking: { id: \\"%s\\" name: { value: \\"Updated\\" lang: \\"fi\\" } }) { id paymentMethods } }",
                  "variables": ""
                }
                """.formatted(GraphQLNames.MUTATE_PARKING, parkingNetexId);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(updateMutation)
                .when()
                .post(BASE_URI_GRAPHQL)
                .then()
                .log().body()
                .statusCode(200)
                .body("data.parking[0].paymentMethods", hasItems("cash", "creditCard"));

        FintrafficParking latest = (FintrafficParking)
                parkingRepository.findFirstByNetexIdOrderByVersionDesc(parkingNetexId);
        assertThat(latest.getPaymentMethods())
                .as("paymentMethods must be preserved when not included in update input")
                .containsExactlyInAnyOrder(
                        PaymentMethodEnumeration.CASH,
                        PaymentMethodEnumeration.CREDIT_CARD);
    }

    /**
     * Verifies that {@code paymentMethods} persisted via {@code mutateParking} are returned by
     * the GraphQL {@code parking} query. This exercises the full read path:
     * {@code parkingFetcher} → {@link org.rutebanken.tiamat.repository.ParkingRepository} →
     * {@link org.rutebanken.tiamat.netex.mapping.NetexMapper#mapToNetexModel} →
     * {@link org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor}.
     */
    @Test
    public void parkingQuery_returnsPaymentMethods_whenFintrafficProfileActive() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String stopNetexId = stopPlace.getNetexId();

        // Create parking with paymentMethods via mutation
        String createMutation = """
                {
                  "query": "mutation { parking: %s (Parking: { name: { value: \\"Test\\" lang: \\"fi\\" } parkingType: parkAndRide parentSiteRef: \\"%s\\" paymentMethods: [cash, creditCard] }) { id } }",
                  "variables": ""
                }
                """.formatted(GraphQLNames.MUTATE_PARKING, stopNetexId);

        String parkingNetexId = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(createMutation)
                .when()
                .post(BASE_URI_GRAPHQL)
                .then()
                .statusCode(200)
                .extract()
                .path("data.parking[0].id");

        // Query it back via the parking read query
        String query = """
                {
                  "query": "{ parking(id: \\"%s\\") { id paymentMethods } }"
                }
                """.formatted(parkingNetexId);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post(BASE_URI_GRAPHQL)
                .then()
                .log().body()
                .statusCode(200)
                .body("data.parking[0].id", notNullValue())
                .body("data.parking[0].paymentMethods", hasItems("cash", "creditCard"));
    }

    /**
     * Regression test: when {@code fintraffic} profile is active, the {@code stopPlace} GraphQL
     * query must be served by {@code stopPlaceFetcher}, not hijacked by any {@code @Primary}
     * bean override intended only for {@code parkingUpdater}. The field must never be {@code null}
     * (an empty list is acceptable; {@code null} means the wrong fetcher was injected).
     */
    @Test
    public void stopPlaceQuery_notNull_whenFintrafficProfileActive() {
        String query = """
                {
                  "query": "{ stopPlace(query: \\"nonexistent_xyz_regression_check\\", size: 1) { id } }"
                }
                """;

        Object result = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(query)
                .when()
                .post(BASE_URI_GRAPHQL)
                .then()
                .statusCode(200)
                .extract()
                .path("data.stopPlace");

        assertThat(result)
                .as("stopPlace field must not be null — null means stopPlaceFetcher was replaced " +
                    "by an unrelated @Primary bean (e.g. FintrafficParkingUpdater)")
                .isNotNull();
    }
}
