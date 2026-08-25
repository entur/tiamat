package org.rutebanken.tiamat.ext.fintraffic.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.changelog.EntityChangedListener;
import org.rutebanken.tiamat.ext.fintraffic.FintrafficIntegrationTest;
import org.rutebanken.tiamat.ext.fintraffic.FintrafficTiamatTestApplication;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.rest.graphql.GraphQLNames;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.rutebanken.tiamat.config.JerseyConfig.SERVICES_STOP_PLACE_PATH;

/**
 * Reproduces the reported scenario: a brand-new Parking created via the real GraphQL
 * {@code mutateParking} editor path never appears in the Read API cache table, even though
 * the incremental sync path ({@code ParkingVersionedSaverService.sendToJMS} →
 * {@code ReadApiEntityChangedPublisher.onChange} → {@code ReadApiNetexMarshallingService
 * .handleEntityChange}) is invoked for every save. Activates the real {@code
 * fintraffic-read-api} profile (not mocked, unlike the other Read API unit tests) so the real
 * marshaller, search key service and repository run end to end.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = FintrafficTiamatTestApplication.class
)
@ActiveProfiles({"test", "gcs-blobstore", "fintraffic", "fintraffic-read-api"})
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
public class ReadApiParkingIncrementalSyncIntegrationTest extends FintrafficIntegrationTest {

    private static final String BASE_URI_GRAPHQL = SERVICES_STOP_PLACE_PATH + "/graphql/";

    /**
     * The {@code test} profile activates {@code EntityChangedEventLocalPublisher}
     * ({@code @Profile("local-changelog | test")}) alongside {@code fintraffic-read-api}'s
     * {@code ReadApiEntityChangedPublisher}, so every bean that autowires the single-bean
     * {@code EntityChangedListener} interface fails with a {@code NoUniqueBeanDefinitionException}.
     * Real deployments only ever activate one of the two (see {@code
     * spring.profiles.group.dev/tst/prd} in the peti-backend config, which never combines
     * {@code test}/{@code local-changelog} with {@code fintraffic-read-api}), so this ambiguity
     * is a test-only artifact. Marking the real Read API publisher {@code @Primary} here
     * reproduces the production wiring for this test without changing any production bean.
     * <p>
     * This class is a static nested {@code @TestConfiguration}, so the shared
     * {@code FintrafficTiamatTestApplication}'s {@code @ComponentScan(basePackages =
     * "org.rutebanken.tiamat")} picks it up as a real component in every test that boots that
     * application context, not just this one. Gating it with {@code @Profile("fintraffic-read-api")}
     * keeps it a no-op wherever that profile isn't active, so it can't break unrelated tests.
     */
    @TestConfiguration
    @Profile("fintraffic-read-api")
    static class PrimaryEntityChangedListenerConfig {
        @Bean
        @Primary
        EntityChangedListener primaryEntityChangedListener(ReadApiEntityChangedPublisher readApiEntityChangedPublisher) {
            return readApiEntityChangedPublisher;
        }
    }

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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private org.rutebanken.tiamat.ext.fintraffic.api.repository.NetexRepository netexRepository;

    @Autowired
    private org.springframework.transaction.PlatformTransactionManager transactionManager;

    @Before
    public void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @After
    public void cleanUp() {
        parkingRepository.deleteAll();
        stopPlaceRepository.deleteAll();
        jdbcTemplate.update("DELETE FROM ext_fintraffic_netex_entity");
    }

    /**
     * Reproduces the exact reported symptom: a brand-new Parking created via the real GraphQL
     * editor path must (a) sync a {@code type='Parking'} row into the Read API cache table, and
     * (b) actually be returned by the real {@code streamStopPlaces} repository method (the same
     * one the {@code GET /api/fintraffic/v1/stops} endpoint uses) — not just be present in the
     * table under some other type value. The StopPlace/Parking creation happens over real HTTP
     * (a separate thread/transaction/connection), so only the final {@code streamStopPlaces}
     * call (which requires an active transaction) is wrapped in one, via a {@code
     * TransactionTemplate} — wrapping the whole test method in {@code @Transactional} would hide
     * the StopPlace/Parking from the GraphQL server's own connection until commit.
     */
    @Test
    public void mutateParking_incrementalSync_writesRowToReadApiCacheTable() {
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
                .statusCode(200)
                .body("data.parking[0].id", notNullValue())
                .extract()
                .path("data.parking[0].id");

        Map<String, Object> row = jdbcTemplate.queryForMap(
                "SELECT id, type, status FROM ext_fintraffic_netex_entity WHERE id = ?", parkingNetexId);

        assertThat(row)
                .as("a brand-new Parking created via GraphQL must sync into the Read API cache table")
                .containsEntry("id", parkingNetexId)
                .containsEntry("type", "Parking")
                .containsEntry("status", "CURRENT");

        var transactionTemplate = new org.springframework.transaction.support.TransactionTemplate(transactionManager);
        java.util.List<String> matchingTypes = transactionTemplate.execute(status -> {
            try (var stream = netexRepository.streamStopPlaces(
                    org.rutebanken.tiamat.ext.fintraffic.api.model.FintrafficReadApiSearchKey.empty())) {
                return stream
                        .filter(r -> r.type().equals("Parking") && r.xml().contains(parkingNetexId))
                        .map(r -> r.type())
                        .toList();
            }
        });
        assertThat(matchingTypes)
                .as("the new Parking must be returned by the real Read API stream query")
                .hasSize(1);
    }
}
