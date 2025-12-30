create table site_facility_set_passenger_information_equipment_list (site_facility_set_id bigint not null, passenger_information_equipment_list varchar(255) check (passenger_information_equipment_list in ('FARE_INFORMATION','LINE_NETWORK_PLAN','LINE_TIMETABLE','INFORMATION_DESK','REAL_TIME_DEPARTURES','OTHER')));
create table site_facility_set_passenger_information_facility_list (site_facility_set_id bigint not null, passenger_information_facility_list varchar(255) check (passenger_information_facility_list in ('NEXT_STOP_INDICATOR','STOP_ANNOUNCEMENTS','PASSENGER_INFORMATION_DISPLAY','REAL_TIME_CONNECTIONS','OTHER')));

alter table if exists site_facility_set_passenger_information_equipment_list add constraint FKrfvlnjda1g1k0seui0vvknvd8 foreign key (site_facility_set_id) references site_facility_set;
alter table if exists site_facility_set_passenger_information_facility_list add constraint FK8xae706ubfbpv9xs39x5t20c3 foreign key (site_facility_set_id) references site_facility_set;

create table stop_place_facilities (stop_place_id bigint not null, facilities_id bigint not null, primary key (stop_place_id, facilities_id));
alter table if exists stop_place_facilities add constraint UK8amvssk5xncmap3l30mr8lmul unique (facilities_id);
alter table if exists stop_place_facilities add constraint FK8f4ajmjhjmf0gpabe90ypyg2v foreign key (facilities_id) references site_facility_set;
alter table if exists stop_place_facilities add constraint FK56fork9p7xykwv01yqar759a8 foreign key (stop_place_id) references stop_place;
