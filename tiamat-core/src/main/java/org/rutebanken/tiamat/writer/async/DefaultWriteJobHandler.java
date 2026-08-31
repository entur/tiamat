package org.rutebanken.tiamat.writer.async;

import org.rutebanken.tiamat.writer.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Processes a write job, whichever transport delivered it.
 * <p>
 * Transport agnostic on purpose. A transport implementation should be a thin shell that hands the
 * message over; keeping the claim, the principal, the write and the job outcome in one place means
 * a second transport cannot drift from the first, and in particular cannot reproduce a subtly
 * different version of the correctness or security handling.
 * <p>
 * Deliberately not transactional. The write and its completion are one transaction, owned by
 * {@link WriteJobProcessor}, and both the failure and the timeout have to be recorded after that
 * transaction has rolled back rather than inside it.
 */
@Component
public class DefaultWriteJobHandler implements WriteJobHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultWriteJobHandler.class);

    private final JobService jobService;
    private final WriteJobProcessor processor;
    private final WriteJobPrincipal principal;

    public DefaultWriteJobHandler(
            JobService jobService,
            WriteJobProcessor processor,
            WriteJobPrincipal principal
    ) {
        this.jobService = jobService;
        this.processor = processor;
        this.principal = principal;
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
            // The write is authorized and attributed where it happens, not where it was accepted.
            principal.restore(jobService.principalClaimsFor(message.jobId()));
            processor.process(message);
        } catch (WriteJobCredentialsExpiredException e) {
            // Nothing was written and the payload is not at fault, so this is a timeout rather
            // than a failure: resubmitting with a fresh token will succeed.
            logger.warn("Write job {} outlived the credentials that submitted it", message.jobId(), e);
            jobService.timeOut(message.jobId(), e.getMessage());
        } catch (Exception e) {
            // The write has rolled back by the time we get here, so the job records why in a
            // transaction of its own.
            logger.error("Write job {} failed", message.jobId(), e);
            jobService.fail(message.jobId(), e);
        } finally {
            // Worker threads are pooled. A principal left installed would be inherited by whichever
            // job ran next on this thread.
            principal.clear();
        }
    }
}
