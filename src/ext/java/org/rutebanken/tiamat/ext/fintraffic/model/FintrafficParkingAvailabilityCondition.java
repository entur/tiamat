package org.rutebanken.tiamat.ext.fintraffic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalTime;
import java.util.Objects;

@Embeddable
public class FintrafficParkingAvailabilityCondition {

    @Column(name = "day_type_ref", nullable = false, length = 128)
    private String dayTypeRef;

    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    public FintrafficParkingAvailabilityCondition() {
    }

    public FintrafficParkingAvailabilityCondition(String dayTypeRef, boolean available, LocalTime startTime, LocalTime endTime) {
        this.dayTypeRef = dayTypeRef;
        this.available = available;
        this.startTime = startTime;
        this.endTime = endTime;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FintrafficParkingAvailabilityCondition that)) return false;
        return available == that.available &&
               Objects.equals(dayTypeRef, that.dayTypeRef) &&
               Objects.equals(startTime, that.startTime) &&
               Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayTypeRef, available, startTime, endTime);
    }
}
