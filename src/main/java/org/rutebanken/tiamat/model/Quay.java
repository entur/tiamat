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

import com.google.common.base.MoreObjects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "quay_netex_id_version_constraint", columnNames = {"netexId", "version"})}
)
public class Quay extends StopPlaceSpace_VersionStructure {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<BoardingPosition> boardingPositions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SiteFacilitySet> facilities = new HashSet<>();

    protected String publicCode;

    /**
     * TODO: reconsider data type for compass bearing.
     * https://rutebanken.atlassian.net/browse/NRP-895
     */
    protected Float compassBearing;

    public Quay(EmbeddableMultilingualString name) {
        super(name);
    }

    public Quay() {
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public Float getCompassBearing() {
        return compassBearing;
    }

    public void setCompassBearing(Float value) {
        this.compassBearing = value;
    }

    public List<BoardingPosition> getBoardingPositions() {
        return boardingPositions;
    }

    public Set<SiteFacilitySet> getFacilities() {
        return facilities;
    }

    public void setFacilities(Set<SiteFacilitySet> facilities) {
       this.facilities = facilities;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof Quay)) {
            return false;
        }

        Quay other = (Quay) object;

        return Objects.equals(this.name, other.name)
                && Objects.equals(this.centroid, other.centroid)
                && Objects.equals(this.compassBearing, other.compassBearing)
                && Objects.equals(this.publicCode, other.publicCode)
                && getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).containsAll(other.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, centroid,
                compassBearing, publicCode,
                getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", id)
                .add("netexId", netexId)
                .add("version", version)
                .add("name", name)
                .add("centroid", centroid)
                .add("bearing", compassBearing)
                .add("publicCode", publicCode)
                .add("keyValues", getKeyValues())
                .toString();
    }
}
