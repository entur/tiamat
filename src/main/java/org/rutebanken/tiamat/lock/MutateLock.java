/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Prevent too long running mutations.
 * Avoid parallel mutations.
 */
@Component
public class MutateLock {

    public static final int WAIT_FOR_LOCK_SECONDS = 15;


    public static final int LOCK_MAX_LEASE_TIME_SECONDS = 60;

    private static final Logger logger = LoggerFactory.getLogger(MutateLock.class);

    private final int waitTimeoutSeconds;
    public static final String LOCK_NAME = "mutate-lock";
    private final TimeoutMaxLeaseTimeLock timoutMaxLeaseTimeLock;


    @Autowired
    public MutateLock(TimeoutMaxLeaseTimeLock timeoutMaxLeaseTimeLock) {
        this.timoutMaxLeaseTimeLock = timeoutMaxLeaseTimeLock;
        this.waitTimeoutSeconds = WAIT_FOR_LOCK_SECONDS;

    }

    public <T> T executeInLock(Supplier<T> supplier) {
        return timoutMaxLeaseTimeLock.executeInLock(supplier, LOCK_NAME, waitTimeoutSeconds, LOCK_MAX_LEASE_TIME_SECONDS);
    }

}
