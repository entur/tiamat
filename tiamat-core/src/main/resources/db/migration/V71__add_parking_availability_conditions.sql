-- Persists Parking.availabilityConditions: the parking's opening hours, modelled
-- after NeTEx's AvailabilityCondition. dayTypeRef is a ref-only value (matching the
-- TariffZoneRef precedent), not an owned DayType entity.
--
-- A source AvailabilityCondition may carry several DayTypeRefs and several inline Timebands.
-- Each combination is stored as its own row, which is semantically equivalent and keeps the
-- import lossless. Consequently there is deliberately NO unique constraint on
-- (parking_id, day_type_ref): a single day type legitimately has several opening periods,
-- e.g. 06:00-10:00 and 15:00-20:00 on the same day.
--
-- day_offset mirrors NeTEx's Timeband/DayOffset ("number of days after start time that end
-- time is"). It is what distinguishes an opening period ending at the end of the day
-- (00:00 with day_offset 1) from one ending at the very start of the same day
-- (00:00 with day_offset 0).

CREATE TABLE parking_availability_conditions (
    parking_id    bigint       NOT NULL,
    day_type_ref  varchar(128) NOT NULL,
    is_available  boolean      NOT NULL DEFAULT TRUE,
    start_time    time,
    end_time      time,
    day_offset    smallint     NOT NULL DEFAULT 0
);

ALTER TABLE parking_availability_conditions
    ADD CONSTRAINT parking_availability_conditions_parking_fk
        FOREIGN KEY (parking_id) REFERENCES parking;

CREATE INDEX parking_availability_conditions_parking_id_index
    ON parking_availability_conditions (parking_id);
