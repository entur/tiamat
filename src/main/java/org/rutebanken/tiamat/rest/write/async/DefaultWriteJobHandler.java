package org.rutebanken.tiamat.rest.write.async;

import org.rutebanken.tiamat.rest.write.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Processes a write job, whichever transport delivered it.
 * <p>
 * Transport agnostic on purpose. A transport implementation should be a thin shell that hands the
 * message over; keeping the claim, the write and the job outcome in one place means a second
 * transport cannot drift from the first, and in particular cannot reproduce a subtly different
 * version of the correctness or security handling.
 * <p>
 * Deliberately not transactional. The write and its completion are one transaction, owned by
 * {@link WriteJobProcessor}, and the failure has to be recorded after that transaction has rolled
 * back rather than inside it.
 */
@Component
public class DefaultWriteJobHandler implements WriteJobHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultWriteJobHandler.class);

    private final JobService jobService;
    private final WriteJobProcessor processor;

    public DefaultWriteJobHandler(JobService jobService, WriteJobProcessor processor) {
        this.jobService = jobService;
        this.processor = processor;
    }

    @Override
    public void handle(WriteJobMessage message) {
        if (!jobService.claim(message.jobId())) {
            // Already claimed by another delivery, or already terminal. Doing the work anyway
            // would duplicate it: a create mints a fresh NSR id on every run.
            logger.debug("Job {} could not be claimed, discarding delivery", message.jobId());
            return;
        }

        try {
            processor.process(message);
        } catch (Exception e) {
            // The write has rolled back by the time we get here, so the job records why in a
            // transaction of its own.
            logger.error("Write job {} failed", message.jobId(), e);
            jobService.fail(message.jobId(), e);
        }
    }
}
