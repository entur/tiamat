package org.rutebanken.tiamat.rest.write.async;

/**
 * Hands an accepted write job to a transport. This is the only interface a transport implements;
 * the processing itself belongs to {@link WriteJobHandler} and is the same whichever transport is
 * in use.
 * <p>
 * Called on the request thread, after the job row exists and before the response is returned, so
 * it must not do the payload's work.
 */
public interface WriteJobPublisher {

    /**
     * @throws WriteJobRejectedException if the transport cannot accept the job, for instance
     *                                   because its queue is full. The caller answers 503.
     */
    void publish(WriteJobMessage message);
}
