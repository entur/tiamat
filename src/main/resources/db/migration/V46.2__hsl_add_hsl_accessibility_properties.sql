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
    service_area_width                numeric(7, 2), -- (m)
    service_area_length               numeric(7, 2), -- (m)
    platform_edge_warning_area        boolean,
    guidance_tiles                    boolean,
    guidance_stripe                   boolean,
    service_area_stripes              boolean,
    sidewalk_accessible_connection    boolean,
    stop_area_surroundings_accessible boolean,
    curved_stop                       boolean,
    stop_type                         character varying(255),
    shelter_type                      character varying(255),
    guidance_type                     character varying(255),
    map_type                          character varying(255),
    pedestrian_crossing_ramp_type     character varying(255)
);

-- Add comments mostly for the direct Tiamat DB connection.
COMMENT ON COLUMN hsl_accessibility_properties.stop_area_side_slope IS 'Pysäkkialueen sivukaltevuus (%)';
COMMENT ON COLUMN hsl_accessibility_properties.stop_area_lengthwise_slope IS 'Pysäkkialueen pituuskaltevuus (%)';
COMMENT ON COLUMN hsl_accessibility_properties.end_ramp_slope IS 'Päätyluiskan kaltevuus (%)';
COMMENT ON COLUMN hsl_accessibility_properties.shelter_lane_distance IS 'Katoksen ja ajoradan välinen leveys (cm)';
COMMENT ON COLUMN hsl_accessibility_properties.curb_back_of_rail_distance IS 'Reunakiven etäisyys kiskon selästä (cm)';
COMMENT ON COLUMN hsl_accessibility_properties.curb_drive_side_of_rail_distance IS 'Reunakiven etäisyys kiskon ajoreunasta (cm)';
COMMENT ON COLUMN hsl_accessibility_properties.structure_lane_distance IS 'Rakenteiden ja ajoradan välinen pienin leveys (cm)';
COMMENT ON COLUMN hsl_accessibility_properties.stop_elevation_from_rail_top IS 'Pysäkin korotus kiskon ajopintaan nähden (cm)';
COMMENT ON COLUMN hsl_accessibility_properties.stop_elevation_from_sidewalk IS 'Pysäkin korotus jalkakäytävään nähden (cm)';
COMMENT ON COLUMN hsl_accessibility_properties.lower_cleat_height IS 'Alapienan korkeus (cm)';
COMMENT ON COLUMN hsl_accessibility_properties.service_area_width IS 'Palvelualueen leveys (m)';
COMMENT ON COLUMN hsl_accessibility_properties.service_area_length IS 'Palvelualueen pituus (m)';
COMMENT ON COLUMN hsl_accessibility_properties.platform_edge_warning_area IS 'Pysäkkialueen varoitusalue';
COMMENT ON COLUMN hsl_accessibility_properties.guidance_tiles IS 'Opaslaatat';
COMMENT ON COLUMN hsl_accessibility_properties.guidance_stripe IS 'Opasteraita';
COMMENT ON COLUMN hsl_accessibility_properties.service_area_stripes IS 'Palvelualueen raidoitus';
COMMENT ON COLUMN hsl_accessibility_properties.sidewalk_accessible_connection IS 'Esteetön yhteys jalkakäytävältä pysäkille';
COMMENT ON COLUMN hsl_accessibility_properties.stop_area_surroundings_accessible IS 'Pysäkin ympäristo: Esteellinen / Esteetön';
COMMENT ON COLUMN hsl_accessibility_properties.curved_stop IS 'Kaareva pysäkki';
COMMENT ON COLUMN hsl_accessibility_properties.stop_type IS 'Pysäkin tyyppi: Syvennys (pullOut) / Uloke (busBulb) / Ajoradalla (inLane) / Muu (other)';
COMMENT ON COLUMN hsl_accessibility_properties.shelter_type IS 'Katoksen tyyppi: Leveä (wide) / Kapea (narrow) / Muu (other)';
COMMENT ON COLUMN hsl_accessibility_properties.guidance_type IS 'Opasteiden tyyppi: Pisteopaste (braille) / Ei opastetta (none) / Muu opastus (other)';
COMMENT ON COLUMN hsl_accessibility_properties.map_type IS 'Kartan tyyppi: Kohokartta (tactile) / Ei karttaa (none) / Muu kartta (other)';
COMMENT ON COLUMN hsl_accessibility_properties.pedestrian_crossing_ramp_type IS 'Suojatien luiskaus';

ALTER TABLE ONLY hsl_accessibility_properties
    ADD CONSTRAINT hsl_accessibility_properties_pkey PRIMARY KEY (id);

ALTER TABLE ONLY hsl_accessibility_properties
    ADD CONSTRAINT hsl_accessibility_properties_netex_id_version_constraint UNIQUE (netex_id, version);

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
