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

package org.rutebanken.tiamat.netex.id;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Configuration for valid NeTEx ID numeric ranges per entity type.
 * Configured in application.properties as a map:
 * <pre>
 * netex.id.range.StopPlace.min=10000
 * netex.id.range.StopPlace.max=20000
 * netex.id.range.Quay.min=50000
 * netex.id.range.Quay.max=60000
 * </pre>
 */
@Configuration
@ConfigurationProperties(prefix = "netex.id")
public class NetexIdRangeConfiguration {

    private Map<String, IdRange> range = new HashMap<>();

    public Map<String, IdRange> getRange() {
        return range;
    }

    public void setRange(Map<String, IdRange> range) {
        this.range = range;
    }

    /**
     * Get the configured ID range for the given entity type name, if any.
     *
     * @param entityTypeName e.g. "StopPlace", "Quay"
     * @return Optional containing the range, or empty if no range is configured for this type
     */
    public Optional<IdRange> getRangeForEntity(String entityTypeName) {
        return Optional.ofNullable(range.get(entityTypeName));
    }

    /**
     * Check if the given numeric ID is within the configured range for the entity type.
     * If no range is configured for the entity type, the ID is considered valid.
     *
     * @param entityTypeName e.g. "StopPlace", "Quay"
     * @param numericId      the numeric part of the NeTEx ID
     * @return true if valid (in range or no range configured)
     */
    public boolean isIdInRange(String entityTypeName, long numericId) {
        return getRangeForEntity(entityTypeName)
                .map(r -> r.isInRange(numericId))
                .orElse(true);
    }

    /**
     * Check if the given ID postfix (as string) is a valid numeric ID within the configured range for the entity type.
     * If the postfix is not a valid number, it is considered out of range.
     * If no range is configured for the entity type, all IDs are considered valid.
     *
     * @param entityTypeName e.g. "StopPlace", "Quay"
     * @param idPostfix      the numeric part of the NeTEx ID as string
     * @return true if valid (in range or no range configured), false if not a valid number or out of range
     */
    public boolean isIdInRange(String entityTypeName, String idPostfix) {
        try {
            boolean hasRange = getRangeForEntity(entityTypeName).isPresent();
            if (!hasRange) {
                // If no range is configured for this entity type, we consider all IDs valid.
                return true;
            }
            long numericId = Long.parseLong(idPostfix);
            return isIdInRange(entityTypeName, numericId);
        } catch (NumberFormatException e) {
            // If the postfix is not a valid number, we consider it out of range.
            return false;
        }
    }

    public static class IdRange {
        private long min;
        private long max;

        public IdRange() {
        }

        public IdRange(long min, long max) {
            this.min = min;
            this.max = max;
            if (min >= max) {
                throw new IllegalArgumentException("IdRange min must be less than max");
            }
        }

        public long getMin() {
            return min;
        }

        public void setMin(long min) {
            this.min = min;
        }

        public long getMax() {
            return max;
        }

        public void setMax(long max) {
            this.max = max;
        }

        public boolean isInRange(long numericId) {
            return numericId >= min && numericId <= max;
        }

        @Override
        public String toString() {
            return "[" + min + ", " + max + "]";
        }
    }
}
