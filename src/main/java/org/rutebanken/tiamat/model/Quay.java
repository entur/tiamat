package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "stop_place_netex_id_version_constraint", columnNames = {"netexId", "version"})}
)
public class Quay extends StopPlaceSpace_VersionStructure {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<BoardingPosition> boardingPositions = new ArrayList<>();
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
