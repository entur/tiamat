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

    /**
     * NeTEx {@code AccessModes} value, stored as the raw space-separated token list
     * exactly as it appears in the NeTEx XML (e.g. {@code "foot bicycle"}), since
     * {@code AccessModes} is an XML list-typed field (single element, multiple tokens)
     * rather than a repeatable element. Use {@link #getAccessModesList()} /
     * {@link #setAccessModesList(java.util.List)} for a parsed {@code List<String>} view.
     */
    @Column(name = "access_modes", length = 128)
    private String accessModes;

    public FintrafficParkingEntranceForVehicles() {
    }

    public FintrafficParkingEntranceForVehicles(String label, String entranceType, BigDecimal width,
                                                BigDecimal height, Boolean isEntry, Boolean isExit,
                                                String publicCode) {
        this(label, entranceType, width, height, isEntry, isExit, publicCode, null);
    }

    public FintrafficParkingEntranceForVehicles(String label, String entranceType, BigDecimal width,
                                                BigDecimal height, Boolean isEntry, Boolean isExit,
                                                String publicCode, String accessModes) {
        this.label = label;
        this.entranceType = entranceType;
        this.width = width;
        this.height = height;
        this.isEntry = isEntry;
        this.isExit = isExit;
        this.publicCode = publicCode;
        this.accessModes = accessModes;
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

    public String getAccessModes() { return accessModes; }
    public void setAccessModes(String accessModes) { this.accessModes = accessModes; }

    /** Parsed view of {@link #getAccessModes()} as individual NeTEx {@code AccessModeEnumeration} value strings. */
    public java.util.List<String> getAccessModesList() {
        if (accessModes == null || accessModes.isBlank()) {
            return java.util.List.of();
        }
        return java.util.Arrays.stream(accessModes.trim().split("\\s+")).toList();
    }

    public void setAccessModesList(java.util.List<String> values) {
        this.accessModes = (values == null || values.isEmpty()) ? null : String.join(" ", values);
    }

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
               Objects.equals(publicCode, that.publicCode) &&
               Objects.equals(accessModes, that.accessModes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, entranceType, width, height, isEntry, isExit, publicCode, accessModes);
    }

    @Override
    public String toString() {
        return "FintrafficParkingEntranceForVehicles{" +
               "label='" + label + "', entranceType='" + entranceType + "', " +
               "width=" + width + ", height=" + height + ", " +
               "isEntry=" + isEntry + ", isExit=" + isExit + ", publicCode='" + publicCode + "', " +
               "accessModes='" + accessModes + "'}";
    }
}
