package org.rutebanken.tiamat.ext.fintraffic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Embeddable value object for a single NeTEx {@code ParkingEntranceForVehicles} entry.
 * <p>
 * Persisted in the {@code parking_vehicle_entrances} collection table owned by
 * {@link FintrafficParking}.  Only the scalar fields that Abzu can display and edit
 * are stored; geometry (centroid) is omitted in this increment.
 */
@Embeddable
public class FintrafficParkingEntranceForVehicles {

    /** Human-readable label from the NeTEx {@code MultilingualString.value}. */
    @Column(name = "label", length = 255)
    private String label;

    /**
     * NeTEx {@code EntranceEnumeration} value string (e.g. {@code "door"}, {@code "gate"}).
     * Nullable because the attribute is optional in the NeTEx schema.
     */
    @Column(name = "entrance_type", length = 64)
    private String entranceType;

    @Column(name = "width", precision = 10, scale = 2)
    private BigDecimal width;

    @Column(name = "height", precision = 10, scale = 2)
    private BigDecimal height;

    @Column(name = "is_entry")
    private Boolean isEntry;

    @Column(name = "is_exit")
    private Boolean isExit;

    @Column(name = "public_code", length = 64)
    private String publicCode;

    public FintrafficParkingEntranceForVehicles() {
    }

    public FintrafficParkingEntranceForVehicles(String label, String entranceType, BigDecimal width,
                                                BigDecimal height, Boolean isEntry, Boolean isExit,
                                                String publicCode) {
        this.label = label;
        this.entranceType = entranceType;
        this.width = width;
        this.height = height;
        this.isEntry = isEntry;
        this.isExit = isExit;
        this.publicCode = publicCode;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getEntranceType() { return entranceType; }
    public void setEntranceType(String entranceType) { this.entranceType = entranceType; }

    public BigDecimal getWidth() { return width; }
    public void setWidth(BigDecimal width) { this.width = width; }

    public BigDecimal getHeight() { return height; }
    public void setHeight(BigDecimal height) { this.height = height; }

    public Boolean getIsEntry() { return isEntry; }
    public void setIsEntry(Boolean isEntry) { this.isEntry = isEntry; }

    public Boolean getIsExit() { return isExit; }
    public void setIsExit(Boolean isExit) { this.isExit = isExit; }

    public String getPublicCode() { return publicCode; }
    public void setPublicCode(String publicCode) { this.publicCode = publicCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FintrafficParkingEntranceForVehicles that)) return false;
        return Objects.equals(label, that.label) &&
               Objects.equals(entranceType, that.entranceType) &&
               Objects.equals(width, that.width) &&
               Objects.equals(height, that.height) &&
               Objects.equals(isEntry, that.isEntry) &&
               Objects.equals(isExit, that.isExit) &&
               Objects.equals(publicCode, that.publicCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, entranceType, width, height, isEntry, isExit, publicCode);
    }

    @Override
    public String toString() {
        return "FintrafficParkingEntranceForVehicles{" +
               "label='" + label + "', entranceType='" + entranceType + "', " +
               "width=" + width + ", height=" + height + ", " +
               "isEntry=" + isEntry + ", isExit=" + isExit + ", publicCode='" + publicCode + "'}";
    }
}
