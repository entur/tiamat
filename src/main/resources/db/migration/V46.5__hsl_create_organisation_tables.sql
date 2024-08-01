create table contact (
    id                                 bigint primary key,
    netex_id                           character varying(255),

    contact_person                     character varying(255),
    email                              character varying(255),
    phone                              character varying(255),
    fax                                character varying(255),
    url                                character varying(255),
    further_details                    character varying(255)
);

create table organisation
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

    private_code                      character varying(255),
    company_number                    character varying(255),
    name                              character varying(255),
    organisation_type                 character varying(255),
    legal_name_lang                   varchar(5),
    legal_name_value                  varchar(255),
    contact_details_id                bigint,
    private_contact_details_id        bigint,
    FOREIGN KEY (contact_details_id) REFERENCES contact(id) ON DELETE CASCADE,
    FOREIGN KEY (private_contact_details_id) REFERENCES contact(id) ON DELETE CASCADE
);

ALTER TABLE ONLY organisation
    ADD CONSTRAINT organisation_netex_id_version_constraint UNIQUE (netex_id, version);

CREATE SEQUENCE contact_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE contact_seq OWNER TO tiamat;

CREATE SEQUENCE organisation_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE organisation_seq OWNER TO tiamat;

CREATE TABLE organisation_key_values (
    organisation_id bigint NOT NULL,
    key_values_id bigint NOT NULL UNIQUE,
    key_values_key character varying(255) NOT NULL,
    PRIMARY KEY (organisation_id, key_values_key),
    FOREIGN KEY (key_values_id) REFERENCES value(id),
    FOREIGN KEY (organisation_id) REFERENCES organisation(id) ON DELETE CASCADE
);
ALTER TABLE organisation_key_values OWNER TO tiamat;

-- HSL specific organisation relationships.
CREATE TABLE stop_place_organisations (
    stop_place_id bigint NOT NULL,
    organisation_ref character varying(255) NOT NULL,
    relationship_type character varying(255) NOT NULL,
    PRIMARY KEY (stop_place_id, organisation_ref, relationship_type),
    FOREIGN KEY (stop_place_id) REFERENCES stop_place(id) ON DELETE CASCADE
);
ALTER TABLE stop_place_organisations OWNER TO tiamat;
