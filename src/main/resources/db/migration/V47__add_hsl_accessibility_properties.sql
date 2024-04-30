-- HSL specific accessibility and measurement fields.

CREATE TABLE hsl_accessibility_properties
(
    id                                bigint NOT NULL,
    netex_id                          character varying(255),
    changed                           timestamp without time zone,
    created                           timestamp without time zone,
    from_date                         timestamp without time zone,
    to_date                           timestamp without time zone,
    version                           bigint NOT NULL,
    version_comment                   character varying(255),
    changed_by                        character varying(255),

    stop_area_side_slope              numeric(5, 2), -- (%)
    stop_area_lengthwise_slope        numeric(5, 2), -- (%)
    end_ramp_slope                    numeric(5, 2), -- (%)
    shelter_lane_distance             numeric(7, 2), -- (cm)
    curb_back_of_rail_distance        numeric(7, 2), -- (cm)
    curb_drive_side_of_rail_distance  numeric(7, 2), -- (cm)
    structure_lane_distance           numeric(7, 2), -- (cm)
    stop_elevation_from_rail_top      numeric(7, 2), -- (cm)
    stop_elevation_from_sidewalk      numeric(7, 2), -- (cm)
    lower_cleat_height                numeric(7, 2), -- (cm)
    service_area_width                numeric(7, 2), -- (cm)
    service_area_length               numeric(7, 2), -- (cm)
    platform_edge_warning_area        boolean,
    guidance_tiles                    boolean,
    guidance_stripe                   boolean,
    service_area_stripes              boolean,
    sidewalk_accessible_connection    boolean,
    stop_area_surroundings_accessible boolean,
    curved_stop                       boolean
);

ALTER TABLE ONLY hsl_accessibility_properties
    ADD CONSTRAINT hsl_accessibility_properties_pkey PRIMARY KEY (id);

CREATE SEQUENCE hsl_accessibility_properties_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE hsl_accessibility_properties_seq OWNER TO tiamat;

ALTER TABLE ONLY accessibility_assessment
    ADD COLUMN hsl_accessibility_properties_id bigint;
ALTER TABLE ONLY accessibility_assessment
    ADD CONSTRAINT fk_accessibility_assessment_hsl_accessibility_properties FOREIGN KEY (hsl_accessibility_properties_id) REFERENCES hsl_accessibility_properties (id);
