alter table public.stop_place add column modification_enumeration character varying(255);
CREATE INDEX stop_modification_enumeration_index ON public.stop_place USING btree (modification_enumeration);


