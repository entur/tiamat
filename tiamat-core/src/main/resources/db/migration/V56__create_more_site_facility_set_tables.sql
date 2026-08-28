create table site_facility_set_passenger_information_equipment_list (site_facility_set_id bigint not null, passenger_information_equipment_list varchar(255) check (passenger_information_equipment_list in ('FARE_INFORMATION','LINE_NETWORK_PLAN','LINE_TIMETABLE','INFORMATION_DESK','REAL_TIME_DEPARTURES','OTHER')));
create table site_facility_set_passenger_information_facility_list (site_facility_set_id bigint not null, passenger_information_facility_list varchar(255) check (passenger_information_facility_list in ('NEXT_STOP_INDICATOR','STOP_ANNOUNCEMENTS','PASSENGER_INFORMATION_DISPLAY','REAL_TIME_CONNECTIONS','OTHER')));

alter table if exists site_facility_set_passenger_information_equipment_list add constraint FKrfvlnjda1g1k0seui0vvknvd8 foreign key (site_facility_set_id) references site_facility_set;
alter table if exists site_facility_set_passenger_information_facility_list add constraint FK8xae706ubfbpv9xs39x5t20c3 foreign key (site_facility_set_id) references site_facility_set;

create table stop_place_facilities (stop_place_id bigint not null, facilities_id bigint not null, primary key (stop_place_id, facilities_id));
alter table if exists stop_place_facilities add constraint UK8amvssk5xncmap3l30mr8lmul unique (facilities_id);
alter table if exists stop_place_facilities add constraint FK8f4ajmjhjmf0gpabe90ypyg2v foreign key (facilities_id) references site_facility_set;
alter table if exists stop_place_facilities add constraint FK56fork9p7xykwv01yqar759a8 foreign key (stop_place_id) references stop_place;

create table parking_facilities (parking_id bigint not null, facilities_id bigint not null, primary key (parking_id, facilities_id));
alter table if exists parking_facilities add constraint UK6wgskiqaxnpkqa9cvswoiik2c unique (facilities_id);
alter table if exists parking_facilities add constraint FKsapbpcnurbh52skis8cnvkptu foreign key (facilities_id) references site_facility_set;
alter table if exists parking_facilities add constraint FKtcf010j21xstx0iai2m3bjdao foreign key (parking_id) references parking;

create table parking_area_facilities (parking_area_id bigint not null, facilities_id bigint not null, primary key (parking_area_id, facilities_id));
alter table if exists parking_area_facilities add constraint UK8jlkw5aijts7les04707f5dvl unique (facilities_id);
alter table if exists parking_area_facilities add constraint FKh6jiqf263hy3pkxgxurhitlf6 foreign key (facilities_id) references site_facility_set;
alter table if exists parking_area_facilities add constraint FKek4khn4vwtb1f5mhjbdhgpafc foreign key (parking_area_id) references parking_area;

create table access_space_facilities (access_space_id bigint not null, facilities_id bigint not null, primary key (access_space_id, facilities_id));
alter table if exists access_space_facilities add constraint UKsprgwh9c88jv6k8to1c3v9u6b unique (facilities_id);
alter table if exists access_space_facilities add constraint FK1d5dirqrlcvs423lwwi7bg7wj foreign key (facilities_id) references site_facility_set;
alter table if exists access_space_facilities add constraint FKk97x4haexkdj15mi3ngoxmnjm foreign key (access_space_id) references access_space;

create table boarding_position_facilities (boarding_position_id bigint not null, facilities_id bigint not null, primary key (boarding_position_id, facilities_id));
alter table if exists boarding_position_facilities add constraint UKaogq198n6imsagtiryyisab9t unique (facilities_id);
alter table if exists boarding_position_facilities add constraint FKllh6gk8do97lv0fyg0wlx6s7 foreign key (facilities_id) references site_facility_set;
alter table if exists boarding_position_facilities add constraint FKfg9p5ngiqwuw2s3voqbyyrxvl foreign key (boarding_position_id) references boarding_position;