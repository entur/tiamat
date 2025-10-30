create table local_service (
    dtype varchar(31) not null,
    id bigint not null,
    netex_id varchar(255),
    changed timestamp(6),
    created timestamp(6),
    from_date timestamp(6),
    to_date timestamp(6),
    version bigint not null,
    out_of_service boolean,
    private_code_type varchar(255),
    private_code_value varchar(255),
    assistance_availability varchar(255) check (assistance_availability in ('NONE','AVAILABLE','AVAILABLE_IF_BOOKED','AVAILABLE_AT_CERTAIN_TIMES','UNKNOWN')),
    primary key (id)
);
create sequence local_service_seq start with 1 increment by 10;

create table assistance_service_assistance_facility_list(
    assistance_service_id bigint not null,
    assistance_facility_list varchar(255) check (assistance_facility_list in ('PERSONAL_ASSISTANCE','BOARDING_ASSISTANCE','WHEELCHAIR_ASSISTANCE','UNACCOMPANIED_MINOR_ASSISTANCE','WHEELCHAIR_USE','CONDUCTOR','INFORMATION','OTHER','NONE','ANY'))
);
alter table if exists assistance_service_assistance_facility_list add constraint FK7hv0arxglqauwoymrqq9q2t5a foreign key (assistance_service_id) references local_service;

create table quay_local_services (quay_id bigint not null, local_services_id bigint not null);
alter table if exists quay_local_services add constraint UK7cv023m3okd4cap0nx2o20sfd unique (local_services_id);
alter table if exists quay_local_services add constraint FKewjn7uc84bfofxj36v4yui01o foreign key (local_services_id) references local_service;
alter table if exists quay_local_services add constraint FKmqk9fbvr2au2vyf8f11j5p3b3 foreign key (quay_id) references quay;

create table stop_place_local_services (stop_place_id bigint not null, local_services_id bigint not null);
alter table if exists stop_place_local_services add constraint UKjgwwlstp97hn4ha7sikpm47tm unique (local_services_id);
alter table if exists stop_place_local_services add constraint FK4nx7kpggrn79fvjt3v93ua1wf foreign key (local_services_id) references local_service;
alter table if exists stop_place_local_services add constraint FK1yi1orrd8oehj0cx45ox66vke foreign key (stop_place_id) references stop_place;

create table access_space_local_services (access_space_id bigint not null, local_services_id bigint not null);
alter table if exists access_space_local_services add constraint UK8xu7mi958ih699b1okogaxywu unique (local_services_id);
alter table if exists access_space_local_services add constraint FKgmxdvbxe6xq70tkmieegmn5l2 foreign key (local_services_id) references local_service;
alter table if exists access_space_local_services add constraint FKobi1hfgnlp0o2ummdm1wuuxr9 foreign key (access_space_id) references access_space;

create table boarding_position_local_services (boarding_position_id bigint not null, local_services_id bigint not null);
alter table if exists boarding_position_local_services add constraint UKb2st1ogm9fb7tnhmdwv8hiqy2 unique (local_services_id);
alter table if exists boarding_position_local_services add constraint FKbqwf0w3vj7nhlqmg77jsrk4ha foreign key (local_services_id) references local_service;
alter table if exists boarding_position_local_services add constraint FKs5p5ifpjrrunp7tykrqvx6f6w foreign key (boarding_position_id) references boarding_position;

create table parking_local_services (parking_id bigint not null, local_services_id bigint not null);
alter table if exists parking_local_services add constraint UK4nju1a7uuil5qj0tf220sksm7 unique (local_services_id);
alter table if exists parking_local_services add constraint FKf8gfqoka3c2vpogxrpv2j64o3 foreign key (local_services_id) references local_service;
alter table if exists parking_local_services add constraint FK9cxsit3ne1ba1of1k5kmc33mv foreign key (parking_id) references parking;

create table parking_area_local_services (parking_area_id bigint not null, local_services_id bigint not null);
alter table if exists parking_area_local_services add constraint UK5mqxo1dllt1scoew8ypyxko9r unique (local_services_id);
alter table if exists parking_area_local_services add constraint FKm59clkd4lnfj397aenbv5kngo foreign key (local_services_id) references local_service;
alter table if exists parking_area_local_services add constraint FK86bpjpv5wg0klbgy37ocq70y8 foreign key (parking_area_id) references parking_area;



