ALTER TABLE group_of_stop_places ADD COLUMN purpose_of_grouping_id bigint;
CREATE TABLE purpose_of_grouping (
                                     id bigint not null,
                                     netex_id varchar(255),
                                     changed timestamp,
                                     created timestamp,
                                     from_date timestamp,
                                     to_date timestamp,
                                     version int8 not null,
                                     changed_by varchar(255),
                                     version_comment varchar(255),
                                     description_lang varchar(5),
                                     description_value varchar(4000),
                                     name_lang varchar(5),
                                     name_value varchar(255),
                                     primary key (id)
);
CREATE TABLE purpose_of_grouping_key_values (
                                                purpose_of_grouping_id bigint not null,
                                                key_values_id bigint not null,
                                                key_values_key varchar(255) not null,
                                                primary key (
                                                             purpose_of_grouping_id, key_values_key
                                                    )
);

CREATE SEQUENCE purpose_of_grouping_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE purpose_of_grouping_seq OWNER TO tiamat;
ALTER TABLE purpose_of_grouping OWNER TO tiamat;
ALTER TABLE purpose_of_grouping_key_values OWNER TO tiamat;

CREATE INDEX purpose_of_grouping_name_value_index ON purpose_of_grouping (name_value);
ALTER TABLE purpose_of_grouping ADD CONSTRAINT purpose_of_grouping_netex_id_version_constraint UNIQUE (netex_id);
ALTER TABLE purpose_of_grouping ADD CONSTRAINT purpose_of_grouping_name_value_constraint UNIQUE (name_value);
ALTER TABLE purpose_of_grouping_key_values ADD CONSTRAINT purpose_of_grouping_key_values_unique_key UNIQUE (key_values_id);





