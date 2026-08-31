create sequence group_of_stop_places_seq start with 1 increment by 10;

create table group_of_stop_places_members (group_of_stop_places_id bigint not null, ref varchar(255), version varchar(255));

create table group_of_stop_places (id bigint not null, netex_id varchar(255), changed timestamp, created timestamp, from_date timestamp, to_date timestamp, version bigint not null, changed_by varchar(255), version_comment varchar(255), description_lang varchar(5), description_value varchar(4000), name_lang varchar(5), name_value varchar(255), private_code_type varchar(255), private_code_value varchar(255), short_name_lang varchar(5), short_name_value varchar(255), primary key (id));

create table group_of_stop_places_alternative_names (group_of_stop_places_id bigint not null, alternative_names_id bigint not null);

create table group_of_stop_places_key_values (group_of_stop_places_id bigint not null, key_values_id bigint not null, key_values_key varchar(255) not null, primary key (group_of_stop_places_id, key_values_key));


alter table group_of_stop_places_alternative_names add constraint group_of_stop_places_alternative_names_id_key unique (alternative_names_id);

alter table group_of_stop_places_key_values add constraint group_of_stop_places_key_values_id_key unique (key_values_id);

alter table group_of_stop_places_members add constraint group_of_stop_places_members_group_of_stop_places_id_fkey foreign key (group_of_stop_places_id) references group_of_stop_places;

alter table group_of_stop_places_alternative_names add constraint group_of_stop_places_alternative_names_alternative_names_id_fkey foreign key (alternative_names_id) references alternative_name;

alter table group_of_stop_places_alternative_names add constraint group_of_stop_places_alternative_names_group_of_stop_places_id_fkey foreign key (group_of_stop_places_id) references group_of_stop_places;

alter table group_of_stop_places_key_values add constraint group_of_stop_places_key_values_key_values_id_fkey foreign key (key_values_id) references value;

alter table group_of_stop_places_key_values add constraint group_of_stop_places_key_values_group_of_stop_places_id_fkey foreign key (group_of_stop_places_id) references group_of_stop_places;