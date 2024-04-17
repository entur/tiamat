create or replace view stop_place_newest_version as
  select distinct on (netex_id) *
  from stop_place
  order by netex_id, version desc;

