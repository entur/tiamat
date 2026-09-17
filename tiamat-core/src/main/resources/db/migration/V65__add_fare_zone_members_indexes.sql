-- Membership lookup per stop place save (findValidExplicitStopsFareZones) and the explicit branch
-- of updateStopPlaceTariffZoneRef both filter on ref; the EAGER fareZoneMembers collection and the
-- same batch join load by fare_zone_id. The table carried only a foreign key until now.
CREATE INDEX idx_fare_zone_members_ref ON fare_zone_members (ref, fare_zone_id);
CREATE INDEX idx_fare_zone_members_fare_zone_id ON fare_zone_members (fare_zone_id);
