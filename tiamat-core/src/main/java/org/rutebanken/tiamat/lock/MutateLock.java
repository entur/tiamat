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

import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Runs one mutation at a time across all Tiamat instances.
 * <p>
 * The lock is held until the surrounding transaction commits, so the next mutation reads the
 * committed result of the previous one.
 */
@Component
public class MutateLock {

    public static final int WAIT_FOR_LOCK_SECONDS = 15;

    public static final String LOCK_NAME = "mutate-lock";

    private final AdvisoryLock advisoryLock;

    public MutateLock(AdvisoryLock advisoryLock) {
        this.advisoryLock = advisoryLock;
    }

    public <T> T executeInLock(Supplier<T> supplier) {
        return advisoryLock.executeInTransactionLock(LOCK_NAME, WAIT_FOR_LOCK_SECONDS, supplier);
    }

}
