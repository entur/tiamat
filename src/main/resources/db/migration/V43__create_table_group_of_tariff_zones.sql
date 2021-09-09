CREATE TABLE group_of_tariff_zones
(
    id bigint NOT NULL,
    netex_id character varying(255),
    changed timestamp without time zone,
    created timestamp without time zone,
    from_date timestamp without time zone,
    to_date timestamp without time zone,
    version bigint NOT NULL,
    changed_by character varying(255),
    version_comment character varying(255),
    description_lang character varying(5),
    description_value character varying(4000),
    name_lang character varying(5),
    name_value character varying(255),
    private_code_type character varying(255),
    private_code_value character varying(255),
    purpose_of_grouping_ref character varying(255),
    purpose_of_grouping_ref_version character varying(255)
);

ALTER TABLE ONLY group_of_tariff_zones
    ADD CONSTRAINT group_of_tariff_zones_pkey PRIMARY KEY (id);

ALTER TABLE group_of_tariff_zones OWNER to tiamat;

CREATE SEQUENCE group_of_tariff_zones_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE group_of_tariff_zones_seq OWNER TO tiamat;

CREATE TABLE group_of_tariff_zones_key_values
(
    group_of_tariff_zones_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);

ALTER TABLE ONLY group_of_tariff_zones_key_values
    ADD CONSTRAINT group_of_tariff_zones_key_values_pkey PRIMARY KEY (group_of_tariff_zones_id, key_values_key),
    ADD CONSTRAINT uk_pfy12mpgyt1qevehecnwh5vq2 UNIQUE (key_values_id),
    ADD CONSTRAINT fkdxsb7togrx2wrcyi8wkyas0hn FOREIGN KEY (group_of_tariff_zones_id) REFERENCES group_of_tariff_zones(id),
    ADD CONSTRAINT fk3m6kr7mcvitox5adi5vqmuvpc FOREIGN KEY (key_values_id) REFERENCES value(id);

ALTER TABLE group_of_tariff_zones_key_values OWNER to tiamat;


CREATE TABLE group_of_tariff_zones_members
(
    group_of_tariff_zones_id bigint NOT NULL,
    ref varchar(255) NOT NULL ,
    version varchar(255)
);

ALTER TABLE ONLY group_of_tariff_zones_members
    ADD CONSTRAINT group_of_tariff_zones_members_pkey PRIMARY KEY (group_of_tariff_zones_id, ref),
    ADD CONSTRAINT fk7sff4g5u4o584lwkrc1hiqsfa FOREIGN KEY (group_of_tariff_zones_id)REFERENCES group_of_tariff_zones(id);

ALTER TABLE group_of_tariff_zones_members OWNER to tiamat;
