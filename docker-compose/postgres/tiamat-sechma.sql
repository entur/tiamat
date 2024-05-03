--
-- PostgreSQL database dump
--

-- Dumped from database version 13.7 (Debian 13.7-1.pgdg110+1)
-- Dumped by pg_dump version 14.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: topology; Type: SCHEMA; Schema: -; Owner: tiamat
--

CREATE SCHEMA topology;


ALTER SCHEMA topology OWNER TO tiamat;

--
-- Name: SCHEMA topology; Type: COMMENT; Schema: -; Owner: tiamat
--

COMMENT ON SCHEMA topology IS 'PostGIS Topology schema';


--
-- Name: pg_trgm; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA public;


--
-- Name: EXTENSION pg_trgm; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pg_trgm IS 'text similarity measurement and index searching based on trigrams';


--
-- Name: postgis; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;


--
-- Name: EXTENSION postgis; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis IS 'PostGIS geometry, geography, and raster spatial types and functions';


--
-- Name: postgis_topology; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS postgis_topology WITH SCHEMA topology;


--
-- Name: EXTENSION postgis_topology; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis_topology IS 'PostGIS topology spatial types and functions';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: access_space; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.access_space (
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
    centroid public.geometry,
    all_areas_wheelchair_accessible boolean,
    covered integer,
    level_ref character varying(255),
    level_ref_version character varying(255),
    site_ref character varying(255),
    site_ref_version character varying(255),
    label_lang character varying(5),
    label_value character varying(255),
    polygon_id bigint,
    accessibility_assessment_id bigint,
    place_equipments_id bigint,
    changed_by character varying(255)
);


ALTER TABLE public.access_space OWNER TO tiamat;

--
-- Name: access_space_alternative_names; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.access_space_alternative_names (
    access_space_id bigint NOT NULL,
    alternative_names_id bigint NOT NULL
);


ALTER TABLE public.access_space_alternative_names OWNER TO tiamat;

--
-- Name: access_space_check_constraints; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.access_space_check_constraints (
    access_space_id bigint NOT NULL,
    check_constraints_id bigint NOT NULL
);


ALTER TABLE public.access_space_check_constraints OWNER TO tiamat;

--
-- Name: access_space_equipment_places; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.access_space_equipment_places (
    access_space_id bigint NOT NULL,
    equipment_places_id bigint NOT NULL
);


ALTER TABLE public.access_space_equipment_places OWNER TO tiamat;

--
-- Name: access_space_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.access_space_key_values (
    access_space_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.access_space_key_values OWNER TO tiamat;

--
-- Name: access_space_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.access_space_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.access_space_seq OWNER TO tiamat;

--
-- Name: accessibility_assessment; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.accessibility_assessment (
    id bigint NOT NULL,
    netex_id character varying(255),
    changed timestamp without time zone,
    created timestamp without time zone,
    from_date timestamp without time zone,
    to_date timestamp without time zone,
    version bigint NOT NULL,
    version_comment character varying(255),
    mobility_impaired_access character varying(255),
    changed_by character varying(255)
);


ALTER TABLE public.accessibility_assessment OWNER TO tiamat;

--
-- Name: accessibility_assessment_limitations; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.accessibility_assessment_limitations (
    accessibility_assessment_id bigint NOT NULL,
    limitations_id bigint NOT NULL
);


ALTER TABLE public.accessibility_assessment_limitations OWNER TO tiamat;

--
-- Name: accessibility_assessment_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.accessibility_assessment_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.accessibility_assessment_seq OWNER TO tiamat;

--
-- Name: accessibility_limitation; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.accessibility_limitation (
    id bigint NOT NULL,
    netex_id character varying(255),
    changed timestamp without time zone,
    created timestamp without time zone,
    from_date timestamp without time zone,
    to_date timestamp without time zone,
    version bigint NOT NULL,
    version_comment character varying(255),
    audible_signals_available character varying(255),
    escalator_free_access character varying(255),
    lift_free_access character varying(255),
    step_free_access character varying(255),
    visual_signs_available character varying(255),
    wheelchair_access character varying(255),
    changed_by character varying(255)
);


ALTER TABLE public.accessibility_limitation OWNER TO tiamat;

--
-- Name: accessibility_limitation_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.accessibility_limitation_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.accessibility_limitation_seq OWNER TO tiamat;

--
-- Name: alternative_name; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.alternative_name (
    id bigint NOT NULL,
    netex_id character varying(255),
    changed timestamp without time zone,
    created timestamp without time zone,
    from_date timestamp without time zone,
    to_date timestamp without time zone,
    version bigint NOT NULL,
    version_comment character varying(255),
    abbreviation_lang character varying(255),
    abbreviation_value character varying(255),
    lang character varying(255),
    name_lang character varying(255),
    name_value character varying(255),
    name_type character varying(11),
    named_object_ref bytea,
    qualifier_name_lang character varying(255),
    qualifier_name_value character varying(255),
    short_name_lang character varying(255),
    short_name_value character varying(255),
    type_of_name character varying(255),
    changed_by character varying(255)
);


ALTER TABLE public.alternative_name OWNER TO tiamat;

--
-- Name: alternative_name_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.alternative_name_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.alternative_name_seq OWNER TO tiamat;

--
-- Name: boarding_position; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.boarding_position (
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
    centroid public.geometry,
    all_areas_wheelchair_accessible boolean,
    covered integer,
    level_ref character varying(255),
    level_ref_version character varying(255),
    site_ref character varying(255),
    site_ref_version character varying(255),
    label_lang character varying(5),
    label_value character varying(255),
    boarding_position_type character varying(255),
    public_code character varying(255),
    polygon_id bigint,
    accessibility_assessment_id bigint,
    place_equipments_id bigint,
    changed_by character varying(255)
);


ALTER TABLE public.boarding_position OWNER TO tiamat;

--
-- Name: boarding_position_alternative_names; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.boarding_position_alternative_names (
    boarding_position_id bigint NOT NULL,
    alternative_names_id bigint NOT NULL
);


ALTER TABLE public.boarding_position_alternative_names OWNER TO tiamat;

--
-- Name: boarding_position_check_constraints; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.boarding_position_check_constraints (
    boarding_position_id bigint NOT NULL,
    check_constraints_id bigint NOT NULL
);


ALTER TABLE public.boarding_position_check_constraints OWNER TO tiamat;

--
-- Name: boarding_position_equipment_places; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.boarding_position_equipment_places (
    boarding_position_id bigint NOT NULL,
    equipment_places_id bigint NOT NULL
);


ALTER TABLE public.boarding_position_equipment_places OWNER TO tiamat;

--
-- Name: boarding_position_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.boarding_position_key_values (
    boarding_position_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.boarding_position_key_values OWNER TO tiamat;

--
-- Name: boarding_position_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.boarding_position_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.boarding_position_seq OWNER TO tiamat;

--
-- Name: check_constraint; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.check_constraint (
    id bigint NOT NULL,
    netex_id character varying(255),
    changed timestamp without time zone,
    created timestamp without time zone,
    from_date timestamp without time zone,
    to_date timestamp without time zone,
    version bigint NOT NULL,
    version_comment character varying(255),
    description_id bigint,
    name_id bigint,
    changed_by character varying(255)
);


ALTER TABLE public.check_constraint OWNER TO tiamat;

--
-- Name: check_constraint_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.check_constraint_key_values (
    check_constraint_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.check_constraint_key_values OWNER TO tiamat;

--
-- Name: check_constraint_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.check_constraint_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.check_constraint_seq OWNER TO tiamat;

--
-- Name: destination_display_view; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.destination_display_view (
    id bigint NOT NULL,
    branding_ref bytea,
    public_code character varying(255),
    short_code character varying(255),
    name_id bigint
);


ALTER TABLE public.destination_display_view OWNER TO tiamat;

--
-- Name: equipment_place; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.equipment_place (
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
    centroid public.geometry,
    polygon_id bigint,
    changed_by character varying(255)
);


ALTER TABLE public.equipment_place OWNER TO tiamat;

--
-- Name: equipment_place_equipment_positions; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.equipment_place_equipment_positions (
    equipment_place_id bigint NOT NULL,
    equipment_positions_id bigint NOT NULL
);


ALTER TABLE public.equipment_place_equipment_positions OWNER TO tiamat;

--
-- Name: equipment_place_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.equipment_place_key_values (
    equipment_place_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.equipment_place_key_values OWNER TO tiamat;

--
-- Name: equipment_place_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.equipment_place_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.equipment_place_seq OWNER TO tiamat;

--
-- Name: equipment_position; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.equipment_position (
    id bigint NOT NULL,
    netex_id character varying(255),
    changed timestamp without time zone,
    created timestamp without time zone,
    from_date timestamp without time zone,
    to_date timestamp without time zone,
    version bigint NOT NULL,
    version_comment character varying(255),
    reference_point_ref character varying(255),
    reference_point_version character varying(255),
    x_offset numeric(19,2),
    y_offset numeric(19,2),
    description_id bigint,
    changed_by character varying(255)
);


ALTER TABLE public.equipment_position OWNER TO tiamat;

--
-- Name: equipment_position_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.equipment_position_key_values (
    equipment_position_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.equipment_position_key_values OWNER TO tiamat;

--
-- Name: equipment_position_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.equipment_position_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.equipment_position_seq OWNER TO tiamat;

--
-- Name: export_job; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.export_job (
    id bigint NOT NULL,
    file_name character varying(255),
    finished timestamp without time zone,
    job_url character varying(255),
    message text,
    started timestamp without time zone,
    status integer,
    sub_folder character varying(40)
);


ALTER TABLE public.export_job OWNER TO tiamat;

--
-- Name: export_job_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.export_job_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.export_job_seq OWNER TO tiamat;

--
-- Name: fare_zone; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.fare_zone (
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
    centroid public.geometry,
    polygon_id bigint,
    changed_by character varying(255),
    scoping_method character varying(255),
    transport_organisation_ref character varying(255),
    zone_topology character varying(255)
);


ALTER TABLE public.fare_zone OWNER TO tiamat;

--
-- Name: fare_zone_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.fare_zone_key_values (
    fare_zone_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.fare_zone_key_values OWNER TO tiamat;

--
-- Name: fare_zone_members; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.fare_zone_members (
    fare_zone_id bigint NOT NULL,
    ref character varying(255),
    version character varying(255)
);


ALTER TABLE public.fare_zone_members OWNER TO tiamat;

--
-- Name: fare_zone_neighbours; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.fare_zone_neighbours (
    fare_zone_id bigint NOT NULL,
    ref character varying(255),
    version character varying(255)
);


ALTER TABLE public.fare_zone_neighbours OWNER TO tiamat;

--
-- Name: fare_zone_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.fare_zone_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.fare_zone_seq OWNER TO tiamat;

--
-- Name: group_of_stop_places; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.group_of_stop_places (
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
    short_name_lang character varying(5),
    short_name_value character varying(255),
    centroid public.geometry,
    purpose_of_grouping_id bigint
);


ALTER TABLE public.group_of_stop_places OWNER TO tiamat;

--
-- Name: group_of_stop_places_alternative_names; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.group_of_stop_places_alternative_names (
    group_of_stop_places_id bigint NOT NULL,
    alternative_names_id bigint NOT NULL
);


ALTER TABLE public.group_of_stop_places_alternative_names OWNER TO tiamat;

--
-- Name: group_of_stop_places_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.group_of_stop_places_key_values (
    group_of_stop_places_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.group_of_stop_places_key_values OWNER TO tiamat;

--
-- Name: group_of_stop_places_members; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.group_of_stop_places_members (
    group_of_stop_places_id bigint NOT NULL,
    ref character varying(255),
    version character varying(255)
);


ALTER TABLE public.group_of_stop_places_members OWNER TO tiamat;

--
-- Name: group_of_stop_places_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.group_of_stop_places_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.group_of_stop_places_seq OWNER TO tiamat;

--
-- Name: group_of_tariff_zones; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.group_of_tariff_zones (
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


ALTER TABLE public.group_of_tariff_zones OWNER TO tiamat;

--
-- Name: group_of_tariff_zones_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.group_of_tariff_zones_key_values (
    group_of_tariff_zones_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.group_of_tariff_zones_key_values OWNER TO tiamat;

--
-- Name: group_of_tariff_zones_members; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.group_of_tariff_zones_members (
    group_of_tariff_zones_id bigint NOT NULL,
    ref character varying(255) NOT NULL,
    version character varying(255)
);


ALTER TABLE public.group_of_tariff_zones_members OWNER TO tiamat;

--
-- Name: group_of_tariff_zones_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.group_of_tariff_zones_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.group_of_tariff_zones_seq OWNER TO tiamat;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO tiamat;

--
-- Name: id_generator; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.id_generator (
    table_name character varying(50),
    id_value bigint
);


ALTER TABLE public.id_generator OWNER TO tiamat;

--
-- Name: installed_equipment; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.installed_equipment (
    id character varying(255) NOT NULL,
    item_type character varying(255),
    equipment_id integer NOT NULL
);


ALTER TABLE public.installed_equipment OWNER TO tiamat;

--
-- Name: installed_equipment_version_structure; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.installed_equipment_version_structure (
    dtype character varying(31) NOT NULL,
    id bigint NOT NULL,
    netex_id character varying(255),
    changed timestamp without time zone,
    created timestamp without time zone,
    from_date timestamp without time zone,
    to_date timestamp without time zone,
    version bigint NOT NULL,
    version_comment character varying(255),
    out_of_service boolean,
    private_code_type character varying(255),
    private_code_value character varying(255),
    number_of_machines numeric(19,2),
    ticket_machines boolean,
    ticket_office boolean,
    cycle_storage_type integer,
    number_of_spaces numeric(19,2),
    gender integer,
    number_of_toilets numeric(19,2),
    brand_graphic character varying(255),
    height numeric(19,2),
    height_from_floor numeric(19,2),
    machine_readable boolean,
    sign_graphic character varying(255),
    width numeric(19,2),
    content_lang character varying(255),
    content_value character varying(255),
    sign_content_type character varying(255),
    air_conditioned boolean,
    heated boolean,
    length numeric(19,2),
    seats numeric(19,2),
    smoking_allowed boolean,
    step_free boolean,
    wheelchair_area_length numeric(19,2),
    wheelchair_area_width numeric(19,2),
    enclosed boolean,
    class_of_use_ref bytea,
    women_only boolean,
    changed_by character varying(255)
);


ALTER TABLE public.installed_equipment_version_structure OWNER TO tiamat;

--
-- Name: installed_equipment_version_structure_installed_equipment; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.installed_equipment_version_structure_installed_equipment (
    place_equipment_id bigint NOT NULL,
    installed_equipment_id bigint NOT NULL
);


ALTER TABLE public.installed_equipment_version_structure_installed_equipment OWNER TO tiamat;

--
-- Name: installed_equipment_version_structure_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.installed_equipment_version_structure_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.installed_equipment_version_structure_seq OWNER TO tiamat;

--
-- Name: level; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.level (
    id bigint NOT NULL,
    netex_id character varying(255),
    changed timestamp without time zone,
    created timestamp without time zone,
    from_date timestamp without time zone,
    to_date timestamp without time zone,
    version bigint NOT NULL,
    version_comment character varying(255),
    all_areas_wheelchair_accessible boolean,
    public_code character varying(255),
    public_use boolean,
    description_id bigint,
    name_id bigint,
    short_name_id bigint,
    changed_by character varying(255)
);


ALTER TABLE public.level OWNER TO tiamat;

--
-- Name: level_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.level_key_values (
    level_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.level_key_values OWNER TO tiamat;

--
-- Name: level_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.level_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.level_seq OWNER TO tiamat;

--
-- Name: multilingual_string_entity; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.multilingual_string_entity (
    id bigint NOT NULL,
    lang character varying(5),
    value character varying(255)
);


ALTER TABLE public.multilingual_string_entity OWNER TO tiamat;

--
-- Name: navigation_path; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.navigation_path (
    id character varying(255) NOT NULL,
    item_type character varying(255),
    path_id integer NOT NULL
);


ALTER TABLE public.navigation_path OWNER TO tiamat;

--
-- Name: parking; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking (
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
    centroid public.geometry,
    all_areas_wheelchair_accessible boolean,
    covered integer,
    parent_site_ref character varying(255),
    parent_site_ref_version character varying(255),
    booking_url character varying(255),
    free_parking_out_of_hours boolean,
    number_of_parking_levels numeric(19,2),
    overnight_parking_permitted boolean,
    parking_layout integer,
    parking_reservation integer,
    parking_type character varying(255),
    principal_capacity numeric(19,2),
    prohibited_for_hazardous_materials boolean,
    real_time_occupancy_available boolean,
    recharging_available boolean,
    secure boolean,
    total_capacity numeric(19,2),
    polygon_id bigint,
    accessibility_assessment_id bigint,
    place_equipments_id bigint,
    topographic_place_id bigint,
    changed_by character varying(255)
);


ALTER TABLE public.parking OWNER TO tiamat;

--
-- Name: parking_adjacent_sites; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking_adjacent_sites (
    parking_id bigint NOT NULL,
    ref character varying(255),
    version character varying(255)
);


ALTER TABLE public.parking_adjacent_sites OWNER TO tiamat;

--
-- Name: parking_alternative_names; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking_alternative_names (
    parking_id bigint NOT NULL,
    alternative_names_id bigint NOT NULL
);


ALTER TABLE public.parking_alternative_names OWNER TO tiamat;

--
-- Name: parking_area; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking_area (
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
    centroid public.geometry,
    all_areas_wheelchair_accessible boolean,
    covered integer,
    level_ref character varying(255),
    level_ref_version character varying(255),
    site_ref character varying(255),
    site_ref_version character varying(255),
    total_capacity numeric(19,2),
    polygon_id bigint,
    accessibility_assessment_id bigint,
    place_equipments_id bigint,
    parking_properties_id bigint,
    changed_by character varying(255)
);


ALTER TABLE public.parking_area OWNER TO tiamat;

--
-- Name: parking_area_alternative_names; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking_area_alternative_names (
    parking_area_id bigint NOT NULL,
    alternative_names_id bigint NOT NULL
);


ALTER TABLE public.parking_area_alternative_names OWNER TO tiamat;

--
-- Name: parking_area_check_constraints; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking_area_check_constraints (
    parking_area_id bigint NOT NULL,
    check_constraints_id bigint NOT NULL
);


ALTER TABLE public.parking_area_check_constraints OWNER TO tiamat;

--
-- Name: parking_area_equipment_places; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking_area_equipment_places (
    parking_area_id bigint NOT NULL,
    equipment_places_id bigint NOT NULL
);


ALTER TABLE public.parking_area_equipment_places OWNER TO tiamat;

--
-- Name: parking_area_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking_area_key_values (
    parking_area_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.parking_area_key_values OWNER TO tiamat;

--
-- Name: parking_area_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.parking_area_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.parking_area_seq OWNER TO tiamat;

--
-- Name: parking_capacity; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking_capacity (
    id bigint NOT NULL,
    netex_id character varying(255),
    changed timestamp without time zone,
    created timestamp without time zone,
    from_date timestamp without time zone,
    to_date timestamp without time zone,
    version bigint NOT NULL,
    version_comment character varying(255),
    number_of_spaces numeric(19,2),
    parent_ref bytea,
    parking_stay_type character varying(255),
    parking_user_type character varying(255),
    parking_vehicle_type character varying(255),
    changed_by character varying(255),
    number_of_spaces_with_recharge_point numeric(19,2)
);


ALTER TABLE public.parking_capacity OWNER TO tiamat;

--
-- Name: parking_capacity_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.parking_capacity_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.parking_capacity_seq OWNER TO tiamat;

--
-- Name: parking_equipment_places; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking_equipment_places (
    parking_id bigint NOT NULL,
    equipment_places_id bigint NOT NULL
);


ALTER TABLE public.parking_equipment_places OWNER TO tiamat;

--
-- Name: parking_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking_key_values (
    parking_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.parking_key_values OWNER TO tiamat;

--
-- Name: parking_parking_areas; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking_parking_areas (
    parking_id bigint NOT NULL,
    parking_areas_id bigint NOT NULL
);


ALTER TABLE public.parking_parking_areas OWNER TO tiamat;

--
-- Name: parking_parking_payment_process; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking_parking_payment_process (
    parking_id bigint NOT NULL,
    parking_payment_process character varying(255)
);


ALTER TABLE public.parking_parking_payment_process OWNER TO tiamat;

--
-- Name: parking_parking_properties; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking_parking_properties (
    parking_id bigint NOT NULL,
    parking_properties_id bigint NOT NULL
);


ALTER TABLE public.parking_parking_properties OWNER TO tiamat;

--
-- Name: parking_parking_vehicle_types; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking_parking_vehicle_types (
    parking_id bigint NOT NULL,
    parking_vehicle_types character varying(255)
);


ALTER TABLE public.parking_parking_vehicle_types OWNER TO tiamat;

--
-- Name: parking_properties; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking_properties (
    id bigint NOT NULL,
    netex_id character varying(255),
    changed timestamp without time zone,
    created timestamp without time zone,
    from_date timestamp without time zone,
    to_date timestamp without time zone,
    version bigint NOT NULL,
    version_comment character varying(255),
    changed_by character varying(255)
);


ALTER TABLE public.parking_properties OWNER TO tiamat;

--
-- Name: parking_properties_parking_user_types; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking_properties_parking_user_types (
    parking_properties_id bigint NOT NULL,
    parking_user_types character varying(255)
);


ALTER TABLE public.parking_properties_parking_user_types OWNER TO tiamat;

--
-- Name: parking_properties_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.parking_properties_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.parking_properties_seq OWNER TO tiamat;

--
-- Name: parking_properties_spaces; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.parking_properties_spaces (
    parking_properties_id bigint NOT NULL,
    spaces_id bigint NOT NULL
);


ALTER TABLE public.parking_properties_spaces OWNER TO tiamat;

--
-- Name: parking_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.parking_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.parking_seq OWNER TO tiamat;

--
-- Name: path_junction; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.path_junction (
    id bigint NOT NULL,
    netex_id character varying(255),
    changed timestamp without time zone,
    created timestamp without time zone,
    from_date timestamp without time zone,
    to_date timestamp without time zone,
    version bigint NOT NULL,
    version_comment character varying(255),
    changed_by character varying(255)
);


ALTER TABLE public.path_junction OWNER TO tiamat;

--
-- Name: path_junction_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.path_junction_key_values (
    path_junction_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.path_junction_key_values OWNER TO tiamat;

--
-- Name: path_junction_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.path_junction_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.path_junction_seq OWNER TO tiamat;

--
-- Name: path_link; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.path_link (
    id bigint NOT NULL,
    netex_id character varying(255),
    changed timestamp without time zone,
    created timestamp without time zone,
    from_date timestamp without time zone,
    to_date timestamp without time zone,
    version bigint NOT NULL,
    version_comment character varying(255),
    line_string public.geometry,
    default_duration bytea,
    frequent_traveller_duration bytea,
    mobility_restricted_traveller_duration bytea,
    occasional_traveller_duration bytea,
    from_id bigint NOT NULL,
    to_id bigint NOT NULL,
    changed_by character varying(255)
);


ALTER TABLE public.path_link OWNER TO tiamat;

--
-- Name: path_link_end; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.path_link_end (
    id bigint NOT NULL,
    netex_id character varying(255),
    place_ref character varying(255),
    place_version character varying(255),
    path_junction_id bigint
);


ALTER TABLE public.path_link_end OWNER TO tiamat;

--
-- Name: path_link_end_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.path_link_end_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.path_link_end_seq OWNER TO tiamat;

--
-- Name: path_link_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.path_link_key_values (
    path_link_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.path_link_key_values OWNER TO tiamat;

--
-- Name: path_link_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.path_link_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.path_link_seq OWNER TO tiamat;

--
-- Name: persistable_polygon; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.persistable_polygon (
    id bigint NOT NULL,
    polygon public.geometry
);


ALTER TABLE public.persistable_polygon OWNER TO tiamat;

--
-- Name: persistable_polygon_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.persistable_polygon_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.persistable_polygon_seq OWNER TO tiamat;

--
-- Name: purpose_of_grouping; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.purpose_of_grouping (
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
    name_value character varying(255)
);


ALTER TABLE public.purpose_of_grouping OWNER TO tiamat;

--
-- Name: purpose_of_grouping_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.purpose_of_grouping_key_values (
    purpose_of_grouping_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.purpose_of_grouping_key_values OWNER TO tiamat;

--
-- Name: purpose_of_grouping_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.purpose_of_grouping_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.purpose_of_grouping_seq OWNER TO tiamat;

--
-- Name: quay; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.quay (
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
    centroid public.geometry,
    all_areas_wheelchair_accessible boolean,
    covered integer,
    level_ref character varying(255),
    level_ref_version character varying(255),
    site_ref character varying(255),
    site_ref_version character varying(255),
    label_lang character varying(5),
    label_value character varying(255),
    compass_bearing real,
    public_code character varying(255),
    polygon_id bigint,
    accessibility_assessment_id bigint,
    place_equipments_id bigint,
    changed_by character varying(255)
);


ALTER TABLE public.quay OWNER TO tiamat;

--
-- Name: quay_alternative_names; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.quay_alternative_names (
    quay_id bigint NOT NULL,
    alternative_names_id bigint NOT NULL
);


ALTER TABLE public.quay_alternative_names OWNER TO tiamat;

--
-- Name: quay_boarding_positions; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.quay_boarding_positions (
    quay_id bigint NOT NULL,
    boarding_positions_id bigint NOT NULL
);


ALTER TABLE public.quay_boarding_positions OWNER TO tiamat;

--
-- Name: quay_check_constraints; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.quay_check_constraints (
    quay_id bigint NOT NULL,
    check_constraints_id bigint NOT NULL
);


ALTER TABLE public.quay_check_constraints OWNER TO tiamat;

--
-- Name: quay_equipment_places; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.quay_equipment_places (
    quay_id bigint NOT NULL,
    equipment_places_id bigint NOT NULL
);


ALTER TABLE public.quay_equipment_places OWNER TO tiamat;

--
-- Name: quay_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.quay_key_values (
    quay_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.quay_key_values OWNER TO tiamat;

--
-- Name: quay_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.quay_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.quay_seq OWNER TO tiamat;

--
-- Name: schema_version; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.schema_version (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.schema_version OWNER TO tiamat;

--
-- Name: seq_multilingual_string_entity; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.seq_multilingual_string_entity
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seq_multilingual_string_entity OWNER TO tiamat;

--
-- Name: stop_place; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.stop_place (
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
    centroid public.geometry,
    all_areas_wheelchair_accessible boolean,
    covered integer,
    parent_site_ref character varying(255),
    parent_site_ref_version character varying(255),
    air_submode character varying(255),
    border_crossing boolean,
    bus_submode character varying(255),
    coach_submode character varying(255),
    funicular_submode character varying(255),
    metro_submode character varying(255),
    public_code character varying(255),
    rail_submode character varying(255),
    stop_place_type character varying(255),
    telecabin_submode character varying(255),
    tram_submode character varying(255),
    transport_mode character varying(255),
    water_submode character varying(255),
    weighting character varying(255),
    polygon_id bigint,
    accessibility_assessment_id bigint,
    place_equipments_id bigint,
    topographic_place_id bigint,
    changed_by character varying(255),
    parent_stop_place boolean DEFAULT false NOT NULL,
    modification_enumeration character varying(255)
);


ALTER TABLE public.stop_place OWNER TO tiamat;

--
-- Name: stop_place_access_spaces; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.stop_place_access_spaces (
    stop_place_id bigint NOT NULL,
    access_spaces_id bigint NOT NULL
);


ALTER TABLE public.stop_place_access_spaces OWNER TO tiamat;

--
-- Name: stop_place_adjacent_sites; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.stop_place_adjacent_sites (
    stop_place_id bigint NOT NULL,
    ref character varying(255),
    version character varying(255)
);


ALTER TABLE public.stop_place_adjacent_sites OWNER TO tiamat;

--
-- Name: stop_place_alternative_names; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.stop_place_alternative_names (
    stop_place_id bigint NOT NULL,
    alternative_names_id bigint NOT NULL
);


ALTER TABLE public.stop_place_alternative_names OWNER TO tiamat;

--
-- Name: stop_place_children; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.stop_place_children (
    stop_place_id bigint NOT NULL,
    children_id bigint NOT NULL
);


ALTER TABLE public.stop_place_children OWNER TO tiamat;

--
-- Name: stop_place_equipment_places; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.stop_place_equipment_places (
    stop_place_id bigint NOT NULL,
    equipment_places_id bigint NOT NULL
);


ALTER TABLE public.stop_place_equipment_places OWNER TO tiamat;

--
-- Name: stop_place_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.stop_place_key_values (
    stop_place_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.stop_place_key_values OWNER TO tiamat;

--
-- Name: stop_place_quays; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.stop_place_quays (
    stop_place_id bigint NOT NULL,
    quays_id bigint NOT NULL
);


ALTER TABLE public.stop_place_quays OWNER TO tiamat;

--
-- Name: stop_place_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.stop_place_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.stop_place_seq OWNER TO tiamat;

--
-- Name: stop_place_tariff_zones; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.stop_place_tariff_zones (
    stop_place_id bigint NOT NULL,
    ref character varying(255),
    version character varying(255),
    purpose_of_grouping_ref character varying(255),
    purpose_of_grouping_ref_version character varying(255)
);


ALTER TABLE public.stop_place_tariff_zones OWNER TO tiamat;

--
-- Name: tag; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.tag (
    netex_reference character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    comment character varying(255),
    created timestamp without time zone,
    created_by character varying(255),
    removed timestamp without time zone,
    removed_by character varying(255)
);


ALTER TABLE public.tag OWNER TO tiamat;

--
-- Name: tariff_zone; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.tariff_zone (
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
    centroid public.geometry,
    polygon_id bigint,
    changed_by character varying(255)
);


ALTER TABLE public.tariff_zone OWNER TO tiamat;

--
-- Name: tariff_zone_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.tariff_zone_key_values (
    tariff_zone_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.tariff_zone_key_values OWNER TO tiamat;

--
-- Name: tariff_zone_ref_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.tariff_zone_ref_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tariff_zone_ref_seq OWNER TO tiamat;

--
-- Name: tariff_zone_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.tariff_zone_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tariff_zone_seq OWNER TO tiamat;

--
-- Name: topographic_place; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.topographic_place (
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
    centroid public.geometry,
    country_ref character varying(255),
    country_ref_value character varying(255),
    iso_code character varying(255),
    parent_ref character varying(255),
    parent_ref_version character varying(255),
    topographic_place_type character varying(255),
    polygon_id bigint,
    changed_by character varying(255)
);


ALTER TABLE public.topographic_place OWNER TO tiamat;

--
-- Name: topographic_place_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.topographic_place_key_values (
    topographic_place_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE public.topographic_place_key_values OWNER TO tiamat;

--
-- Name: topographic_place_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.topographic_place_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.topographic_place_seq OWNER TO tiamat;

--
-- Name: valid_between_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.valid_between_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.valid_between_seq OWNER TO tiamat;

--
-- Name: value; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.value (
    id bigint NOT NULL
);


ALTER TABLE public.value OWNER TO tiamat;

--
-- Name: value_items; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE public.value_items (
    value_id bigint NOT NULL,
    items character varying(255)
);


ALTER TABLE public.value_items OWNER TO tiamat;

--
-- Name: value_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE public.value_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.value_seq OWNER TO tiamat;

--
-- Name: access_space_key_values access_space_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space_key_values
    ADD CONSTRAINT access_space_key_values_pkey PRIMARY KEY (access_space_id, key_values_key);


--
-- Name: access_space access_space_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space
    ADD CONSTRAINT access_space_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: access_space access_space_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space
    ADD CONSTRAINT access_space_pkey PRIMARY KEY (id);


--
-- Name: accessibility_assessment accessibility_assessment_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.accessibility_assessment
    ADD CONSTRAINT accessibility_assessment_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: accessibility_assessment accessibility_assessment_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.accessibility_assessment
    ADD CONSTRAINT accessibility_assessment_pkey PRIMARY KEY (id);


--
-- Name: accessibility_limitation accessibility_limitation_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.accessibility_limitation
    ADD CONSTRAINT accessibility_limitation_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: accessibility_limitation accessibility_limitation_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.accessibility_limitation
    ADD CONSTRAINT accessibility_limitation_pkey PRIMARY KEY (id);


--
-- Name: alternative_name alternative_name_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.alternative_name
    ADD CONSTRAINT alternative_name_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: alternative_name alternative_name_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.alternative_name
    ADD CONSTRAINT alternative_name_pkey PRIMARY KEY (id);


--
-- Name: boarding_position_key_values boarding_position_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position_key_values
    ADD CONSTRAINT boarding_position_key_values_pkey PRIMARY KEY (boarding_position_id, key_values_key);


--
-- Name: boarding_position boarding_position_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position
    ADD CONSTRAINT boarding_position_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: boarding_position boarding_position_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position
    ADD CONSTRAINT boarding_position_pkey PRIMARY KEY (id);


--
-- Name: check_constraint_key_values check_constraint_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.check_constraint_key_values
    ADD CONSTRAINT check_constraint_key_values_pkey PRIMARY KEY (check_constraint_id, key_values_key);


--
-- Name: check_constraint check_constraint_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.check_constraint
    ADD CONSTRAINT check_constraint_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: check_constraint check_constraint_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.check_constraint
    ADD CONSTRAINT check_constraint_pkey PRIMARY KEY (id);


--
-- Name: destination_display_view destination_display_view_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.destination_display_view
    ADD CONSTRAINT destination_display_view_pkey PRIMARY KEY (id);


--
-- Name: equipment_place_key_values equipment_place_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.equipment_place_key_values
    ADD CONSTRAINT equipment_place_key_values_pkey PRIMARY KEY (equipment_place_id, key_values_key);


--
-- Name: equipment_place equipment_place_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.equipment_place
    ADD CONSTRAINT equipment_place_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: equipment_place equipment_place_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.equipment_place
    ADD CONSTRAINT equipment_place_pkey PRIMARY KEY (id);


--
-- Name: equipment_position_key_values equipment_position_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.equipment_position_key_values
    ADD CONSTRAINT equipment_position_key_values_pkey PRIMARY KEY (equipment_position_id, key_values_key);


--
-- Name: equipment_position equipment_position_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.equipment_position
    ADD CONSTRAINT equipment_position_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: equipment_position equipment_position_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.equipment_position
    ADD CONSTRAINT equipment_position_pkey PRIMARY KEY (id);


--
-- Name: export_job export_job_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.export_job
    ADD CONSTRAINT export_job_pkey PRIMARY KEY (id);


--
-- Name: fare_zone_key_values fare_zone_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.fare_zone_key_values
    ADD CONSTRAINT fare_zone_key_values_pkey PRIMARY KEY (fare_zone_id, key_values_key);


--
-- Name: fare_zone fare_zone_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.fare_zone
    ADD CONSTRAINT fare_zone_pkey PRIMARY KEY (id);


--
-- Name: group_of_stop_places_alternative_names group_of_stop_places_alternative_names_id_key; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_stop_places_alternative_names
    ADD CONSTRAINT group_of_stop_places_alternative_names_id_key UNIQUE (alternative_names_id);


--
-- Name: group_of_stop_places_key_values group_of_stop_places_key_values_id_key; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_stop_places_key_values
    ADD CONSTRAINT group_of_stop_places_key_values_id_key UNIQUE (key_values_id);


--
-- Name: group_of_stop_places_key_values group_of_stop_places_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_stop_places_key_values
    ADD CONSTRAINT group_of_stop_places_key_values_pkey PRIMARY KEY (group_of_stop_places_id, key_values_key);


--
-- Name: group_of_stop_places group_of_stop_places_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_stop_places
    ADD CONSTRAINT group_of_stop_places_pkey PRIMARY KEY (id);


--
-- Name: group_of_tariff_zones_key_values group_of_tariff_zones_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_tariff_zones_key_values
    ADD CONSTRAINT group_of_tariff_zones_key_values_pkey PRIMARY KEY (group_of_tariff_zones_id, key_values_key);


--
-- Name: group_of_tariff_zones_members group_of_tariff_zones_members_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_tariff_zones_members
    ADD CONSTRAINT group_of_tariff_zones_members_pkey PRIMARY KEY (group_of_tariff_zones_id, ref);


--
-- Name: group_of_tariff_zones group_of_tariff_zones_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_tariff_zones
    ADD CONSTRAINT group_of_tariff_zones_pkey PRIMARY KEY (id);


--
-- Name: id_generator id_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.id_generator
    ADD CONSTRAINT id_constraint UNIQUE (table_name, id_value);


--
-- Name: installed_equipment_version_structure installed_equipment_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.installed_equipment_version_structure
    ADD CONSTRAINT installed_equipment_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: installed_equipment_version_structure installed_equipment_version_structure_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.installed_equipment_version_structure
    ADD CONSTRAINT installed_equipment_version_structure_pkey PRIMARY KEY (id);


--
-- Name: level_key_values level_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.level_key_values
    ADD CONSTRAINT level_key_values_pkey PRIMARY KEY (level_id, key_values_key);


--
-- Name: level level_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.level
    ADD CONSTRAINT level_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: level level_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.level
    ADD CONSTRAINT level_pkey PRIMARY KEY (id);


--
-- Name: multilingual_string_entity multilingual_string_entity_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.multilingual_string_entity
    ADD CONSTRAINT multilingual_string_entity_pkey PRIMARY KEY (id);


--
-- Name: parking_area_key_values parking_area_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area_key_values
    ADD CONSTRAINT parking_area_key_values_pkey PRIMARY KEY (parking_area_id, key_values_key);


--
-- Name: parking_area parking_area_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area
    ADD CONSTRAINT parking_area_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: parking_area parking_area_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area
    ADD CONSTRAINT parking_area_pkey PRIMARY KEY (id);


--
-- Name: parking_capacity parking_capacity_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_capacity
    ADD CONSTRAINT parking_capacity_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: parking_capacity parking_capacity_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_capacity
    ADD CONSTRAINT parking_capacity_pkey PRIMARY KEY (id);


--
-- Name: parking_key_values parking_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_key_values
    ADD CONSTRAINT parking_key_values_pkey PRIMARY KEY (parking_id, key_values_key);


--
-- Name: parking parking_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking
    ADD CONSTRAINT parking_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: parking parking_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking
    ADD CONSTRAINT parking_pkey PRIMARY KEY (id);


--
-- Name: parking_properties parking_properties_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_properties
    ADD CONSTRAINT parking_properties_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: parking_properties parking_properties_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_properties
    ADD CONSTRAINT parking_properties_pkey PRIMARY KEY (id);


--
-- Name: path_junction_key_values path_junction_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_junction_key_values
    ADD CONSTRAINT path_junction_key_values_pkey PRIMARY KEY (path_junction_id, key_values_key);


--
-- Name: path_junction path_junction_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_junction
    ADD CONSTRAINT path_junction_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: path_junction path_junction_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_junction
    ADD CONSTRAINT path_junction_pkey PRIMARY KEY (id);


--
-- Name: path_link_end path_link_end_netex_id_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_link_end
    ADD CONSTRAINT path_link_end_netex_id_constraint UNIQUE (netex_id);


--
-- Name: path_link_end path_link_end_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_link_end
    ADD CONSTRAINT path_link_end_pkey PRIMARY KEY (id);


--
-- Name: path_link_key_values path_link_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_link_key_values
    ADD CONSTRAINT path_link_key_values_pkey PRIMARY KEY (path_link_id, key_values_key);


--
-- Name: path_link path_link_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_link
    ADD CONSTRAINT path_link_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: path_link path_link_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_link
    ADD CONSTRAINT path_link_pkey PRIMARY KEY (id);


--
-- Name: persistable_polygon persistable_polygon_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.persistable_polygon
    ADD CONSTRAINT persistable_polygon_pkey PRIMARY KEY (id);


--
-- Name: purpose_of_grouping_key_values purpose_of_grouping_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.purpose_of_grouping_key_values
    ADD CONSTRAINT purpose_of_grouping_key_values_pkey PRIMARY KEY (purpose_of_grouping_id, key_values_key);


--
-- Name: purpose_of_grouping purpose_of_grouping_name_value_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.purpose_of_grouping
    ADD CONSTRAINT purpose_of_grouping_name_value_constraint UNIQUE (name_value);


--
-- Name: purpose_of_grouping purpose_of_grouping_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.purpose_of_grouping
    ADD CONSTRAINT purpose_of_grouping_netex_id_version_constraint UNIQUE (netex_id);


--
-- Name: purpose_of_grouping purpose_of_grouping_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.purpose_of_grouping
    ADD CONSTRAINT purpose_of_grouping_pkey PRIMARY KEY (id);


--
-- Name: quay_key_values quay_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay_key_values
    ADD CONSTRAINT quay_key_values_pkey PRIMARY KEY (quay_id, key_values_key);


--
-- Name: quay quay_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay
    ADD CONSTRAINT quay_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: quay quay_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay
    ADD CONSTRAINT quay_pkey PRIMARY KEY (id);


--
-- Name: schema_version schema_version_pk; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.schema_version
    ADD CONSTRAINT schema_version_pk PRIMARY KEY (installed_rank);


--
-- Name: stop_place_children stop_place_children_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_children
    ADD CONSTRAINT stop_place_children_pkey PRIMARY KEY (stop_place_id, children_id);


--
-- Name: stop_place_key_values stop_place_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_key_values
    ADD CONSTRAINT stop_place_key_values_pkey PRIMARY KEY (stop_place_id, key_values_key);


--
-- Name: stop_place stop_place_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place
    ADD CONSTRAINT stop_place_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: stop_place stop_place_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place
    ADD CONSTRAINT stop_place_pkey PRIMARY KEY (id);


--
-- Name: stop_place_quays stop_place_quays_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_quays
    ADD CONSTRAINT stop_place_quays_pkey PRIMARY KEY (stop_place_id, quays_id);


--
-- Name: tag tag_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (netex_reference, name);


--
-- Name: tariff_zone_key_values tariff_zone_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.tariff_zone_key_values
    ADD CONSTRAINT tariff_zone_key_values_pkey PRIMARY KEY (tariff_zone_id, key_values_key);


--
-- Name: tariff_zone tariff_zone_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.tariff_zone
    ADD CONSTRAINT tariff_zone_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: tariff_zone tariff_zone_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.tariff_zone
    ADD CONSTRAINT tariff_zone_pkey PRIMARY KEY (id);


--
-- Name: topographic_place_key_values topographic_place_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.topographic_place_key_values
    ADD CONSTRAINT topographic_place_key_values_pkey PRIMARY KEY (topographic_place_id, key_values_key);


--
-- Name: topographic_place topographic_place_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.topographic_place
    ADD CONSTRAINT topographic_place_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: topographic_place topographic_place_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.topographic_place
    ADD CONSTRAINT topographic_place_pkey PRIMARY KEY (id);


--
-- Name: access_space_equipment_places uk_15g5ep156j0s0m3dmh1by6dof; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space_equipment_places
    ADD CONSTRAINT uk_15g5ep156j0s0m3dmh1by6dof UNIQUE (equipment_places_id);


--
-- Name: quay_check_constraints uk_1tirlnmtpwtd5i69kn8hy05v6; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay_check_constraints
    ADD CONSTRAINT uk_1tirlnmtpwtd5i69kn8hy05v6 UNIQUE (check_constraints_id);


--
-- Name: parking_area_check_constraints uk_1vh5s3bg8ag28aip9fbx1l32r; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area_check_constraints
    ADD CONSTRAINT uk_1vh5s3bg8ag28aip9fbx1l32r UNIQUE (check_constraints_id);


--
-- Name: boarding_position_alternative_names uk_250rbh3vi00fvoca1dqy5dnwa; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position_alternative_names
    ADD CONSTRAINT uk_250rbh3vi00fvoca1dqy5dnwa UNIQUE (alternative_names_id);


--
-- Name: quay_equipment_places uk_2kygsfeskolk0dcv3580xknh4; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay_equipment_places
    ADD CONSTRAINT uk_2kygsfeskolk0dcv3580xknh4 UNIQUE (equipment_places_id);


--
-- Name: stop_place_alternative_names uk_2mabhvrur7dd4xuqf7be5tq6h; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_alternative_names
    ADD CONSTRAINT uk_2mabhvrur7dd4xuqf7be5tq6h UNIQUE (alternative_names_id);


--
-- Name: parking_properties_spaces uk_2rhu1u10q5achulke0kwg4e0o; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_properties_spaces
    ADD CONSTRAINT uk_2rhu1u10q5achulke0kwg4e0o UNIQUE (spaces_id);


--
-- Name: path_link uk_32gcphapatrdd3ol1iia6j1pa; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_link
    ADD CONSTRAINT uk_32gcphapatrdd3ol1iia6j1pa UNIQUE (to_id);


--
-- Name: access_space_check_constraints uk_35wb7oemdnk85n1hg680228tv; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space_check_constraints
    ADD CONSTRAINT uk_35wb7oemdnk85n1hg680228tv UNIQUE (check_constraints_id);


--
-- Name: path_link uk_3l2j5yhck3afg33m8tg5vrn1p; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_link
    ADD CONSTRAINT uk_3l2j5yhck3afg33m8tg5vrn1p UNIQUE (from_id);


--
-- Name: level_key_values uk_4eghmku46yje2lg3f1u6p949e; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.level_key_values
    ADD CONSTRAINT uk_4eghmku46yje2lg3f1u6p949e UNIQUE (key_values_id);


--
-- Name: stop_place_key_values uk_54aj7c8yuc5751x4c7qly6e5t; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_key_values
    ADD CONSTRAINT uk_54aj7c8yuc5751x4c7qly6e5t UNIQUE (key_values_id);


--
-- Name: parking_parking_areas uk_66npakygxb5mjymo8x06yf9sj; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_parking_areas
    ADD CONSTRAINT uk_66npakygxb5mjymo8x06yf9sj UNIQUE (parking_areas_id);


--
-- Name: quay_alternative_names uk_6h2bs7xhqq2ca64hjpp8can1w; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay_alternative_names
    ADD CONSTRAINT uk_6h2bs7xhqq2ca64hjpp8can1w UNIQUE (alternative_names_id);


--
-- Name: path_junction_key_values uk_8au15celles62v9ug5bvq2t4x; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_junction_key_values
    ADD CONSTRAINT uk_8au15celles62v9ug5bvq2t4x UNIQUE (key_values_id);


--
-- Name: group_of_stop_places_alternative_names uk_8fntgjff3lat9y850c17pwkwv; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_stop_places_alternative_names
    ADD CONSTRAINT uk_8fntgjff3lat9y850c17pwkwv UNIQUE (alternative_names_id);


--
-- Name: parking_equipment_places uk_9sg6v3vst7yq7nvli7tt317wg; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_equipment_places
    ADD CONSTRAINT uk_9sg6v3vst7yq7nvli7tt317wg UNIQUE (equipment_places_id);


--
-- Name: equipment_place_equipment_positions uk_a3yu015il8xu4ty68idmk8csl; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.equipment_place_equipment_positions
    ADD CONSTRAINT uk_a3yu015il8xu4ty68idmk8csl UNIQUE (equipment_positions_id);


--
-- Name: accessibility_assessment_limitations uk_aeu5728ehva06k95lioaubr8s; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.accessibility_assessment_limitations
    ADD CONSTRAINT uk_aeu5728ehva06k95lioaubr8s UNIQUE (limitations_id);


--
-- Name: stop_place_quays uk_f684i92mysvn6hqigs0j3m2nr; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_quays
    ADD CONSTRAINT uk_f684i92mysvn6hqigs0j3m2nr UNIQUE (quays_id);


--
-- Name: equipment_place_key_values uk_fyyde9f6a3dq1436v1wykpur2; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.equipment_place_key_values
    ADD CONSTRAINT uk_fyyde9f6a3dq1436v1wykpur2 UNIQUE (key_values_id);


--
-- Name: boarding_position_equipment_places uk_gq09mcv5i3kkrwltbnj3120j5; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position_equipment_places
    ADD CONSTRAINT uk_gq09mcv5i3kkrwltbnj3120j5 UNIQUE (equipment_places_id);


--
-- Name: check_constraint_key_values uk_gsegfx5ipotsd45aqbmq7kux0; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.check_constraint_key_values
    ADD CONSTRAINT uk_gsegfx5ipotsd45aqbmq7kux0 UNIQUE (key_values_id);


--
-- Name: parking_area_alternative_names uk_hb8tvxumnj3j12b5i3a161lcm; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area_alternative_names
    ADD CONSTRAINT uk_hb8tvxumnj3j12b5i3a161lcm UNIQUE (alternative_names_id);


--
-- Name: group_of_stop_places_key_values uk_hnagmxgtptqid1jyv5ri75vxd; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_stop_places_key_values
    ADD CONSTRAINT uk_hnagmxgtptqid1jyv5ri75vxd UNIQUE (key_values_id);


--
-- Name: equipment_position_key_values uk_hw9nq847b38qyxa25ide9ltyy; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.equipment_position_key_values
    ADD CONSTRAINT uk_hw9nq847b38qyxa25ide9ltyy UNIQUE (key_values_id);


--
-- Name: parking_key_values uk_iteh0to4gqim61p74lq2ugc2k; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_key_values
    ADD CONSTRAINT uk_iteh0to4gqim61p74lq2ugc2k UNIQUE (key_values_id);


--
-- Name: parking_parking_properties uk_j9vtca7vmg7ee8588wdseipvv; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_parking_properties
    ADD CONSTRAINT uk_j9vtca7vmg7ee8588wdseipvv UNIQUE (parking_properties_id);


--
-- Name: boarding_position_key_values uk_jilhh4jbyloqka3r1xpv88lpb; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position_key_values
    ADD CONSTRAINT uk_jilhh4jbyloqka3r1xpv88lpb UNIQUE (key_values_id);


--
-- Name: access_space_key_values uk_kcsgl47aba68824kjdceo60ql; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space_key_values
    ADD CONSTRAINT uk_kcsgl47aba68824kjdceo60ql UNIQUE (key_values_id);


--
-- Name: stop_place_children uk_kj0a7ruk5k2bub2028nbkqwtw; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_children
    ADD CONSTRAINT uk_kj0a7ruk5k2bub2028nbkqwtw UNIQUE (children_id);


--
-- Name: path_link_key_values uk_kn4m9f3l3gdgyg7mdus6qd1r1; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_link_key_values
    ADD CONSTRAINT uk_kn4m9f3l3gdgyg7mdus6qd1r1 UNIQUE (key_values_id);


--
-- Name: parking_area_equipment_places uk_lpu10934dkewquqflehpo95ye; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area_equipment_places
    ADD CONSTRAINT uk_lpu10934dkewquqflehpo95ye UNIQUE (equipment_places_id);


--
-- Name: quay_boarding_positions uk_lx6ql0b834b5l0agvouh1w860; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay_boarding_positions
    ADD CONSTRAINT uk_lx6ql0b834b5l0agvouh1w860 UNIQUE (boarding_positions_id);


--
-- Name: stop_place_equipment_places uk_mnksrduwpe1bfxskob1pkbi28; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_equipment_places
    ADD CONSTRAINT uk_mnksrduwpe1bfxskob1pkbi28 UNIQUE (equipment_places_id);


--
-- Name: tariff_zone_key_values uk_n3n61qrmgry87uoc7sho0nphm; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.tariff_zone_key_values
    ADD CONSTRAINT uk_n3n61qrmgry87uoc7sho0nphm UNIQUE (key_values_id);


--
-- Name: fare_zone_key_values uk_oujfy4iyi1cf3fdquir6jsn0n; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.fare_zone_key_values
    ADD CONSTRAINT uk_oujfy4iyi1cf3fdquir6jsn0n UNIQUE (key_values_id);


--
-- Name: purpose_of_grouping_key_values uk_p9w2a2o9k9l0wkrw6cipmthfk; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.purpose_of_grouping_key_values
    ADD CONSTRAINT uk_p9w2a2o9k9l0wkrw6cipmthfk UNIQUE (key_values_id);


--
-- Name: boarding_position_check_constraints uk_pcbtfcjcauaikel1s4uqjfldp; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position_check_constraints
    ADD CONSTRAINT uk_pcbtfcjcauaikel1s4uqjfldp UNIQUE (check_constraints_id);


--
-- Name: group_of_tariff_zones_key_values uk_pfy12mpgyt1qevehecnwh5vq2; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_tariff_zones_key_values
    ADD CONSTRAINT uk_pfy12mpgyt1qevehecnwh5vq2 UNIQUE (key_values_id);


--
-- Name: quay_key_values uk_plgcx1aoolr4vngts8ifkrse6; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay_key_values
    ADD CONSTRAINT uk_plgcx1aoolr4vngts8ifkrse6 UNIQUE (key_values_id);


--
-- Name: access_space_alternative_names uk_qvw904jxmey0b5c2oenaks4o6; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space_alternative_names
    ADD CONSTRAINT uk_qvw904jxmey0b5c2oenaks4o6 UNIQUE (alternative_names_id);


--
-- Name: parking_alternative_names uk_rlf4rns9qabhhdins8l3y89fo; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_alternative_names
    ADD CONSTRAINT uk_rlf4rns9qabhhdins8l3y89fo UNIQUE (alternative_names_id);


--
-- Name: parking_area_key_values uk_rxv53i59u1pf70kxtdchlxird; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area_key_values
    ADD CONSTRAINT uk_rxv53i59u1pf70kxtdchlxird UNIQUE (key_values_id);


--
-- Name: installed_equipment_version_structure_installed_equipment uk_s4px36fd2jutbf4p8lagcocbd; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.installed_equipment_version_structure_installed_equipment
    ADD CONSTRAINT uk_s4px36fd2jutbf4p8lagcocbd UNIQUE (installed_equipment_id);


--
-- Name: stop_place_access_spaces uk_stiis59w04hmptq2wkpsfjpb8; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_access_spaces
    ADD CONSTRAINT uk_stiis59w04hmptq2wkpsfjpb8 UNIQUE (access_spaces_id);


--
-- Name: topographic_place_key_values uk_tq5dgj811w1k4w86m4x66iwso; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.topographic_place_key_values
    ADD CONSTRAINT uk_tq5dgj811w1k4w86m4x66iwso UNIQUE (key_values_id);


--
-- Name: value value_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.value
    ADD CONSTRAINT value_pkey PRIMARY KEY (id);


--
-- Name: group_of_stop_places_centroid_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX group_of_stop_places_centroid_index ON public.group_of_stop_places USING gist (centroid);


--
-- Name: group_of_stop_places_name_value_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX group_of_stop_places_name_value_index ON public.group_of_stop_places USING btree (name_value);


--
-- Name: group_of_stop_places_netex_id_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX group_of_stop_places_netex_id_index ON public.group_of_stop_places USING btree (netex_id);


--
-- Name: group_of_stop_places_trgm_idx; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX group_of_stop_places_trgm_idx ON public.group_of_stop_places USING gist (name_value public.gist_trgm_ops);


--
-- Name: group_of_stop_places_version_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX group_of_stop_places_version_index ON public.group_of_stop_places USING btree (version);


--
-- Name: id_value_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX id_value_index ON public.id_generator USING btree (id_value);


--
-- Name: idx_accessibility_assessment_id; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX idx_accessibility_assessment_id ON public.accessibility_assessment_limitations USING btree (accessibility_assessment_id);


--
-- Name: idx_fk_place_equipment_id; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX idx_fk_place_equipment_id ON public.installed_equipment_version_structure_installed_equipment USING btree (place_equipment_id);


--
-- Name: idx_stop_place_id; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX idx_stop_place_id ON public.stop_place_tariff_zones USING btree (stop_place_id);


--
-- Name: items_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX items_index ON public.value_items USING btree (items);


--
-- Name: items_trgm_gin_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX items_trgm_gin_index ON public.value_items USING gin (items public.gin_trgm_ops);


--
-- Name: lower_case_stop_place_name_value; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX lower_case_stop_place_name_value ON public.stop_place USING btree (lower((name_value)::text));


--
-- Name: netex_reference_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX netex_reference_index ON public.tag USING btree (netex_reference);


--
-- Name: parent_site_ref_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX parent_site_ref_index ON public.stop_place USING btree (parent_site_ref);


--
-- Name: parent_site_ref_version_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX parent_site_ref_version_index ON public.stop_place USING btree (parent_site_ref_version);


--
-- Name: persistable_polygon_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX persistable_polygon_index ON public.persistable_polygon USING gist (polygon);


--
-- Name: purpose_of_grouping_name_value_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX purpose_of_grouping_name_value_index ON public.purpose_of_grouping USING btree (name_value);


--
-- Name: quay_netex_id_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX quay_netex_id_index ON public.quay USING btree (netex_id);


--
-- Name: quay_version_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX quay_version_index ON public.quay USING btree (version);


--
-- Name: schema_version_s_idx; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX schema_version_s_idx ON public.schema_version USING btree (success);


--
-- Name: stop_modification_enumeration_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX stop_modification_enumeration_index ON public.stop_place USING btree (modification_enumeration);


--
-- Name: stop_place_centroid_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX stop_place_centroid_index ON public.stop_place USING gist (centroid);


--
-- Name: stop_place_name_value_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX stop_place_name_value_index ON public.stop_place USING btree (name_value);


--
-- Name: stop_place_netex_id_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX stop_place_netex_id_index ON public.stop_place USING btree (netex_id);


--
-- Name: stop_place_type_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX stop_place_type_index ON public.stop_place USING btree (stop_place_type);


--
-- Name: stop_place_version_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX stop_place_version_index ON public.stop_place USING btree (version);


--
-- Name: table_name_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX table_name_index ON public.id_generator USING btree (table_name);


--
-- Name: topographic_place_name_value_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX topographic_place_name_value_index ON public.topographic_place USING btree (name_value);


--
-- Name: topographic_place_type_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX topographic_place_type_index ON public.topographic_place USING btree (topographic_place_type);


--
-- Name: trgm_idx; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX trgm_idx ON public.stop_place USING gist (name_value public.gist_trgm_ops);


--
-- Name: trgm_topographic_place_name_value_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX trgm_topographic_place_name_value_index ON public.topographic_place USING gist (name_value public.gist_trgm_ops);


--
-- Name: value_id_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX value_id_index ON public.value_items USING btree (value_id);


--
-- Name: quay fk10uxphnmebvjuua8n3erlo1n1; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay
    ADD CONSTRAINT fk10uxphnmebvjuua8n3erlo1n1 FOREIGN KEY (accessibility_assessment_id) REFERENCES public.accessibility_assessment(id);


--
-- Name: parking_parking_areas fk117owm5bl41inj5bxrwp84awc; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_parking_areas
    ADD CONSTRAINT fk117owm5bl41inj5bxrwp84awc FOREIGN KEY (parking_areas_id) REFERENCES public.parking_area(id);


--
-- Name: access_space fk1fidaattdqbcu4jlypwcf4p2m; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space
    ADD CONSTRAINT fk1fidaattdqbcu4jlypwcf4p2m FOREIGN KEY (polygon_id) REFERENCES public.persistable_polygon(id);


--
-- Name: stop_place_quays fk22h93cna6b2o9o8vybqb1i9qb; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_quays
    ADD CONSTRAINT fk22h93cna6b2o9o8vybqb1i9qb FOREIGN KEY (stop_place_id) REFERENCES public.stop_place(id);


--
-- Name: quay_check_constraints fk2a38aoc67evygl1e6xk0iybta; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay_check_constraints
    ADD CONSTRAINT fk2a38aoc67evygl1e6xk0iybta FOREIGN KEY (check_constraints_id) REFERENCES public.check_constraint(id);


--
-- Name: boarding_position_equipment_places fk2afgri1bcaay1etgl7sw5wljq; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position_equipment_places
    ADD CONSTRAINT fk2afgri1bcaay1etgl7sw5wljq FOREIGN KEY (equipment_places_id) REFERENCES public.equipment_place(id);


--
-- Name: equipment_place_key_values fk2hxfx966yjfx66s7e0rwkc807; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.equipment_place_key_values
    ADD CONSTRAINT fk2hxfx966yjfx66s7e0rwkc807 FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: parking_area_equipment_places fk32yrc89194bun5bwjy1u6pan4; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area_equipment_places
    ADD CONSTRAINT fk32yrc89194bun5bwjy1u6pan4 FOREIGN KEY (equipment_places_id) REFERENCES public.equipment_place(id);


--
-- Name: stop_place_alternative_names fk38wmuiuq889ldydpbyrybc7od; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_alternative_names
    ADD CONSTRAINT fk38wmuiuq889ldydpbyrybc7od FOREIGN KEY (alternative_names_id) REFERENCES public.alternative_name(id);


--
-- Name: stop_place_tariff_zones fk3j2paa5yrolwcuvpsx15jx9x9; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_tariff_zones
    ADD CONSTRAINT fk3j2paa5yrolwcuvpsx15jx9x9 FOREIGN KEY (stop_place_id) REFERENCES public.stop_place(id);


--
-- Name: boarding_position_alternative_names fk3kk76shxnjbca405imdodyx5x; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position_alternative_names
    ADD CONSTRAINT fk3kk76shxnjbca405imdodyx5x FOREIGN KEY (boarding_position_id) REFERENCES public.boarding_position(id);


--
-- Name: group_of_tariff_zones_key_values fk3m6kr7mcvitox5adi5vqmuvpc; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_tariff_zones_key_values
    ADD CONSTRAINT fk3m6kr7mcvitox5adi5vqmuvpc FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: parking_properties_spaces fk3whnrr5j2addxg4vv3vnx4x4e; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_properties_spaces
    ADD CONSTRAINT fk3whnrr5j2addxg4vv3vnx4x4e FOREIGN KEY (parking_properties_id) REFERENCES public.parking_properties(id);


--
-- Name: path_junction_key_values fk4cgeli3mja440oxomtpvjed6t; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_junction_key_values
    ADD CONSTRAINT fk4cgeli3mja440oxomtpvjed6t FOREIGN KEY (path_junction_id) REFERENCES public.path_junction(id);


--
-- Name: stop_place_children fk4erbijiihg4wb6qblaolq3kwn; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_children
    ADD CONSTRAINT fk4erbijiihg4wb6qblaolq3kwn FOREIGN KEY (children_id) REFERENCES public.stop_place(id);


--
-- Name: check_constraint_key_values fk4sgvgvx1dy8kvkyhvmpb4b0u5; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.check_constraint_key_values
    ADD CONSTRAINT fk4sgvgvx1dy8kvkyhvmpb4b0u5 FOREIGN KEY (check_constraint_id) REFERENCES public.check_constraint(id);


--
-- Name: quay_equipment_places fk543bcymfury929rotvx4qsvjp; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay_equipment_places
    ADD CONSTRAINT fk543bcymfury929rotvx4qsvjp FOREIGN KEY (quay_id) REFERENCES public.quay(id);


--
-- Name: stop_place_equipment_places fk5fxel42imvy86cikp0d27uubw; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_equipment_places
    ADD CONSTRAINT fk5fxel42imvy86cikp0d27uubw FOREIGN KEY (stop_place_id) REFERENCES public.stop_place(id);


--
-- Name: stop_place_alternative_names fk5h4utprl88fnjm48xnwhuly5q; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_alternative_names
    ADD CONSTRAINT fk5h4utprl88fnjm48xnwhuly5q FOREIGN KEY (stop_place_id) REFERENCES public.stop_place(id);


--
-- Name: quay fk5pa1d6xv2ad8gd33gcubl1dhb; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay
    ADD CONSTRAINT fk5pa1d6xv2ad8gd33gcubl1dhb FOREIGN KEY (place_equipments_id) REFERENCES public.installed_equipment_version_structure(id);


--
-- Name: fare_zone_members fk5pcf1db54ci2k7cnbcam6b2ye; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.fare_zone_members
    ADD CONSTRAINT fk5pcf1db54ci2k7cnbcam6b2ye FOREIGN KEY (fare_zone_id) REFERENCES public.fare_zone(id);


--
-- Name: boarding_position fk698fd5rwmie70j91ngq0jfau2; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position
    ADD CONSTRAINT fk698fd5rwmie70j91ngq0jfau2 FOREIGN KEY (accessibility_assessment_id) REFERENCES public.accessibility_assessment(id);


--
-- Name: boarding_position_key_values fk6pdfundcf9ro8reay93b03l4f; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position_key_values
    ADD CONSTRAINT fk6pdfundcf9ro8reay93b03l4f FOREIGN KEY (boarding_position_id) REFERENCES public.boarding_position(id);


--
-- Name: stop_place_adjacent_sites fk6ss4r72m0i6twg6u2okgo51e8; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_adjacent_sites
    ADD CONSTRAINT fk6ss4r72m0i6twg6u2okgo51e8 FOREIGN KEY (stop_place_id) REFERENCES public.stop_place(id);


--
-- Name: stop_place_key_values fk6v8qe1uxjok2wrexhprfusrpy; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_key_values
    ADD CONSTRAINT fk6v8qe1uxjok2wrexhprfusrpy FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: stop_place_equipment_places fk6ywi2d8ytfi5m3e5emowdysen; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_equipment_places
    ADD CONSTRAINT fk6ywi2d8ytfi5m3e5emowdysen FOREIGN KEY (equipment_places_id) REFERENCES public.equipment_place(id);


--
-- Name: parking_parking_properties fk70urag9nyyejkivm7bodubg0l; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_parking_properties
    ADD CONSTRAINT fk70urag9nyyejkivm7bodubg0l FOREIGN KEY (parking_id) REFERENCES public.parking(id);


--
-- Name: accessibility_assessment_limitations fk71lv2d2xdl6il9t8lxhiw2oxr; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.accessibility_assessment_limitations
    ADD CONSTRAINT fk71lv2d2xdl6il9t8lxhiw2oxr FOREIGN KEY (limitations_id) REFERENCES public.accessibility_limitation(id);


--
-- Name: path_junction_key_values fk79i9vyw1kl8vv6qt5t5ag37ib; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_junction_key_values
    ADD CONSTRAINT fk79i9vyw1kl8vv6qt5t5ag37ib FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: parking_alternative_names fk7l8e26etcpfmcd63bqvioh7bt; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_alternative_names
    ADD CONSTRAINT fk7l8e26etcpfmcd63bqvioh7bt FOREIGN KEY (alternative_names_id) REFERENCES public.alternative_name(id);


--
-- Name: group_of_tariff_zones_members fk7sff4g5u4o584lwkrc1hiqsfa; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_tariff_zones_members
    ADD CONSTRAINT fk7sff4g5u4o584lwkrc1hiqsfa FOREIGN KEY (group_of_tariff_zones_id) REFERENCES public.group_of_tariff_zones(id);


--
-- Name: parking_area_alternative_names fk83bhdbmy03cgn1v7vfm2hc25g; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area_alternative_names
    ADD CONSTRAINT fk83bhdbmy03cgn1v7vfm2hc25g FOREIGN KEY (alternative_names_id) REFERENCES public.alternative_name(id);


--
-- Name: purpose_of_grouping_key_values fk8d9tkmg5hupfrrogc1rcisl9e; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.purpose_of_grouping_key_values
    ADD CONSTRAINT fk8d9tkmg5hupfrrogc1rcisl9e FOREIGN KEY (purpose_of_grouping_id) REFERENCES public.purpose_of_grouping(id);


--
-- Name: level fk8h6xjcugey2fg2dhix15v4lsm; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.level
    ADD CONSTRAINT fk8h6xjcugey2fg2dhix15v4lsm FOREIGN KEY (description_id) REFERENCES public.multilingual_string_entity(id);


--
-- Name: level fk8icvbp3tjonuijcbajsgdvkjp; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.level
    ADD CONSTRAINT fk8icvbp3tjonuijcbajsgdvkjp FOREIGN KEY (name_id) REFERENCES public.multilingual_string_entity(id);


--
-- Name: tariff_zone_key_values fk8ulw0u13r8x9tytsrvo6tr8ak; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.tariff_zone_key_values
    ADD CONSTRAINT fk8ulw0u13r8x9tytsrvo6tr8ak FOREIGN KEY (tariff_zone_id) REFERENCES public.tariff_zone(id);


--
-- Name: parking_adjacent_sites fk96ene7wgwsid8skgjsnlexmb7; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_adjacent_sites
    ADD CONSTRAINT fk96ene7wgwsid8skgjsnlexmb7 FOREIGN KEY (parking_id) REFERENCES public.parking(id);


--
-- Name: parking fk96p5d5t8n4r0mqxjhkwktud4; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking
    ADD CONSTRAINT fk96p5d5t8n4r0mqxjhkwktud4 FOREIGN KEY (polygon_id) REFERENCES public.persistable_polygon(id);


--
-- Name: level_key_values fk99i09lflmm3dtvdh479gybs5g; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.level_key_values
    ADD CONSTRAINT fk99i09lflmm3dtvdh479gybs5g FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: purpose_of_grouping_key_values fk9d5elf8qr9f7tg4t3jjljocsl; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.purpose_of_grouping_key_values
    ADD CONSTRAINT fk9d5elf8qr9f7tg4t3jjljocsl FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: path_link_key_values fk9qwypjgswmctp5fn2tahr0l7o; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_link_key_values
    ADD CONSTRAINT fk9qwypjgswmctp5fn2tahr0l7o FOREIGN KEY (path_link_id) REFERENCES public.path_link(id);


--
-- Name: stop_place_access_spaces fk9v547jr8nnbcfvv4tsi229kxn; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_access_spaces
    ADD CONSTRAINT fk9v547jr8nnbcfvv4tsi229kxn FOREIGN KEY (access_spaces_id) REFERENCES public.access_space(id);


--
-- Name: fare_zone_key_values fk9vxr65yfa7q8wuw48dquyha9y; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.fare_zone_key_values
    ADD CONSTRAINT fk9vxr65yfa7q8wuw48dquyha9y FOREIGN KEY (fare_zone_id) REFERENCES public.fare_zone(id);


--
-- Name: parking_key_values fk9y6amcojpo70vg2ydp3p5il9s; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_key_values
    ADD CONSTRAINT fk9y6amcojpo70vg2ydp3p5il9s FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: parking_area fka20x6t18riu82sahee5mn0g7n; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area
    ADD CONSTRAINT fka20x6t18riu82sahee5mn0g7n FOREIGN KEY (parking_properties_id) REFERENCES public.parking_properties(id);


--
-- Name: check_constraint_key_values fka88lssnbb1kj1s052nckd6dn4; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.check_constraint_key_values
    ADD CONSTRAINT fka88lssnbb1kj1s052nckd6dn4 FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: equipment_position_key_values fka9k2btoq1hylsxgdc2r4y7c6h; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.equipment_position_key_values
    ADD CONSTRAINT fka9k2btoq1hylsxgdc2r4y7c6h FOREIGN KEY (equipment_position_id) REFERENCES public.equipment_position(id);


--
-- Name: equipment_place_equipment_positions fkabblwcd7yqvqcueubgds9g79p; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.equipment_place_equipment_positions
    ADD CONSTRAINT fkabblwcd7yqvqcueubgds9g79p FOREIGN KEY (equipment_positions_id) REFERENCES public.equipment_position(id);


--
-- Name: access_space_equipment_places fkahpl2sexrde229vbbli7grrp4; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space_equipment_places
    ADD CONSTRAINT fkahpl2sexrde229vbbli7grrp4 FOREIGN KEY (access_space_id) REFERENCES public.access_space(id);


--
-- Name: equipment_position_key_values fkb9i1yjga0o91xdhcb3rvpvkx9; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.equipment_position_key_values
    ADD CONSTRAINT fkb9i1yjga0o91xdhcb3rvpvkx9 FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: quay_key_values fkc18wd399ytds57bsuuip2pl41; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay_key_values
    ADD CONSTRAINT fkc18wd399ytds57bsuuip2pl41 FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: topographic_place_key_values fkc3wle51cxccfkwwkpiu2ekkim; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.topographic_place_key_values
    ADD CONSTRAINT fkc3wle51cxccfkwwkpiu2ekkim FOREIGN KEY (topographic_place_id) REFERENCES public.topographic_place(id);


--
-- Name: quay_alternative_names fkc51iijml1n53m15a3o1uytbxv; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay_alternative_names
    ADD CONSTRAINT fkc51iijml1n53m15a3o1uytbxv FOREIGN KEY (alternative_names_id) REFERENCES public.alternative_name(id);


--
-- Name: parking_properties_spaces fkc6bu1c3cyaxa6w9s096rurn7b; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_properties_spaces
    ADD CONSTRAINT fkc6bu1c3cyaxa6w9s096rurn7b FOREIGN KEY (spaces_id) REFERENCES public.parking_capacity(id);


--
-- Name: stop_place_access_spaces fkcfh3y91gw4ulh0x6tohnevsxt; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_access_spaces
    ADD CONSTRAINT fkcfh3y91gw4ulh0x6tohnevsxt FOREIGN KEY (stop_place_id) REFERENCES public.stop_place(id);


--
-- Name: parking_key_values fkchgifjy5ltx55l2riu92p8rah; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_key_values
    ADD CONSTRAINT fkchgifjy5ltx55l2riu92p8rah FOREIGN KEY (parking_id) REFERENCES public.parking(id);


--
-- Name: boarding_position fkckcw3qglsese2680ss59lq9yy; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position
    ADD CONSTRAINT fkckcw3qglsese2680ss59lq9yy FOREIGN KEY (place_equipments_id) REFERENCES public.installed_equipment_version_structure(id);


--
-- Name: access_space_check_constraints fkd0f8scim513pkmar0rrtgmvk0; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space_check_constraints
    ADD CONSTRAINT fkd0f8scim513pkmar0rrtgmvk0 FOREIGN KEY (check_constraints_id) REFERENCES public.check_constraint(id);


--
-- Name: group_of_stop_places fkd732x7a9ap1djb2me5ee3khmm; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_stop_places
    ADD CONSTRAINT fkd732x7a9ap1djb2me5ee3khmm FOREIGN KEY (purpose_of_grouping_id) REFERENCES public.purpose_of_grouping(id);


--
-- Name: check_constraint fkd7kx2g2kfknuq180s9psrxheg; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.check_constraint
    ADD CONSTRAINT fkd7kx2g2kfknuq180s9psrxheg FOREIGN KEY (description_id) REFERENCES public.multilingual_string_entity(id);


--
-- Name: quay_boarding_positions fkddggv25j677uyu93kjcejrkoy; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay_boarding_positions
    ADD CONSTRAINT fkddggv25j677uyu93kjcejrkoy FOREIGN KEY (quay_id) REFERENCES public.quay(id);


--
-- Name: installed_equipment_version_structure_installed_equipment fkdjkqt8xtfm3betwint02yr8ul; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.installed_equipment_version_structure_installed_equipment
    ADD CONSTRAINT fkdjkqt8xtfm3betwint02yr8ul FOREIGN KEY (installed_equipment_id) REFERENCES public.installed_equipment_version_structure(id);


--
-- Name: access_space fkdkxpwi2gsjyno21hnp5vxjcqx; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space
    ADD CONSTRAINT fkdkxpwi2gsjyno21hnp5vxjcqx FOREIGN KEY (accessibility_assessment_id) REFERENCES public.accessibility_assessment(id);


--
-- Name: group_of_tariff_zones_key_values fkdxsb7togrx2wrcyi8wkyas0hn; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_tariff_zones_key_values
    ADD CONSTRAINT fkdxsb7togrx2wrcyi8wkyas0hn FOREIGN KEY (group_of_tariff_zones_id) REFERENCES public.group_of_tariff_zones(id);


--
-- Name: quay_key_values fke06xofaj85jd2715l5wvgcewf; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay_key_values
    ADD CONSTRAINT fke06xofaj85jd2715l5wvgcewf FOREIGN KEY (quay_id) REFERENCES public.quay(id);


--
-- Name: boarding_position_check_constraints fke3k0ye5cmkahhxwrarv52sxgg; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position_check_constraints
    ADD CONSTRAINT fke3k0ye5cmkahhxwrarv52sxgg FOREIGN KEY (boarding_position_id) REFERENCES public.boarding_position(id);


--
-- Name: destination_display_view fke4decqec3uijjvob9y6nm1m4n; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.destination_display_view
    ADD CONSTRAINT fke4decqec3uijjvob9y6nm1m4n FOREIGN KEY (name_id) REFERENCES public.multilingual_string_entity(id);


--
-- Name: quay_equipment_places fkeaggpvk1rtleplvo725msboob; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay_equipment_places
    ADD CONSTRAINT fkeaggpvk1rtleplvo725msboob FOREIGN KEY (equipment_places_id) REFERENCES public.equipment_place(id);


--
-- Name: parking_area_alternative_names fked2qtdvpkkrp456ttsr04fdtd; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area_alternative_names
    ADD CONSTRAINT fked2qtdvpkkrp456ttsr04fdtd FOREIGN KEY (parking_area_id) REFERENCES public.parking_area(id);


--
-- Name: boarding_position_alternative_names fkeo5gxbxqw4nujvef4rbqlupjo; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position_alternative_names
    ADD CONSTRAINT fkeo5gxbxqw4nujvef4rbqlupjo FOREIGN KEY (alternative_names_id) REFERENCES public.alternative_name(id);


--
-- Name: boarding_position_check_constraints fkexpjw5brnj2x19mke33ahb4v1; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position_check_constraints
    ADD CONSTRAINT fkexpjw5brnj2x19mke33ahb4v1 FOREIGN KEY (check_constraints_id) REFERENCES public.check_constraint(id);


--
-- Name: tariff_zone_key_values fkf0utp1a034yhbyt80kv99lu84; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.tariff_zone_key_values
    ADD CONSTRAINT fkf0utp1a034yhbyt80kv99lu84 FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: parking_area_key_values fkfavrlym6c13ftqqhybqdypaa0; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area_key_values
    ADD CONSTRAINT fkfavrlym6c13ftqqhybqdypaa0 FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: stop_place fkfb9cw77oshl15ax3v7o4x5ndb; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place
    ADD CONSTRAINT fkfb9cw77oshl15ax3v7o4x5ndb FOREIGN KEY (topographic_place_id) REFERENCES public.topographic_place(id);


--
-- Name: topographic_place_key_values fkfdntaoibnwcm1tjita53n8wp5; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.topographic_place_key_values
    ADD CONSTRAINT fkfdntaoibnwcm1tjita53n8wp5 FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: access_space_alternative_names fkfrvjdit8u3jfuwrfjj5kv7ej8; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space_alternative_names
    ADD CONSTRAINT fkfrvjdit8u3jfuwrfjj5kv7ej8 FOREIGN KEY (access_space_id) REFERENCES public.access_space(id);


--
-- Name: equipment_position fkg7tekxdtcgxc9nuqg1gtbfub; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.equipment_position
    ADD CONSTRAINT fkg7tekxdtcgxc9nuqg1gtbfub FOREIGN KEY (description_id) REFERENCES public.multilingual_string_entity(id);


--
-- Name: quay fkga2n69n19frbpsm112vnv7ujp; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay
    ADD CONSTRAINT fkga2n69n19frbpsm112vnv7ujp FOREIGN KEY (polygon_id) REFERENCES public.persistable_polygon(id);


--
-- Name: access_space_alternative_names fkggy00kekruqt7vq13o0hrishg; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space_alternative_names
    ADD CONSTRAINT fkggy00kekruqt7vq13o0hrishg FOREIGN KEY (alternative_names_id) REFERENCES public.alternative_name(id);


--
-- Name: access_space_key_values fkguduxo8jkx68ewnlb9vkg6asj; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space_key_values
    ADD CONSTRAINT fkguduxo8jkx68ewnlb9vkg6asj FOREIGN KEY (access_space_id) REFERENCES public.access_space(id);


--
-- Name: parking_area fkh6h52ajwscge6qctip0056ja5; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area
    ADD CONSTRAINT fkh6h52ajwscge6qctip0056ja5 FOREIGN KEY (accessibility_assessment_id) REFERENCES public.accessibility_assessment(id);


--
-- Name: access_space_equipment_places fkhvudykavfk476h6yvqbe8uke7; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space_equipment_places
    ADD CONSTRAINT fkhvudykavfk476h6yvqbe8uke7 FOREIGN KEY (equipment_places_id) REFERENCES public.equipment_place(id);


--
-- Name: path_link_key_values fki5ewaofkg7dqyrfxmv05up23k; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_link_key_values
    ADD CONSTRAINT fki5ewaofkg7dqyrfxmv05up23k FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: topographic_place fkin7vu25nr46n8tufgnrjdjpna; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.topographic_place
    ADD CONSTRAINT fkin7vu25nr46n8tufgnrjdjpna FOREIGN KEY (polygon_id) REFERENCES public.persistable_polygon(id);


--
-- Name: parking_equipment_places fkjdjk7xlyh5j4wwkdj7b55wwvr; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_equipment_places
    ADD CONSTRAINT fkjdjk7xlyh5j4wwkdj7b55wwvr FOREIGN KEY (parking_id) REFERENCES public.parking(id);


--
-- Name: parking fkjdu5ypd58h2k87dgguvfx904d; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking
    ADD CONSTRAINT fkjdu5ypd58h2k87dgguvfx904d FOREIGN KEY (place_equipments_id) REFERENCES public.installed_equipment_version_structure(id);


--
-- Name: equipment_place fkji5u4pfe7mk8cb9m02xj9mydb; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.equipment_place
    ADD CONSTRAINT fkji5u4pfe7mk8cb9m02xj9mydb FOREIGN KEY (polygon_id) REFERENCES public.persistable_polygon(id);


--
-- Name: check_constraint fkjyh851p5h9hcg8fftuejisrbh; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.check_constraint
    ADD CONSTRAINT fkjyh851p5h9hcg8fftuejisrbh FOREIGN KEY (name_id) REFERENCES public.multilingual_string_entity(id);


--
-- Name: stop_place fkk40r06hkuvmp9bn3nqh3hc72p; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place
    ADD CONSTRAINT fkk40r06hkuvmp9bn3nqh3hc72p FOREIGN KEY (place_equipments_id) REFERENCES public.installed_equipment_version_structure(id);


--
-- Name: access_space fkkaorr2thiqfib1tp2u8hy7qjj; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space
    ADD CONSTRAINT fkkaorr2thiqfib1tp2u8hy7qjj FOREIGN KEY (place_equipments_id) REFERENCES public.installed_equipment_version_structure(id);


--
-- Name: quay_boarding_positions fkkclc1cagtjsudx3cw7lseovcg; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay_boarding_positions
    ADD CONSTRAINT fkkclc1cagtjsudx3cw7lseovcg FOREIGN KEY (boarding_positions_id) REFERENCES public.boarding_position(id);


--
-- Name: accessibility_assessment_limitations fkkghye5kl3gcgb4446yva0hqib; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.accessibility_assessment_limitations
    ADD CONSTRAINT fkkghye5kl3gcgb4446yva0hqib FOREIGN KEY (accessibility_assessment_id) REFERENCES public.accessibility_assessment(id);


--
-- Name: parking_parking_areas fklfn6esk2xmgy0abtbkxleg94h; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_parking_areas
    ADD CONSTRAINT fklfn6esk2xmgy0abtbkxleg94h FOREIGN KEY (parking_id) REFERENCES public.parking(id);


--
-- Name: level fkliqsellqidqhi01xnad6d009h; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.level
    ADD CONSTRAINT fkliqsellqidqhi01xnad6d009h FOREIGN KEY (short_name_id) REFERENCES public.multilingual_string_entity(id);


--
-- Name: stop_place_children fklljrfiip140imskbef529knwo; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_children
    ADD CONSTRAINT fklljrfiip140imskbef529knwo FOREIGN KEY (stop_place_id) REFERENCES public.stop_place(id);


--
-- Name: fare_zone_neighbours fklwjm9qu9fghulu2k425gl996x; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.fare_zone_neighbours
    ADD CONSTRAINT fklwjm9qu9fghulu2k425gl996x FOREIGN KEY (fare_zone_id) REFERENCES public.fare_zone(id);


--
-- Name: parking_area_equipment_places fklyjyw910bdsck4ia3cu5u02b7; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area_equipment_places
    ADD CONSTRAINT fklyjyw910bdsck4ia3cu5u02b7 FOREIGN KEY (parking_area_id) REFERENCES public.parking_area(id);


--
-- Name: quay_check_constraints fkm3br6dbr6lfeirt677vgwhbkt; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay_check_constraints
    ADD CONSTRAINT fkm3br6dbr6lfeirt677vgwhbkt FOREIGN KEY (quay_id) REFERENCES public.quay(id);


--
-- Name: path_link fkmdata7200nfbkhwfiwbdr89gx; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_link
    ADD CONSTRAINT fkmdata7200nfbkhwfiwbdr89gx FOREIGN KEY (from_id) REFERENCES public.path_link_end(id);


--
-- Name: parking_parking_vehicle_types fkmdeyc63w7rq4oahia4ekbhpu9; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_parking_vehicle_types
    ADD CONSTRAINT fkmdeyc63w7rq4oahia4ekbhpu9 FOREIGN KEY (parking_id) REFERENCES public.parking(id);


--
-- Name: installed_equipment_version_structure_installed_equipment fkmjjjgsh44ditp528hx9nynkby; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.installed_equipment_version_structure_installed_equipment
    ADD CONSTRAINT fkmjjjgsh44ditp528hx9nynkby FOREIGN KEY (place_equipment_id) REFERENCES public.installed_equipment_version_structure(id);


--
-- Name: equipment_place_equipment_positions fkn4tdt95gfsvqido4rq1mjb8h8; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.equipment_place_equipment_positions
    ADD CONSTRAINT fkn4tdt95gfsvqido4rq1mjb8h8 FOREIGN KEY (equipment_place_id) REFERENCES public.equipment_place(id);


--
-- Name: boarding_position fkndqqqjgyacwmv60e3qhsnkbhb; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position
    ADD CONSTRAINT fkndqqqjgyacwmv60e3qhsnkbhb FOREIGN KEY (polygon_id) REFERENCES public.persistable_polygon(id);


--
-- Name: equipment_place_key_values fkns6x8o4fyw73gopona4fiihdu; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.equipment_place_key_values
    ADD CONSTRAINT fkns6x8o4fyw73gopona4fiihdu FOREIGN KEY (equipment_place_id) REFERENCES public.equipment_place(id);


--
-- Name: value_items fknuulrwd9o0m7ocvcntkig5csj; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.value_items
    ADD CONSTRAINT fknuulrwd9o0m7ocvcntkig5csj FOREIGN KEY (value_id) REFERENCES public.value(id);


--
-- Name: quay_alternative_names fknvsb6xd4x2jkguqx9hfnfsy74; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.quay_alternative_names
    ADD CONSTRAINT fknvsb6xd4x2jkguqx9hfnfsy74 FOREIGN KEY (quay_id) REFERENCES public.quay(id);


--
-- Name: parking_alternative_names fknx3vhtx356nwc4fwg1dct6ic3; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_alternative_names
    ADD CONSTRAINT fknx3vhtx356nwc4fwg1dct6ic3 FOREIGN KEY (parking_id) REFERENCES public.parking(id);


--
-- Name: parking_area_check_constraints fkny2owjxs13whgj345oo4ghxco; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area_check_constraints
    ADD CONSTRAINT fkny2owjxs13whgj345oo4ghxco FOREIGN KEY (check_constraints_id) REFERENCES public.check_constraint(id);


--
-- Name: fare_zone_key_values fko1fg9cqlm88y8sn6a4wl4oawx; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.fare_zone_key_values
    ADD CONSTRAINT fko1fg9cqlm88y8sn6a4wl4oawx FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: parking_properties_parking_user_types fko1j5k835d8tdh5x4ldjkvnjfv; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_properties_parking_user_types
    ADD CONSTRAINT fko1j5k835d8tdh5x4ldjkvnjfv FOREIGN KEY (parking_properties_id) REFERENCES public.parking_properties(id);


--
-- Name: parking_area_check_constraints fko9rxbvt9bqk35ggadptkxsrna; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area_check_constraints
    ADD CONSTRAINT fko9rxbvt9bqk35ggadptkxsrna FOREIGN KEY (parking_area_id) REFERENCES public.parking_area(id);


--
-- Name: access_space_check_constraints fkob3boul2jifb3vekl11asof4v; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space_check_constraints
    ADD CONSTRAINT fkob3boul2jifb3vekl11asof4v FOREIGN KEY (access_space_id) REFERENCES public.access_space(id);


--
-- Name: parking_area_key_values fkoj56x7qga6tilll3mor88hsru; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area_key_values
    ADD CONSTRAINT fkoj56x7qga6tilll3mor88hsru FOREIGN KEY (parking_area_id) REFERENCES public.parking_area(id);


--
-- Name: stop_place_key_values fkolek3mod8n2ncbfyp6t9nj1qh; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_key_values
    ADD CONSTRAINT fkolek3mod8n2ncbfyp6t9nj1qh FOREIGN KEY (stop_place_id) REFERENCES public.stop_place(id);


--
-- Name: parking fkpbhyj09qvbw33cmw9wshgsg7y; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking
    ADD CONSTRAINT fkpbhyj09qvbw33cmw9wshgsg7y FOREIGN KEY (topographic_place_id) REFERENCES public.topographic_place(id);


--
-- Name: boarding_position_key_values fkqd4jmt7qmq7ecblajntgrae11; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position_key_values
    ADD CONSTRAINT fkqd4jmt7qmq7ecblajntgrae11 FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: level_key_values fkql9va96wfvxdtrsar93qoqapf; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.level_key_values
    ADD CONSTRAINT fkql9va96wfvxdtrsar93qoqapf FOREIGN KEY (level_id) REFERENCES public.level(id);


--
-- Name: fare_zone fkqqohh30c6mjxumolbl34epvuv; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.fare_zone
    ADD CONSTRAINT fkqqohh30c6mjxumolbl34epvuv FOREIGN KEY (polygon_id) REFERENCES public.persistable_polygon(id);


--
-- Name: stop_place_quays fkr5tlsd2as2q238g2phacwql72; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place_quays
    ADD CONSTRAINT fkr5tlsd2as2q238g2phacwql72 FOREIGN KEY (quays_id) REFERENCES public.quay(id);


--
-- Name: parking_parking_properties fkr8c26brhce4cxkxse6hsxb4ph; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_parking_properties
    ADD CONSTRAINT fkr8c26brhce4cxkxse6hsxb4ph FOREIGN KEY (parking_properties_id) REFERENCES public.parking_properties(id);


--
-- Name: parking fkrksfqb92ty4tpgf0aegaijlal; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking
    ADD CONSTRAINT fkrksfqb92ty4tpgf0aegaijlal FOREIGN KEY (accessibility_assessment_id) REFERENCES public.accessibility_assessment(id);


--
-- Name: parking_area fkrksrhan2eax7wt85lu0khpuqn; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area
    ADD CONSTRAINT fkrksrhan2eax7wt85lu0khpuqn FOREIGN KEY (place_equipments_id) REFERENCES public.installed_equipment_version_structure(id);


--
-- Name: parking_area fkrm29xbq5804fmta4ttiki0qql; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_area
    ADD CONSTRAINT fkrm29xbq5804fmta4ttiki0qql FOREIGN KEY (polygon_id) REFERENCES public.persistable_polygon(id);


--
-- Name: access_space_key_values fks5ltc1fbi2mbo1arc7ttr5u7t; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.access_space_key_values
    ADD CONSTRAINT fks5ltc1fbi2mbo1arc7ttr5u7t FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: tariff_zone fksiof3uxjddw8koviv4omgimwc; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.tariff_zone
    ADD CONSTRAINT fksiof3uxjddw8koviv4omgimwc FOREIGN KEY (polygon_id) REFERENCES public.persistable_polygon(id);


--
-- Name: path_link fksyksujopo02932so13dt18a3n; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_link
    ADD CONSTRAINT fksyksujopo02932so13dt18a3n FOREIGN KEY (to_id) REFERENCES public.path_link_end(id);


--
-- Name: path_link_end fkt1jkprb71o8k98r1k9gl9767c; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.path_link_end
    ADD CONSTRAINT fkt1jkprb71o8k98r1k9gl9767c FOREIGN KEY (path_junction_id) REFERENCES public.path_junction(id);


--
-- Name: boarding_position_equipment_places fkt6b6123yefqwpog332fptv4wc; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.boarding_position_equipment_places
    ADD CONSTRAINT fkt6b6123yefqwpog332fptv4wc FOREIGN KEY (boarding_position_id) REFERENCES public.boarding_position(id);


--
-- Name: stop_place fkt75b2x29642ei9s99c7pue6h5; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place
    ADD CONSTRAINT fkt75b2x29642ei9s99c7pue6h5 FOREIGN KEY (polygon_id) REFERENCES public.persistable_polygon(id);


--
-- Name: stop_place fktmqm9d5a1fuxiivaxmjluis6g; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.stop_place
    ADD CONSTRAINT fktmqm9d5a1fuxiivaxmjluis6g FOREIGN KEY (accessibility_assessment_id) REFERENCES public.accessibility_assessment(id);


--
-- Name: parking_equipment_places fkwgs9h00yk2uvhgsstupeyxii; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_equipment_places
    ADD CONSTRAINT fkwgs9h00yk2uvhgsstupeyxii FOREIGN KEY (equipment_places_id) REFERENCES public.equipment_place(id);


--
-- Name: group_of_stop_places_alternative_names group_of_stop_places_alternative_names_alternative_names_id_fke; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_stop_places_alternative_names
    ADD CONSTRAINT group_of_stop_places_alternative_names_alternative_names_id_fke FOREIGN KEY (alternative_names_id) REFERENCES public.alternative_name(id);


--
-- Name: group_of_stop_places_alternative_names group_of_stop_places_alternative_names_group_of_stop_places_id_; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_stop_places_alternative_names
    ADD CONSTRAINT group_of_stop_places_alternative_names_group_of_stop_places_id_ FOREIGN KEY (group_of_stop_places_id) REFERENCES public.group_of_stop_places(id);


--
-- Name: group_of_stop_places_key_values group_of_stop_places_key_values_group_of_stop_places_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_stop_places_key_values
    ADD CONSTRAINT group_of_stop_places_key_values_group_of_stop_places_id_fkey FOREIGN KEY (group_of_stop_places_id) REFERENCES public.group_of_stop_places(id);


--
-- Name: group_of_stop_places_key_values group_of_stop_places_key_values_key_values_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_stop_places_key_values
    ADD CONSTRAINT group_of_stop_places_key_values_key_values_id_fkey FOREIGN KEY (key_values_id) REFERENCES public.value(id);


--
-- Name: group_of_stop_places_members group_of_stop_places_members_group_of_stop_places_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.group_of_stop_places_members
    ADD CONSTRAINT group_of_stop_places_members_group_of_stop_places_id_fkey FOREIGN KEY (group_of_stop_places_id) REFERENCES public.group_of_stop_places(id);


--
-- Name: parking_parking_payment_process parking_parking_payment_process_fk; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY public.parking_parking_payment_process
    ADD CONSTRAINT parking_parking_payment_process_fk FOREIGN KEY (parking_id) REFERENCES public.parking(id);


--
-- PostgreSQL database dump complete
--

