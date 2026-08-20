package org.rutebanken.tiamat.rest.write.async;

import org.rutebanken.tiamat.rest.write.JobService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Guarantees that every accepted write job reaches a terminal state.
 * <p>
 * A job can otherwise stay non terminal forever: its worker may have died mid write, or the
 * publish may have failed after the job row was written, in which case no transport ever saw it.
 * Either way the client polls a job that will never change.
 * <p>
 * Transport independent by design. Recovery must not depend on a broker redelivering, because a
 * lost message and a dead worker look the same to the client.
 * <p>
 * Several replicas may sweep at once; the update is conditional, so only one of them moves a
 * given job.
 */
@Component
@ConditionalOnProperty(name = "tiamat.write-api.enabled", havingValue = "true")
public class WriteJobTimeoutSweeper {

    private final JobService jobService;
    private final Duration timeout;

    public WriteJobTimeoutSweeper(
            JobService jobService,
            @Value("${tiamat.write-api.job-timeout:PT10M}") Duration timeout
    ) {
        this.jobService = jobService;
        this.timeout = timeout;
    }

    /**
     * The timeout also bounds how long a job's captured credentials remain usable, so it is a
     * security parameter as well as an operational one and should stay well below token lifetime.
     */
    @Scheduled(fixedDelayString = "${tiamat.write-api.job-timeout-sweep-interval:PT1M}")
    public void timeOutStaleJobs() {
        jobService.timeOutStaleJobs(timeout);
    }
}
