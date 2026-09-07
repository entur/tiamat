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

package org.rutebanken.tiamat.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.Arrays;
import java.util.List;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "parking_entrance_for_vehicles_netex_id_version_constraint",
                        columnNames = {"netexId", "version"})}
)
public class ParkingEntranceForVehicles
        extends ParkingEntranceForVehicles__VersionStructure {

    /**
     * NeTEx {@code AccessModes} value, stored as the raw space-separated token list
     * exactly as it appears in the NeTEx XML (e.g. {@code "foot bicycle"}), since
     * {@code AccessModes} is an XML list-typed field (a single element containing
     * multiple {@code AccessModeEnumeration} tokens) rather than a repeatable element.
     * Use {@link #getAccessModesList()} / {@link #setAccessModesList(List)} for a
     * parsed {@code List<AccessModeEnumeration>} view.
     */
    @Column(name = "access_modes", length = 128)
    private String accessModes;

    public String getAccessModes() {
        return accessModes;
    }

    public void setAccessModes(String accessModes) {
        this.accessModes = accessModes;
    }

    /** Parsed view of {@link #getAccessModes()} as individual {@link AccessModeEnumeration} values. */
    public List<AccessModeEnumeration> getAccessModesList() {
        if (accessModes == null || accessModes.isBlank()) {
            return List.of();
        }
        return Arrays.stream(accessModes.trim().split("\\s+"))
                .map(AccessModeEnumeration::fromValue)
                .toList();
    }

    public void setAccessModesList(List<AccessModeEnumeration> values) {
        this.accessModes = (values == null || values.isEmpty())
                ? null
                : String.join(" ", values.stream().map(AccessModeEnumeration::value).toList());
    }

}
