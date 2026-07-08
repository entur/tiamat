CREATE TABLE fare_zone (
                             id bigint NOT NULL,
                             netex_id character varying(255),
                             changed timestamp without time zone,
                             created timestamp without time zone,
                             from_date timestamp without time zone,
                             to_date timestamp without time zone,
                             version bigint NOT NULL,
                             version_comment character varying(255),
                             description_lang character varying(5),
                             description_value character varying(4000),
                             name_lang character varying(5),
                             name_value character varying(255),
                             private_code_type character varying(255),
                             private_code_value character varying(255),
                             short_name_lang character varying(5),
                             short_name_value character varying(255),
                             centroid geometry,
                             polygon_id bigint,
                             changed_by character varying(255),
                             scoping_method character varying(255),
                             transport_organisation_ref character varying(255),
                             zone_topology character varying(255)


);


ALTER TABLE ONLY fare_zone
    ADD CONSTRAINT fare_zone_pkey PRIMARY KEY (id),
    ADD CONSTRAINT fkqqohh30c6mjxumolbl34epvuv FOREIGN KEY (polygon_id) REFERENCES persistable_polygon(id);


ALTER TABLE fare_zone OWNER TO tiamat;

CREATE SEQUENCE fare_zone_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE fare_zone_seq OWNER TO tiamat;

--
-- Name: tariff_zone_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE fare_zone_key_values (
                                        fare_zone_id bigint NOT NULL,
                                        key_values_id bigint NOT NULL,
                                        key_values_key character varying(255) NOT NULL
);

ALTER TABLE ONLY fare_zone_key_values
    ADD CONSTRAINT fare_zone_key_values_pkey PRIMARY KEY (fare_zone_id, key_values_key),
    ADD CONSTRAINT uk_oujfy4iyi1cf3fdquir6jsn0n UNIQUE (key_values_id),
    ADD CONSTRAINT fk9vxr65yfa7q8wuw48dquyha9y FOREIGN KEY (fare_zone_id) REFERENCES fare_zone(id),
    ADD CONSTRAINT fko1fg9cqlm88y8sn6a4wl4oawx FOREIGN KEY (key_values_id) REFERENCES value(id);

ALTER TABLE fare_zone_key_values OWNER TO tiamat;

CREATE TABLE fare_zone_neighbours (
    fare_zone_id bigint NOT NULL,
    neighbours_id bigint NOT NULL
);

ALTER TABLE ONLY fare_zone_neighbours
    ADD CONSTRAINT fare_zone_neighbours_pkey PRIMARY KEY (fare_zone_id, neighbours_id),
    ADD CONSTRAINT uk_pxn4x40anf9xsrnm60vpakv8x UNIQUE (neighbours_id),
    ADD CONSTRAINT fk500i1nr22alco4hvoqmtdsy9g FOREIGN KEY (neighbours_id) REFERENCES tariff_zone_ref(id),
    ADD CONSTRAINT fklwjm9qu9fghulu2k425gl996x FOREIGN KEY (fare_zone_id)REFERENCES fare_zone(id);

ALTER TABLE fare_zone_neighbours OWNER to tiamat;

CREATE TABLE fare_zone_members(
    fare_zone_id bigint NOT NULL,
    ref character varying(255),
    version character varying(255)
);

ALTER TABLE ONLY fare_zone_members
    ADD CONSTRAINT fk5pcf1db54ci2k7cnbcam6b2ye FOREIGN KEY (fare_zone_id) REFERENCES fare_zone (id);

ALTER TABLE fare_zone_members OWNER to tiamat;