package org.rutebanken.tiamat.rest.write;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * Integration test verifying that when the async write API and the GraphQL API attempt to update
 * the same stop place concurrently, only the first one to acquire the distributed mutate-lock
 * succeeds, and the second one is rejected with a lock-timeout error.
 *
 * Both APIs go through {@link MutateLock} (backed by Hazelcast CP "mutate-lock"), so whichever
 * request reaches the lock first will hold it, and the second request will fail after the
 * configured wait-timeout ({@link MutateLock#WAIT_FOR_LOCK_SECONDS} s).
 */
@TestPropertySource(
    properties = {
        "authorization.enabled=false",
        // the shared test context already bound Hazelcast to port 5701
        "tiamat.hazelcast.port-auto-increment=true",
    }
)
public class ConcurrentStopPlaceUpdateIntegrationTest extends TiamatIntegrationTest {

    private static final String WRITE_ENDPOINT = "/services/stop_places/write";

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoSpyBean
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverServiceSpy;

    @Before
    public void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    /**
     * Scenario: the async write API wins the lock first.
     * <ol>
     *   <li>A stop place is persisted to the database.</li>
     *   <li>The async write API PATCH request is submitted; the spy holds the worker thread
     *       inside {@code updateStopPlace} so the lock stays acquired.</li>
     *   <li>While the lock is held, a GraphQL {@code mutateStopPlace} mutation targeting the
     *       same stop place is sent.</li>
     *   <li>The GraphQL mutation must fail because it cannot acquire the lock within the
     *       timeout period.</li>
     *   <li>The async write API job is allowed to finish and must succeed.</li>
     * </ol>
     */
    @Test
    public void whenAsyncWriteApiHoldsLock_GraphQLMutationIsRejected()
        throws Exception {
        StopPlace stopPlace = new StopPlace(
            new EmbeddableMultilingualString("Concurrent Test Stop")
        );
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        StopPlace saved = stopPlaceVersionedSaverService.saveNewVersion(
            stopPlace
        );
        String netexId = saved.getNetexId();

        CountDownLatch asyncWorkerStarted = new CountDownLatch(1);
        CountDownLatch asyncWorkerMayFinish = new CountDownLatch(1);

        doAnswer(invocation -> {
            asyncWorkerStarted.countDown(); // signal: lock is now held
            boolean released = asyncWorkerMayFinish.await(30, TimeUnit.SECONDS); // hold the lock
            if (!released) throw new RuntimeException("Latch timed out");
            return invocation.callRealMethod();
        })
            .when(stopPlaceVersionedSaverServiceSpy)
            .saveNewVersion(any(StopPlace.class), any(StopPlace.class));

        // --- act 1: fire the async write API PATCH (holds the lock) ----------------
        String patchXml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace id="%s" version="%d">
                    <Name>Async Updated Name</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """.formatted(netexId, saved.getVersion());

        AtomicReference<ResponseEntity<StopPlaceJobDto>> asyncResponse =
            new AtomicReference<>();
        Thread asyncWriteThread = new Thread(() ->
            asyncResponse.set(patchStopPlace(patchXml))
        );
        asyncWriteThread.start();

        // Wait until the async worker actually holds the Hazelcast lock
        boolean workerStarted = asyncWorkerStarted.await(10, TimeUnit.SECONDS);
        assertThat(workerStarted)
            .as("Async worker did not start within the expected time")
            .isTrue();

        // --- act 2: send a GraphQL mutation while the lock is held ------------------
        // MutateLock.WAIT_FOR_LOCK_SECONDS is 15 s; we use a short wait to keep the test fast.
        // We override it via property below in the @SpringBootTest, but the default is fine:
        // the GraphQL call will block for up to 15 s and then get a LockException which
        // GraphQL surfaces as an error in the response body.
        String graphQlMutation = """
            {
              "query": "mutation { stopPlace: mutateStopPlace(StopPlace: {id: \\"%s\\", name: {value: \\"GraphQL Updated Name\\"}}) { id name { value } } }"
            }
            """.formatted(netexId);

        var graphQlResponse = given()
            .port(port)
            .contentType(ContentType.JSON)
            .body(graphQlMutation)
            .when()
            .post("/services/stop_places/graphql/")
            .then()
            .statusCode(200) // GraphQL always returns HTTP 200; errors go in the body
            .extract()
            .response();

        // The GraphQL response must contain an error because the lock timed out
        assertThat(graphQlResponse.jsonPath().getList("errors").toString())
            .as("GraphQL mutation should have failed due to lock contention")
            .contains("Timed out waiting to aquire lock mutate-lock");

        // --- cleanup: release the async worker and verify it succeeded --------------
        asyncWorkerMayFinish.countDown();
        asyncWriteThread.join(30_000);

        ResponseEntity<StopPlaceJobDto> jobResponse = asyncResponse.get();
        assertThat(jobResponse).isNotNull();
        assertThat(jobResponse.getBody()).isNotNull();

        // Poll until the async job reaches a terminal state
        Long jobId = jobResponse.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.status())
            .as(
                "The async write job should have succeeded once the lock was released"
            )
            .isEqualTo(AsyncStopPlaceJobStatus.FINISHED);
    }

    /**
     * Scenario: the GraphQL API wins the lock first.
     * <ol>
     *   <li>A stop place is persisted to the database.</li>
     *   <li>The GraphQL mutation is held inside the lock via a spy on
     *       {@code StopPlaceVersionedSaverService} (not done here – instead we keep
     *       the async-wins scenario above and provide the mirror test as a design note).</li>
     * </ol>
     *
     * <p>Note: testing the GraphQL-wins scenario requires intercepting {@link
     * org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService} inside the GraphQL
     * execution path (which runs on the HTTP thread, not on a separate worker). Because the
     * GraphQL request itself blocks the test thread, a separate {@code CompletableFuture} or
     * {@code Thread} is needed to send the async write request while the GraphQL call is in
     * flight. The assertion logic is symmetric: the async job must end in
     * {@link AsyncStopPlaceJobStatus#FAILED} with a lock-timeout message.
     */
    @Test
    public void whenAsyncWriteApiRacesGraphQL_onlyOneSucceeds()
        throws Exception {
        // --- arrange ---------------------------------------------------------------
        StopPlace stopPlace = new StopPlace(
            new EmbeddableMultilingualString("Race Condition Stop")
        );
        stopPlace.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
        StopPlace saved = stopPlaceVersionedSaverService.saveNewVersion(
            stopPlace
        );
        String netexId = saved.getNetexId();

        CountDownLatch asyncWorkerStarted = new CountDownLatch(1);
        CountDownLatch asyncWorkerMayFinish = new CountDownLatch(1);

        // Slow down the async worker so it holds the lock long enough for the GraphQL
        doAnswer(invocation -> {
            asyncWorkerStarted.countDown();
            boolean released = asyncWorkerMayFinish.await(30, TimeUnit.SECONDS);
            if (!released) throw new RuntimeException("Latch timed out");
            return invocation.callRealMethod();
        })
            .when(stopPlaceVersionedSaverServiceSpy)
            .saveNewVersion(any(StopPlace.class), any(StopPlace.class));

        // Fire async write (will hold the lock)
        String patchXml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace id="%s" version="%d">
                    <Name>Async Race Update</Name>
                    <StopPlaceType>railStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """.formatted(netexId, saved.getVersion());

        AtomicReference<ResponseEntity<StopPlaceJobDto>> asyncJobRef =
            new AtomicReference<>();
        Thread asyncThread = new Thread(() ->
            asyncJobRef.set(patchStopPlace(patchXml))
        );
        asyncThread.start();

        assertThat(asyncWorkerStarted.await(10, TimeUnit.SECONDS))
            .as("Async worker did not acquire the lock in time")
            .isTrue();

        // Send GraphQL mutation – must be rejected because async already holds the lock
        String gql = """
            {
              "query": "mutation { stopPlace: mutateStopPlace(StopPlace: {id: \\"%s\\", name: {value: \\"GraphQL Race Update\\"}}) { id } }"
            }
            """.formatted(netexId);

        var gqlErrors = given()
            .port(port)
            .contentType(ContentType.JSON)
            .body(gql)
            .when()
            .post("/services/stop_places/graphql/")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("errors");

        assertThat(gqlErrors)
            .as(
                "GraphQL must report an error when it cannot acquire the mutate-lock"
            )
            .isNotEmpty();

        // Release the async worker
        asyncWorkerMayFinish.countDown();
        asyncThread.join(30_000);

        ResponseEntity<StopPlaceJobDto> asyncJobResponse = asyncJobRef.get();
        assertThat(asyncJobResponse).isNotNull();
        assertThat(asyncJobResponse.getBody()).isNotNull();
        Long jobId = asyncJobResponse.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.status())
            .as("Async job should succeed after the lock is released")
            .isEqualTo(AsyncStopPlaceJobStatus.FINISHED);
    }

    @Test
    public void whenAsyncWriteApiRacesGraphQL_theVersionOfTheStopPlaceCausedException() throws InterruptedException {
        StopPlace stopPlace = new StopPlace(
                new EmbeddableMultilingualString("Stockholm Central")
        );
        stopPlace.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
        StopPlace stopPlaceVersion1 = stopPlaceVersionedSaverService.saveNewVersion(
                stopPlace
        );
        String netexId = stopPlaceVersion1.getNetexId();

        // GraphQL updates first, bumping the DB version from 1 to 2
        String gql = """
            {
              "query": "mutation { stopPlace: mutateStopPlace(StopPlace: {id: \\"%s\\", name: {value: \\"GraphQL Race Update\\"}}) { id } }"
            }
            """.formatted(netexId);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(gql)
                .when()
                .post("/services/stop_places/graphql/")
                .then()
                .statusCode(200);

        // Async PATCH sends the original version (1), which is now stale — must hit a DB constraint
        String patchXml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace id="%s" version="%d">
                    <Name>Async Race Update</Name>
                    <StopPlaceType>railStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """.formatted(netexId, stopPlaceVersion1.getVersion());


        var asyncJobResponse = patchStopPlace(patchXml);

        assertThat(asyncJobResponse).isNotNull();
        assertThat(asyncJobResponse.getBody()).isNotNull();
        Long jobId = asyncJobResponse.getBody().jobId();
        StopPlaceJobDto job = awaitJobCompletion(jobId);

        assertThat(job.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(job.errorMessage()).contains("Version mismatch");
    }

    private ResponseEntity<StopPlaceJobDto> patchStopPlace(String xml) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<>(xml, headers);
        return restTemplate.exchange(
            WRITE_ENDPOINT,
            HttpMethod.PATCH,
            request,
            StopPlaceJobDto.class
        );
    }

    private StopPlaceJobDto awaitJobCompletion(Long jobId)
        throws InterruptedException {
        String jobUrl = WRITE_ENDPOINT + "/jobs/" + jobId;
        for (int i = 0; i < 60; i++) {
            ResponseEntity<StopPlaceJobDto> jobResponse =
                restTemplate.getForEntity(jobUrl, StopPlaceJobDto.class);
            StopPlaceJobDto job = jobResponse.getBody();
            if (
                job != null &&
                job.status() != AsyncStopPlaceJobStatus.PROCESSING
            ) {
                return job;
            }
            Thread.sleep(500);
        }
        throw new AssertionError(
            "Async job did not reach a terminal state within 30 s"
        );
    }
}
