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

package org.rutebanken.tiamat.rest.validation;

/**
 * The writer throws this exception when an edit uses a version that is no longer current.
 * <p>
 * This exception differs from the other validation failures, because it tells the caller
 * something different. A rejected payload stays rejected, and the same bytes always give the
 * same result. A stale version means that the payload was correct, and that another client wrote
 * first. The caller must read the stop place again and reapply the edit.
 */
public class StaleVersionException extends RuntimeException {

    private final long currentVersion;

    public StaleVersionException(String netexId, long expectedVersion, long currentVersion) {
        super("Stop place " + netexId + " has moved to version " + currentVersion
                + " since version " + expectedVersion + " was read.");
        this.currentVersion = currentVersion;
    }

    public long getCurrentVersion() {
        return currentVersion;
    }
}
