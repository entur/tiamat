create or replace view group_of_stop_places_newest_version as
  select distinct on (netex_id) *
  from group_of_stop_places
  order by netex_id, version desc;

