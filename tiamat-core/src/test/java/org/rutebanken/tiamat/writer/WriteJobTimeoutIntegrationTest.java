package org.rutebanken.tiamat.writer;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.repository.AsyncStopPlaceJobRepository;
import org.rutebanken.tiamat.writer.async.WriteJobMessage;
import org.rutebanken.tiamat.writer.async.WriteJobNotOwnedException;
import org.rutebanken.tiamat.writer.async.WriteJobProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A job that never reaches a terminal state is invisible to the client, which polls forever. The
 * sweeper guarantees every accepted job ends up somewhere terminal.
 */
public class WriteJobTimeoutIntegrationTest extends TiamatIntegrationTest {

    private static final Duration TIMEOUT = Duration.ofMinutes(10);

    @Autowired
    private JobService jobService;

    @Autowired
    private AsyncStopPlaceJobRepository jobRepository;

    @Autowired
    private WriteJobProcessor processor;

    /**
     * TiamatIntegrationTest does not clear the job table, so rows left by other test classes would
     * otherwise be counted here, and the sweep in the rollback case takes everything non terminal.
     * These assertions are about counts, so the table has to start empty.
     */
    @org.junit.Before
    public void clearJobs() {
        jobRepository.deleteAll();
    }

    private static final byte[] CREATE_PAYLOAD = ("""
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="1">
                    <Name>Timed Out Stop</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """).getBytes(StandardCharsets.UTF_8);

    /**
     * The guarantee that makes TIMED_OUT mean "nothing was written": if the sweeper wins while a
     * write is in flight, the conditional completion matches nothing and the write rolls back with
     * it. Set up deterministically rather than raced.
     */
    @Test
    public void rollsBackTheWriteWhenTheJobIsTimedOutWhileWriting() {
        Long jobId = persist(AsyncStopPlaceJobStatus.PROCESSING, Instant.now(), null);
        assertThat(jobService.claim(jobId)).isTrue();

        // The sweeper gets there first, as it would if this worker had stalled.
        assertThat(jobService.timeOutStaleJobs(Duration.ofSeconds(-1))).isEqualTo(1);

        long stopPlacesBefore = stopPlaceRepository.count();

        assertThatThrownBy(() -> processor.process(WriteJobMessage.create(jobId, CREATE_PAYLOAD)))
                .isInstanceOf(WriteJobNotOwnedException.class);

        assertThat(stopPlaceRepository.count())
                .as("the write must roll back with the completion it could not perform")
                .isEqualTo(stopPlacesBefore);
        assertThat(statusOf(jobId)).isEqualTo(AsyncStopPlaceJobStatus.TIMED_OUT);
    }

    /**
     * A job whose publish failed was never claimed, so it ages from its creation time. Nothing
     * else would ever move it.
     */
    @Test
    public void timesOutAnAcceptedJobThatWasNeverClaimed() {
        Long jobId = persist(AsyncStopPlaceJobStatus.PROCESSING, Instant.now().minus(Duration.ofHours(1)), null);

        assertThat(jobService.timeOutStaleJobs(TIMEOUT)).isEqualTo(1);
        assertThat(statusOf(jobId)).isEqualTo(AsyncStopPlaceJobStatus.TIMED_OUT);
    }

    /**
     * A claimed job ages from when it was claimed, so a worker that died mid write is recovered.
     */
    @Test
    public void timesOutAClaimedJobWhoseWorkerDisappeared() {
        Long jobId = persist(AsyncStopPlaceJobStatus.IN_PROGRESS, Instant.now().minus(Duration.ofHours(2)),
                Instant.now().minus(Duration.ofHours(1)));

        assertThat(jobService.timeOutStaleJobs(TIMEOUT)).isEqualTo(1);
        assertThat(statusOf(jobId)).isEqualTo(AsyncStopPlaceJobStatus.TIMED_OUT);
    }

    @Test
    public void leavesRecentJobsAlone() {
        Long accepted = persist(AsyncStopPlaceJobStatus.PROCESSING, Instant.now(), null);
        Long claimed = persist(AsyncStopPlaceJobStatus.IN_PROGRESS, Instant.now(), Instant.now());

        assertThat(jobService.timeOutStaleJobs(TIMEOUT)).isZero();
        assertThat(statusOf(accepted)).isEqualTo(AsyncStopPlaceJobStatus.PROCESSING);
        assertThat(statusOf(claimed)).isEqualTo(AsyncStopPlaceJobStatus.IN_PROGRESS);
    }

    /**
     * A claimed job ages from its claim, not its creation, so a long queued job that has only just
     * started being processed must not be taken away from its worker.
     */
    @Test
    public void leavesAnOldJobAloneWhileItIsBeingProcessed() {
        Long jobId = persist(AsyncStopPlaceJobStatus.IN_PROGRESS, Instant.now().minus(Duration.ofHours(5)),
                Instant.now());

        assertThat(jobService.timeOutStaleJobs(TIMEOUT)).isZero();
        assertThat(statusOf(jobId)).isEqualTo(AsyncStopPlaceJobStatus.IN_PROGRESS);
    }

    @Test
    public void leavesTerminalJobsAlone() {
        Instant longAgo = Instant.now().minus(Duration.ofDays(1));
        Long finished = persist(AsyncStopPlaceJobStatus.FINISHED, longAgo, longAgo);
        Long failed = persist(AsyncStopPlaceJobStatus.FAILED, longAgo, longAgo);

        assertThat(jobService.timeOutStaleJobs(TIMEOUT)).isZero();
        assertThat(statusOf(finished)).isEqualTo(AsyncStopPlaceJobStatus.FINISHED);
        assertThat(statusOf(failed)).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
    }

    private Long persist(AsyncStopPlaceJobStatus status, Instant createdAt, Instant claimedAt) {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        job.setStatus(status);
        job.setCreatedAt(createdAt);
        job.setClaimedAt(claimedAt);
        return jobRepository.save(job).getId();
    }

    private AsyncStopPlaceJobStatus statusOf(Long jobId) {
        return jobRepository.findById(jobId).orElseThrow().getStatus();
    }
}
