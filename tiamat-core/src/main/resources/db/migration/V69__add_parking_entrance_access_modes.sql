-- Adds the {@code access_modes} column to {@code parking_entrance_for_vehicles}, storing
-- the NeTEx {@code AccessModes} value for AccessModeEnumeration.
--
-- AccessModes is an XML list-typed field (a single element containing space-separated
-- AccessModeEnumeration tokens, e.g. "foot bicycle"), so it is stored verbatim as a
-- single column rather than a nested collection table.

ALTER TABLE parking_entrance_for_vehicles
    ADD COLUMN access_modes varchar(128);
