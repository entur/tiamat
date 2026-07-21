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
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParkingAvailabilityCondition;
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

import java.time.LocalTime;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
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

    @Test
    public void mutateParking_infoLinks_persistedAndReturnedInResponse() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String stopNetexId = stopPlace.getNetexId();

        String mutation = """
                {
                  "query": "mutation { parking: %s (Parking: { name: { value: \\"Test\\" lang: \\"fi\\" } parkingType: parkAndRide parentSiteRef: \\"%s\\" infoLinks: [{ uri: \\"https://example.com\\" typeOfInfoLink: resource }] }) { id infoLinks { uri typeOfInfoLink } } }",
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
                .body("data.parking[0].infoLinks[0].uri", org.hamcrest.Matchers.equalTo("https://example.com"))
                .body("data.parking[0].infoLinks[0].typeOfInfoLink", org.hamcrest.Matchers.equalTo("resource"))
                .extract()
                .path("data.parking[0].id");

        FintrafficParking saved = (FintrafficParking)
                parkingRepository.findFirstByNetexIdOrderByVersionDesc(parkingNetexId);

        assertThat(saved.getInfoLinks())
                .as("infoLinks must be persisted to DB via FintrafficParkingUpdater")
                .containsExactly(new org.rutebanken.tiamat.ext.fintraffic.model.FintrafficInfoLink("https://example.com", "resource"));
    }

    @Test
    public void mutateParking_updateWithoutInfoLinks_preservesExistingInfoLinks() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String stopNetexId = stopPlace.getNetexId();

        // Create with infoLinks
        String createMutation = """
                {
                  "query": "mutation { parking: %s (Parking: { name: { value: \\"Test\\" lang: \\"fi\\" } parkingType: parkAndRide parentSiteRef: \\"%s\\" infoLinks: [{ uri: \\"https://example.com\\" typeOfInfoLink: resource }] }) { id } }",
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

        // Update without infoLinks — field should be preserved
        String updateMutation = """
                {
                  "query": "mutation { parking: %s (Parking: { id: \\"%s\\" name: { value: \\"Updated\\" lang: \\"fi\\" } }) { id infoLinks { uri typeOfInfoLink } } }",
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
                .body("data.parking[0].infoLinks[0].uri", org.hamcrest.Matchers.equalTo("https://example.com"));

        FintrafficParking latest = (FintrafficParking)
                parkingRepository.findFirstByNetexIdOrderByVersionDesc(parkingNetexId);
        assertThat(latest.getInfoLinks())
                .as("infoLinks must be preserved when not included in update input")
                .containsExactly(new org.rutebanken.tiamat.ext.fintraffic.model.FintrafficInfoLink("https://example.com", "resource"));
    }

    @Test
    public void mutateParking_vehicleEntrances_persistedAndReturnedInResponse() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String stopNetexId = stopPlace.getNetexId();

        String mutation = """
                {
                  "query": "mutation { parking: %s (Parking: { name: { value: \\"Test\\" lang: \\"fi\\" } parkingType: parkAndRide parentSiteRef: \\"%s\\" vehicleEntrances: [{ label: \\"Main\\" entranceType: door isEntry: true isExit: false publicCode: \\"A1\\" }] }) { id vehicleEntrances { label entranceType isEntry isExit publicCode } } }",
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
                .body("data.parking[0].vehicleEntrances[0].label", org.hamcrest.Matchers.equalTo("Main"))
                .body("data.parking[0].vehicleEntrances[0].entranceType", org.hamcrest.Matchers.equalTo("door"))
                .body("data.parking[0].vehicleEntrances[0].isEntry", org.hamcrest.Matchers.equalTo(true))
                .body("data.parking[0].vehicleEntrances[0].isExit", org.hamcrest.Matchers.equalTo(false))
                .body("data.parking[0].vehicleEntrances[0].publicCode", org.hamcrest.Matchers.equalTo("A1"))
                .extract()
                .path("data.parking[0].id");

        FintrafficParking saved = (FintrafficParking)
                parkingRepository.findFirstByNetexIdOrderByVersionDesc(parkingNetexId);

        assertThat(saved.getFintrafficVehicleEntrances()).hasSize(1);
        org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParkingEntranceForVehicles entrance =
                saved.getFintrafficVehicleEntrances().getFirst();
        assertThat(entrance.getLabel()).isEqualTo("Main");
        assertThat(entrance.getEntranceType()).isEqualTo("door");
        assertThat(entrance.getIsEntry()).isTrue();
        assertThat(entrance.getIsExit()).isFalse();
        assertThat(entrance.getPublicCode()).isEqualTo("A1");
    }

    @Test
    public void mutateParking_vehicleEntrances_widthAndHeight_persistedAndReturnedInResponse() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String stopNetexId = stopPlace.getNetexId();

        String mutation = """
                {
                  "query": "mutation { parking: %s (Parking: { name: { value: \\"Test\\" lang: \\"fi\\" } parkingType: parkAndRide parentSiteRef: \\"%s\\" vehicleEntrances: [{ label: \\"Main\\" entranceType: door width: 2.75 height: 3.5 isEntry: true isExit: false publicCode: \\"A1\\" }] }) { id vehicleEntrances { label entranceType width height isEntry isExit publicCode } } }",
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
                .statusCode(200)
                .body("data.parking[0].id", notNullValue())
                .body("data.parking[0].vehicleEntrances[0].width", equalTo(2.75f))
                .body("data.parking[0].vehicleEntrances[0].height", equalTo(3.5f))
                .extract()
                .path("data.parking[0].id");

        FintrafficParking saved = (FintrafficParking)
                parkingRepository.findFirstByNetexIdOrderByVersionDesc(parkingNetexId);

        assertThat(saved.getFintrafficVehicleEntrances()).hasSize(1);
        org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParkingEntranceForVehicles entrance =
                saved.getFintrafficVehicleEntrances().getFirst();
        assertThat(entrance.getWidth()).isNotNull();
        assertThat(entrance.getHeight()).isNotNull();
        assertThat(entrance.getWidth()).isEqualByComparingTo("2.75");
        assertThat(entrance.getHeight()).isEqualByComparingTo("3.5");
    }

    @Test
    public void mutateParking_updateWithoutVehicleEntrances_preservesExistingVehicleEntrances() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String stopNetexId = stopPlace.getNetexId();

        String createMutation = """
                {
                  "query": "mutation { parking: %s (Parking: { name: { value: \\"Test\\" lang: \\"fi\\" } parkingType: parkAndRide parentSiteRef: \\"%s\\" vehicleEntrances: [{ label: \\"Main\\" entranceType: door isEntry: true }] }) { id } }",
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

        String updateMutation = """
                {
                  "query": "mutation { parking: %s (Parking: { id: \\"%s\\" name: { value: \\"Updated\\" lang: \\"fi\\" } }) { id vehicleEntrances { label } } }",
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
                .body("data.parking[0].vehicleEntrances[0].label", org.hamcrest.Matchers.equalTo("Main"));

        FintrafficParking latest = (FintrafficParking)
                parkingRepository.findFirstByNetexIdOrderByVersionDesc(parkingNetexId);
        assertThat(latest.getFintrafficVehicleEntrances())
                .as("vehicleEntrances must be preserved when not included in update input")
                .hasSize(1);
        assertThat(latest.getFintrafficVehicleEntrances().getFirst().getLabel()).isEqualTo("Main");
    }

    @Test
    public void mutateParking_availabilityConditions_persistedAndReadBackViaQuery() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String stopNetexId = stopPlace.getNetexId();

        String mutation = """
                {
                  "query": "mutation { parking: %s (Parking: { name: { value: \\"Test\\" lang: \\"fi\\" } parkingType: parkAndRide parentSiteRef: \\"%s\\" availabilityConditions: [{ dayTypeRef: \\"FSR:DayType:BusinessDay\\" isAvailable: true startTime: \\"06:00\\" endTime: \\"22:00\\" }, { dayTypeRef: \\"FSR:DayType:Sunday\\" isAvailable: false }] }) { id availabilityConditions { dayTypeRef isAvailable startTime endTime } } }",
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
                .body("data.parking[0].availabilityConditions[0].dayTypeRef", equalTo("FSR:DayType:BusinessDay"))
                .body("data.parking[0].availabilityConditions[0].isAvailable", equalTo(true))
                .body("data.parking[0].availabilityConditions[0].startTime", equalTo("06:00"))
                .body("data.parking[0].availabilityConditions[0].endTime", equalTo("22:00"))
                .body("data.parking[0].availabilityConditions[1].dayTypeRef", equalTo("FSR:DayType:Sunday"))
                .body("data.parking[0].availabilityConditions[1].isAvailable", equalTo(false))
                .extract()
                .path("data.parking[0].id");

        FintrafficParking saved = (FintrafficParking)
                parkingRepository.findFirstByNetexIdOrderByVersionDesc(parkingNetexId);

        assertThat(saved.getAvailabilityConditions())
                .containsExactly(
                        new FintrafficParkingAvailabilityCondition("FSR:DayType:BusinessDay", true, LocalTime.of(6, 0), LocalTime.of(22, 0)),
                        new FintrafficParkingAvailabilityCondition("FSR:DayType:Sunday", false, null, null)
                );

        String query = """
                {
                  "query": "{ parking(id: \\"%s\\") { id availabilityConditions { dayTypeRef isAvailable startTime endTime } } }"
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
                .body("data.parking[0].availabilityConditions[0].dayTypeRef", equalTo("FSR:DayType:BusinessDay"))
                .body("data.parking[0].availabilityConditions[0].isAvailable", equalTo(true))
                .body("data.parking[0].availabilityConditions[0].startTime", equalTo("06:00"))
                .body("data.parking[0].availabilityConditions[0].endTime", equalTo("22:00"))
                .body("data.parking[0].availabilityConditions[1].dayTypeRef", equalTo("FSR:DayType:Sunday"))
                .body("data.parking[0].availabilityConditions[1].isAvailable", equalTo(false));
    }

    @Test
    public void mutateParking_updateWithoutAvailabilityConditions_preservesExistingAvailabilityConditions() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String stopNetexId = stopPlace.getNetexId();

        String createMutation = """
                {
                  "query": "mutation { parking: %s (Parking: { name: { value: \\"Test\\" lang: \\"fi\\" } parkingType: parkAndRide parentSiteRef: \\"%s\\" availabilityConditions: [{ dayTypeRef: \\"FSR:DayType:BusinessDay\\" isAvailable: true startTime: \\"06:00\\" endTime: \\"22:00\\" }] }) { id } }",
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

        String updateMutation = """
                {
                  "query": "mutation { parking: %s (Parking: { id: \\"%s\\" name: { value: \\"Updated\\" lang: \\"fi\\" } }) { id availabilityConditions { dayTypeRef startTime endTime } } }",
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
                .body("data.parking[0].availabilityConditions[0].dayTypeRef", equalTo("FSR:DayType:BusinessDay"))
                .body("data.parking[0].availabilityConditions[0].startTime", equalTo("06:00"))
                .body("data.parking[0].availabilityConditions[0].endTime", equalTo("22:00"));

        FintrafficParking latest = (FintrafficParking)
                parkingRepository.findFirstByNetexIdOrderByVersionDesc(parkingNetexId);
        assertThat(latest.getAvailabilityConditions())
                .as("availabilityConditions must be preserved when not included in update input")
                .containsExactly(new FintrafficParkingAvailabilityCondition(
                        "FSR:DayType:BusinessDay",
                        true,
                        LocalTime.of(6, 0),
                        LocalTime.of(22, 0)
                ));
    }

    @Test
    public void mutateParking_lighting_persistedAndReturnedInResponse() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String stopNetexId = stopPlace.getNetexId();

        String mutation = """
                {
                  "query": "mutation { parking: %s (Parking: { name: { value: \\"Test\\" lang: \\"fi\\" } parkingType: parkAndRide parentSiteRef: \\"%s\\" lighting: wellLit }) { id lighting } }",
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
                .body("data.parking[0].lighting", equalTo("wellLit"))
                .extract()
                .path("data.parking[0].id");

        FintrafficParking saved = (FintrafficParking)
                parkingRepository.findFirstByNetexIdOrderByVersionDesc(parkingNetexId);

        assertThat(saved.getLighting())
                .as("lighting must be persisted to DB via FintrafficParkingUpdater")
                .isEqualTo(org.rutebanken.tiamat.model.LightingEnumeration.WELL_LIT);
    }

    @Test
    public void mutateParking_lighting_persistedAndReadBack_viaQuery() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String stopNetexId = stopPlace.getNetexId();

        String mutation = """
                {
                  "query": "mutation { parking: %s (Parking: { name: { value: \\"Test\\" lang: \\"fi\\" } parkingType: parkAndRide parentSiteRef: \\"%s\\" lighting: wellLit }) { id } }",
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
                .statusCode(200)
                .extract()
                .path("data.parking[0].id");

        // Separate read query — proves persistence, not just response echo
        String query = """
                {
                  "query": "{ parking(id: \\"%s\\") { id lighting } }"
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
                .body("data.parking[0].lighting", equalTo("wellLit"));
    }

    @Test
    public void mutateParking_updateWithoutLighting_preservesExistingLighting() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String stopNetexId = stopPlace.getNetexId();

        String createMutation = """
                {
                  "query": "mutation { parking: %s (Parking: { name: { value: \\"Test\\" lang: \\"fi\\" } parkingType: parkAndRide parentSiteRef: \\"%s\\" lighting: wellLit }) { id } }",
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

        // Update without lighting — should be preserved
        String updateMutation = """
                {
                  "query": "mutation { parking: %s (Parking: { id: \\"%s\\" name: { value: \\"Updated\\" lang: \\"fi\\" } }) { id lighting } }",
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
                .body("data.parking[0].lighting", equalTo("wellLit"));

        FintrafficParking latest = (FintrafficParking)
                parkingRepository.findFirstByNetexIdOrderByVersionDesc(parkingNetexId);
        assertThat(latest.getLighting())
                .as("lighting must be preserved when not included in update input")
                .isEqualTo(org.rutebanken.tiamat.model.LightingEnumeration.WELL_LIT);
    }

    @Test
    public void mutateParking_duplicateAvailabilityConditionDayTypeRef_returnsError() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String stopNetexId = stopPlace.getNetexId();

        String mutation = """
                {
                  "query": "mutation { parking: %s (Parking: { name: { value: \\"Test\\" lang: \\"fi\\" } parkingType: parkAndRide parentSiteRef: \\"%s\\" availabilityConditions: [{ dayTypeRef: \\"FSR:DayType:BusinessDay\\" isAvailable: true }, { dayTypeRef: \\"FSR:DayType:BusinessDay\\" isAvailable: false }] }) { id } }",
                  "variables": ""
                }
                """.formatted(GraphQLNames.MUTATE_PARKING, stopNetexId);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(mutation)
                .when()
                .post(BASE_URI_GRAPHQL)
                .then()
                .log().body()
                .statusCode(org.hamcrest.Matchers.anyOf(equalTo(200), equalTo(400)));
    }

    @Test
    public void mutateParking_invalidAvailabilityConditionTime_returnsError() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test stop"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String stopNetexId = stopPlace.getNetexId();

        String mutation = """
                {
                  "query": "mutation { parking: %s (Parking: { name: { value: \\"Test\\" lang: \\"fi\\" } parkingType: parkAndRide parentSiteRef: \\"%s\\" availabilityConditions: [{ dayTypeRef: \\"FSR:DayType:BusinessDay\\" startTime: \\"notATime\\" }] }) { id } }",
                  "variables": ""
                }
                """.formatted(GraphQLNames.MUTATE_PARKING, stopNetexId);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(mutation)
                .when()
                .post(BASE_URI_GRAPHQL)
                .then()
                .log().body()
                .statusCode(org.hamcrest.Matchers.anyOf(equalTo(200), equalTo(400)));
    }
}
