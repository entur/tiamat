create table info_spot
(
    id                                bigint primary key,
    netex_id                          character varying(255),
    changed                           timestamp without time zone,
    created                           timestamp without time zone,
    from_date                         timestamp without time zone,
    to_date                           timestamp without time zone,
    version                           bigint not null,
    version_comment                   character varying(255),
    changed_by                        character varying(255),

    info_spot_type                    character varying(255),
    label                             character varying(255),
    name_value                        character varying(255),
    name_lang                         character varying(5),
    purpose                           character varying(255),
    poster_place_size                 character varying(255),
    description_value                 text,
    description_lang                  character varying(5),
    private_code_value                character varying(255),
    private_code_type                 character varying(255),
    backlight                         boolean,
    maintenance                       character varying(255),
    zone_label                        character varying(255),
    rail_information                  character varying(255),
    floor                             character varying(255),
    speech_property                   boolean,
    display_type                      character varying(255),
    info_spot_location                character varying(255),
    centroid                          public.geometry,
    polygon_id                        bigint
);

create table info_spot_location
(
    info_spot_id                      bigint not null,
    location_netex_id                 character varying(255),
    foreign key (info_spot_id) references info_spot (id) on delete cascade
);

create table info_spot_poster
(
    id                                bigint primary key,
    netex_id                          character varying(255),
    changed                           timestamp without time zone,
    created                           timestamp without time zone,
    from_date                         timestamp without time zone,
    to_date                           timestamp without time zone,
    version                           bigint not null,
    version_comment                   character varying(255),
    changed_by                        character varying(255),
    label                             character varying(255),
    poster_size                       character varying(255),
    lines                             character varying(255)
);

-- Future proofing when posters can be used multiple times
create table info_spot_poster_ref
(
    info_spot_id                       bigint,
    ref                                character varying(255),
    version                            character varying(255)
);

create table info_spot_key_values
(
    info_spot_id                      bigint not null,
    key_values_id                     bigint not null,
    key_values_key                    character varying(255) not null,
    foreign key (info_spot_id) references info_spot (id) on delete cascade
);

create table info_spot_poster_key_values
(
    info_spot_poster_id               bigint not null,
    key_values_id                     bigint not null,
    key_values_key                    character varying(255) not null,
    foreign key (info_spot_poster_id) references info_spot_poster (id) on delete cascade
);

CREATE SEQUENCE info_spot_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE info_spot_poster_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
