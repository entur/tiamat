ALTER TABLE stop_place_tariff_zones ADD COLUMN purpose_of_grouping_ref varchar(255);
ALTER TABLE stop_place_tariff_zones ADD COLUMN purpose_of_grouping_ref_version varchar(255);
Hibernate: alter table group_of_stop_places add column purpose_of_grouping_id int8
    /*
    Hibernate: create table purpose_of_grouping (id int8 not null, netex_id varchar(255), changed timestamp, created timestamp, from_date timestamp, to_date timestamp, version int8 not null, changed_by varchar(255), version_comment varchar(255), primary key (id))
    Hibernate: create table purpose_of_grouping_key_values (purpose_of_grouping_id int8 not null, key_values_id int8 not null, key_values_key varchar(255) not null, primary key (purpose_of_grouping_id, key_values_key))
    Hibernate: alter table group_of_stop_places_alternative_names drop constraint UK_8fntgjff3lat9y850c17pwkwv
    Hibernate: alter table group_of_stop_places_alternative_names add constraint UK_8fntgjff3lat9y850c17pwkwv unique (alternative_names_id)
    Hibernate: alter table group_of_stop_places_key_values drop constraint UK_hnagmxgtptqid1jyv5ri75vxd
    Hibernate: alter table group_of_stop_places_key_values add constraint UK_hnagmxgtptqid1jyv5ri75vxd unique (key_values_id)
    Hibernate: alter table purpose_of_grouping add column description_lang varchar(5)
   Hibernate: alter table purpose_of_grouping add column description_value varchar(4000)
   Hibernate: alter table purpose_of_grouping add column name_lang varchar(5)
   Hibernate: alter table purpose_of_grouping add column name_value varchar(255)

     */




