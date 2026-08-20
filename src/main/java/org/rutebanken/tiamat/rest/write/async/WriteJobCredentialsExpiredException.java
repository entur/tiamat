package org.rutebanken.tiamat.rest.write.async;

import java.time.Instant;

/**
 * The caller's credentials expired before their write could be applied.
 * <p>
 * Nothing was written, and resubmitting with a fresh token will succeed, so this is reported as a
 * timeout rather than a failure: the same payload is not at fault. If it starts happening the
 * queue is running slower than token lifetime, which is a capacity signal rather than a client
 * problem.
 */
public class WriteJobCredentialsExpiredException extends RuntimeException {

    public WriteJobCredentialsExpiredException(Instant expiredAt) {
        super("Credentials for this write expired at " + expiredAt + ", before it could be applied.");
    }
}
