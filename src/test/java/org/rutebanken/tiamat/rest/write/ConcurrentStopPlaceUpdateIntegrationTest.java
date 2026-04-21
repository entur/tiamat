package org.rutebanken.tiamat.rest.write;

import com.hazelcast.cp.lock.FencedLock;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test verifying that when the async write API and the GraphQL API attempt to update
 * the same stop place concurrently, only the first one to acquire the distributed mutate-lock
 * succeeds, and the second one is rejected with a lock-timeout error.
 *
 * Both APIs go through {@link MutateLock} (backed by Hazelcast CP "mutate-lock"), so whichever
 * request reaches the lock first will hold it, and the second request will fail after the
 * configured wait-timeout ({@link MutateLock#WAIT_FOR_LOCK_SECONDS} s).
 */
public class ConcurrentStopPlaceUpdateIntegrationTest extends TiamatIntegrationTest {

    private static final String WRITE_ENDPOINT = "/services/stop_places/write";

    @Autowired
    private TestRestTemplate restTemplate;

    /** Background thread that holds the Hazelcast CP mutate-lock during a test. */
    private Thread lockHolderThread;

    @Before
    public void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @After
    public void releaseLockIfHeld() throws InterruptedException {
        if (lockHolderThread != null && lockHolderThread.isAlive()) {
            lockHolderThread.interrupt();
            lockHolderThread.join(5_000);
        }
    }

    // -------------------------------------------------------------------------
    // Helper: acquire the mutate-lock on a background thread and hold it until
    // the returned CountDownLatch is counted down (or the thread is interrupted).
    // -------------------------------------------------------------------------

    /**
     * Acquires the Hazelcast CP {@value MutateLock#LOCK_NAME} lock on a background thread.
     *
     * @param lockAcquired latch signaled once the lock is held
     * @param releaseLock  latch the background thread waits on before releasing the lock
     * @return the background thread (already started)
     */
    private Thread holdLockInBackground(CountDownLatch lockAcquired, CountDownLatch releaseLock) {
        Thread t = new Thread(() -> {
            FencedLock lock = hazelcastInstance.getCPSubsystem().getLock(MutateLock.LOCK_NAME);
            lock.lock();
            try {
                lockAcquired.countDown();
                releaseLock.await(); // hold until the test signals release
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }, "mutate-lock-holder");
        lockHolderThread = t;
        t.setDaemon(true);
        t.start();
        return t;
    }

    /**
     * Scenario: the mutate-lock is held (simulating another write in progress).
     * A GraphQL {@code mutateStopPlace} mutation must be rejected with a lock-timeout error.
     * After the lock is released a PATCH via the async write API must succeed.
     */
    @Test
    public void whenLockIsHeld_GraphQLMutationIsRejected() throws Exception {
        // --- arrange -----------------------------------------------------------
        StopPlace stopPlace = new StopPlace(
                new EmbeddableMultilingualString("Concurrent Test Stop")
        );
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        StopPlace saved = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String netexId = saved.getNetexId();

        CountDownLatch lockAcquired = new CountDownLatch(1);
        CountDownLatch releaseLock  = new CountDownLatch(1);

        Thread lockHolder = holdLockInBackground(lockAcquired, releaseLock);

        assertThat(lockAcquired.await(10, TimeUnit.SECONDS))
                .as("Background thread did not acquire the lock in time")
                .isTrue();

        // --- act: send GraphQL mutation while the lock is held -----------------
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

        // --- cleanup: release the lock and run an async PATCH that should succeed
        releaseLock.countDown();
        lockHolder.join(10_000);

        String patchXml = """
                <stopPlaces xmlns="http://www.netex.org.uk/netex">
                    <StopPlace id="%s" version="%d">
                        <Name>Async Updated Name</Name>
                        <StopPlaceType>busStation</StopPlaceType>
                    </StopPlace>
                </stopPlaces>
                """.formatted(netexId, saved.getVersion());

        ResponseEntity<StopPlaceJobDto> jobResponse = patchStopPlace(patchXml);
        assertThat(jobResponse.getBody()).isNotNull();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobResponse.getBody().jobId());

        assertThat(finalJob.status())
                .as("Async write job should succeed once the lock is released")
                .isEqualTo(AsyncStopPlaceJobStatus.FINISHED);
    }

    /**
     * Symmetric scenario: the async write API job must fail when it cannot acquire the lock.
     */
    @Test
    public void whenLockIsHeld_AsyncWriteJobFails() throws Exception {
        // --- arrange -----------------------------------------------------------
        StopPlace stopPlace = new StopPlace(
                new EmbeddableMultilingualString("Race Condition Stop")
        );
        stopPlace.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
        StopPlace saved = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        String netexId = saved.getNetexId();

        CountDownLatch lockAcquired = new CountDownLatch(1);
        CountDownLatch releaseLock  = new CountDownLatch(1);

        Thread lockHolder = holdLockInBackground(lockAcquired, releaseLock);

        assertThat(lockAcquired.await(10, TimeUnit.SECONDS))
                .as("Background thread did not acquire the lock in time")
                .isTrue();

        // --- act: send async PATCH while the lock is held ----------------------
        String patchXml = """
                <stopPlaces xmlns="http://www.netex.org.uk/netex">
                    <StopPlace id="%s" version="%d">
                        <Name>Async Race Update</Name>
                        <StopPlaceType>railStation</StopPlaceType>
                    </StopPlace>
                </stopPlaces>
                """.formatted(netexId, saved.getVersion());

        // Submit the job — returns immediately with a job ID; actual processing happens
        // asynchronously on the server and will block trying to acquire the lock.
        ResponseEntity<StopPlaceJobDto> jobResponse = patchStopPlace(patchXml);
        assertThat(jobResponse.getBody()).isNotNull();
        Long jobId = jobResponse.getBody().jobId();

        // Hold the lock for longer than WAIT_FOR_LOCK_SECONDS (15 s) so the server-side
        // async worker times out waiting for the lock, then release.
        Thread.sleep((MutateLock.WAIT_FOR_LOCK_SECONDS + 5) * 1_000L);
        releaseLock.countDown();
        lockHolder.join(10_000);

        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.status())
                .as("Async write job should fail when it cannot acquire the mutate-lock")
                .isEqualTo(AsyncStopPlaceJobStatus.FAILED);
    }

    @Test
    public void whenAsyncWriteApiRacesGraphQL_theVersionOfTheStopPlaceCausedException() throws InterruptedException {
        StopPlace stopPlace = new StopPlace(
                new EmbeddableMultilingualString("Stockholm Central")
        );
        stopPlace.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
        StopPlace stopPlaceVersion1 = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
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

        // Async PATCH sends the original version (1), which is now stale — must hit a version check
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

    private StopPlaceJobDto awaitJobCompletion(Long jobId) throws InterruptedException {
        String jobUrl = WRITE_ENDPOINT + "/jobs/" + jobId;
        for (int i = 0; i < 60; i++) {
            ResponseEntity<StopPlaceJobDto> jobResponse =
                    restTemplate.getForEntity(jobUrl, StopPlaceJobDto.class);
            StopPlaceJobDto job = jobResponse.getBody();
            if (job != null && job.status() != AsyncStopPlaceJobStatus.PROCESSING) {
                return job;
            }
            Thread.sleep(500);
        }
        throw new AssertionError("Async job did not reach a terminal state within 30 s");
    }
}
