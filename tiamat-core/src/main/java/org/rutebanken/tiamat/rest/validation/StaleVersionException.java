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
 * Thrown when an edit was based on a version that is no longer current.
 * <p>
 * Distinct from the other validation failures because it says something different to the caller.
 * A rejected payload stays rejected however many times it is sent. This one means the payload was
 * fine and somebody else got there first, so reading the stop place again and reapplying the edit
 * is the correct response rather than a retry of the same bytes.
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
