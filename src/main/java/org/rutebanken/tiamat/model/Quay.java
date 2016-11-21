package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Quay extends StopPlaceSpace_VersionStructure {

    protected String plateCode;

    protected Float compassBearing;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<BoardingPosition> boardingPositions = new ArrayList<>();

    public Quay(EmbeddableMultilingualString name) {
        super(name);
    }

    public Quay() {
    }

    public String getPlateCode() {
        return plateCode;
    }

    public void setPlateCode(String value) {
        this.plateCode = value;
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
        if(this == object) {
            return true;
        } else if (!(object instanceof Quay)) {
            return false;
        }

        Quay other = (Quay) object;

        return Objects.equals(this.name, other.name)
                && Objects.equals(this.centroid, other.centroid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, centroid);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", id)
                .add("name", name)
                .add("centroid", centroid)
                .add("keyValues", getKeyValues())
                .toString();
    }
}
