CREATE TABLE quay_facilities
(
    quay_id       BIGINT NOT NULL,
    facilities_id BIGINT NOT NULL
);

CREATE TABLE site_facility_set
(
    id        BIGINT NOT NULL,
    netex_id  VARCHAR(255),
    changed   TIMESTAMP(6),
    created   TIMESTAMP(6),
    from_date TIMESTAMP(6),
    to_date   TIMESTAMP(6),
    version   BIGINT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE site_facility_set_mobility_facility_list
(
    site_facility_set_id   BIGINT NOT NULL,
    mobility_facility_list VARCHAR(255) CHECK (mobility_facility_list IN
                                               ('UNKNOWN', 'LOW_FLOOR', 'STEP_FREE_ACCESS', 'SUITABLE_FOR_PUSHCHAIRS',
                                                'SUITABLE_FOR_WHEELCHAIRS', 'SUITABLE_FOR_HEAVILIY_DISABLED',
                                                'BOARDING_ASSISTANCE', 'ONBOARD_ASSISTANCE',
                                                'UNACCOMPANIED_MINOR_ASSISTANCE', 'TACTILE_PLATFORM_EDGES',
                                                'TACTILE_GUIDING_STRIPS'))
);

ALTER TABLE IF EXISTS quay_facilities
    ADD CONSTRAINT UKd8uwnc0n3skq2iakffr2xhepp UNIQUE (facilities_id);

CREATE SEQUENCE site_facility_set_seq START WITH 1 INCREMENT BY 10;

ALTER TABLE IF EXISTS quay_facilities
    ADD CONSTRAINT FKrkqwvf0rahut1fa24ycj8isj6 FOREIGN KEY (facilities_id) REFERENCES site_facility_set;

ALTER TABLE IF EXISTS quay_facilities
    ADD CONSTRAINT FK2el6bhe6t23t4rsc1fxufabve FOREIGN KEY (quay_id) REFERENCES quay;

ALTER TABLE IF EXISTS site_facility_set_mobility_facility_list
    ADD CONSTRAINT FK8l5n4q0mblee24hjic9xlenm3 FOREIGN KEY (site_facility_set_id) REFERENCES site_facility_set;
