create table postal_address
(
    id                  bigint not null,
    netex_id            varchar(255),
    changed             timestamp(6),
    created             timestamp(6),
    from_date           timestamp(6),
    to_date             timestamp(6),
    version             bigint not null,
    address_line1_lang  varchar(255),
    address_line1_value varchar(255),
    post_code           varchar(255),
    town_lang           varchar(255),
    town_value          varchar(255),
    primary key (id)
);

alter table if exists stop_place add column postal_address_id bigint;
alter table if exists stop_place add constraint UK1g41j82qdnyk6egvqoq926xks unique (postal_address_id);

create sequence postal_address_seq start with 1 increment by 10;

alter table if exists stop_place add constraint FKgpdrrmxj51vq3cdv8qc4p9pwj foreign key (postal_address_id) references postal_address;
