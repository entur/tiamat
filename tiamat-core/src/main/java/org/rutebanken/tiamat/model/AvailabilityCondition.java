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
import jakarta.persistence.Embeddable;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Parking opening hours entry: one opening period of a NeTEx {@code AvailabilityCondition},
 * referencing a single {@code DayTypeRef} (by value, not owned — same pattern as
 * {@code TariffZoneRef}), with an {@code isAvailable} flag and an inline timeband. Owned
 * exclusively by {@link Parking}; not an {@code @Entity} — no independent identity, no
 * {@code netexId}/version.
 * <p>
 * Standard NeTEx permits several {@code DayTypeRef}s and several {@code Timeband}s per
 * {@code AvailabilityCondition}. Such a condition is semantically equivalent to one condition
 * per (day type, timeband) combination, so the importer expands it into that many instances of
 * this class rather than discarding the surplus. A given day type therefore legitimately
 * occurs more than once, which is what split opening hours (e.g. 06:00–10:00 and 15:00–20:00
 * on the same day) require.
 * <p>
 * {@code dayOffset} mirrors NeTEx's {@code Timeband/DayOffset} — "number of days after start
 * time that end time is". It is what distinguishes a period ending at the end of the day
 * ({@code 00:00} with {@code dayOffset} 1) from one ending at the very start of the same day
 * ({@code 00:00} with {@code dayOffset} 0); without it the two are indistinguishable once
 * persisted.
 */
@Embeddable
public class AvailabilityCondition {

    @Column(name = "day_type_ref", nullable = false, length = 128)
    private String dayTypeRef;

    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "day_offset", nullable = false)
    private int dayOffset;

    public AvailabilityCondition() {
    }

    public AvailabilityCondition(String dayTypeRef, boolean available, LocalTime startTime, LocalTime endTime) {
        this(dayTypeRef, available, startTime, endTime, 0);
    }

    public AvailabilityCondition(String dayTypeRef, boolean available, LocalTime startTime, LocalTime endTime, int dayOffset) {
        this.dayTypeRef = dayTypeRef;
        this.available = available;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOffset = dayOffset;
    }

    public String getDayTypeRef() {
        return dayTypeRef;
    }

    public void setDayTypeRef(String dayTypeRef) {
        this.dayTypeRef = dayTypeRef;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getDayOffset() {
        return dayOffset;
    }

    public void setDayOffset(int dayOffset) {
        this.dayOffset = dayOffset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AvailabilityCondition that)) return false;
        return available == that.available &&
                dayOffset == that.dayOffset &&
                Objects.equals(dayTypeRef, that.dayTypeRef) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayTypeRef, available, startTime, endTime, dayOffset);
    }

    @Override
    public String toString() {
        return "AvailabilityCondition{dayTypeRef='" + dayTypeRef + "', available=" + available +
                ", startTime=" + startTime + ", endTime=" + endTime + ", dayOffset=" + dayOffset + "}";
    }
}
