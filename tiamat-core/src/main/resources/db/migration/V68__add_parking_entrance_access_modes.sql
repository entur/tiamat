-- Adds the {@code access_modes} column to {@code parking_entrance_for_vehicles}, storing
-- the NeTEx {@code AccessModes} value for AccessModeEnumeration (DPO-4826).
--
-- AccessModes is an XML list-typed field (a single element containing space-separated
-- AccessModeEnumeration tokens, e.g. "foot bicycle"), so it is stored verbatim as a
-- single column rather than a nested collection table, matching the shipped ext
-- V8__FintrafficParkingVehicleEntranceAccessModes.java.

ALTER TABLE parking_entrance_for_vehicles
    ADD COLUMN access_modes varchar(128);
