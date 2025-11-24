--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.1
-- Dumped by pg_dump version 9.6.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: topology; Type: SCHEMA; Schema: -; Owner: tiamat
--

CREATE SCHEMA IF NOT EXISTS topology;


ALTER SCHEMA topology OWNER TO tiamat;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: pg_trgm; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA public;


--
-- Name: EXTENSION pg_trgm; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pg_trgm IS 'text similarity measurement and index searching based on trigrams';


--
-- Name: postgis; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;


--
-- Name: EXTENSION postgis; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis IS 'PostGIS geometry, geography, and raster spatial types and functions';


--
-- Name: postgis_topology; Type: EXTENSION; Schema: -; Owner: 
--

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: access_space; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE access_space (
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
    all_areas_wheelchair_accessible boolean,
    covered integer,
    level_ref character varying(255),
    level_ref_version character varying(255),
    site_ref character varying(255),
    site_ref_version character varying(255),
    lang character varying(5),
    value character varying(255),
    polygon_id bigint,
    accessibility_assessment_id bigint,
    place_equipments_id bigint,
    changed_by character varying(255)
);


ALTER TABLE access_space OWNER TO tiamat;

--
-- Name: access_space_alternative_names; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE access_space_alternative_names (
    access_space_id bigint NOT NULL,
    alternative_names_id bigint NOT NULL
);


ALTER TABLE access_space_alternative_names OWNER TO tiamat;

--
-- Name: access_space_check_constraints; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE access_space_check_constraints (
    access_space_id bigint NOT NULL,
    check_constraints_id bigint NOT NULL
);


ALTER TABLE access_space_check_constraints OWNER TO tiamat;

--
-- Name: access_space_equipment_places; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE access_space_equipment_places (
    access_space_id bigint NOT NULL,
    equipment_places_id bigint NOT NULL
);


ALTER TABLE access_space_equipment_places OWNER TO tiamat;

--
-- Name: access_space_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE access_space_key_values (
    access_space_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE access_space_key_values OWNER TO tiamat;

--
-- Name: access_space_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE access_space_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE access_space_seq OWNER TO tiamat;

--
-- Name: access_space_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE access_space_valid_betweens (
    access_space_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE access_space_valid_betweens OWNER TO tiamat;

--
-- Name: accesses_rel_structure; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE accesses_rel_structure (
    id character varying(255) NOT NULL
);


ALTER TABLE accesses_rel_structure OWNER TO tiamat;

--
-- Name: accesses_rel_structure_access_ref_or_access; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE accesses_rel_structure_access_ref_or_access (
    accesses_rel_structure_id character varying(255) NOT NULL,
    access_ref_or_access bytea
);


ALTER TABLE accesses_rel_structure_access_ref_or_access OWNER TO tiamat;

--
-- Name: accesses_rel_structure_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE accesses_rel_structure_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE accesses_rel_structure_seq OWNER TO tiamat;

--
-- Name: accessibility_assessment; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE accessibility_assessment (
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


ALTER TABLE accessibility_assessment OWNER TO tiamat;

--
-- Name: accessibility_assessment_limitations; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE accessibility_assessment_limitations (
    accessibility_assessment_id bigint NOT NULL,
    limitations_id bigint NOT NULL
);


ALTER TABLE accessibility_assessment_limitations OWNER TO tiamat;

--
-- Name: accessibility_assessment_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE accessibility_assessment_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE accessibility_assessment_seq OWNER TO tiamat;

--
-- Name: accessibility_assessment_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE accessibility_assessment_valid_betweens (
    accessibility_assessment_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE accessibility_assessment_valid_betweens OWNER TO tiamat;

--
-- Name: accessibility_limitation; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE accessibility_limitation (
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


ALTER TABLE accessibility_limitation OWNER TO tiamat;

--
-- Name: accessibility_limitation_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE accessibility_limitation_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE accessibility_limitation_seq OWNER TO tiamat;

--
-- Name: accessibility_limitation_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE accessibility_limitation_valid_betweens (
    accessibility_limitation_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE accessibility_limitation_valid_betweens OWNER TO tiamat;

--
-- Name: alternative_name; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE alternative_name (
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
    name_type integer,
    named_object_ref bytea,
    qualifier_name_lang character varying(255),
    qualifier_name_value character varying(255),
    short_name_lang character varying(255),
    short_name_value character varying(255),
    type_of_name character varying(255),
    changed_by character varying(255)
);


ALTER TABLE alternative_name OWNER TO tiamat;

--
-- Name: alternative_name_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE alternative_name_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE alternative_name_seq OWNER TO tiamat;

--
-- Name: alternative_name_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE alternative_name_valid_betweens (
    alternative_name_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE alternative_name_valid_betweens OWNER TO tiamat;

--
-- Name: boarding_position; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE boarding_position (
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
    all_areas_wheelchair_accessible boolean,
    covered integer,
    level_ref character varying(255),
    level_ref_version character varying(255),
    site_ref character varying(255),
    site_ref_version character varying(255),
    lang character varying(5),
    value character varying(255),
    boarding_position_type character varying(255),
    public_code character varying(255),
    polygon_id bigint,
    accessibility_assessment_id bigint,
    place_equipments_id bigint,
    changed_by character varying(255)
);


ALTER TABLE boarding_position OWNER TO tiamat;

--
-- Name: boarding_position_alternative_names; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE boarding_position_alternative_names (
    boarding_position_id bigint NOT NULL,
    alternative_names_id bigint NOT NULL
);


ALTER TABLE boarding_position_alternative_names OWNER TO tiamat;

--
-- Name: boarding_position_check_constraints; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE boarding_position_check_constraints (
    boarding_position_id bigint NOT NULL,
    check_constraints_id bigint NOT NULL
);


ALTER TABLE boarding_position_check_constraints OWNER TO tiamat;

--
-- Name: boarding_position_equipment_places; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE boarding_position_equipment_places (
    boarding_position_id bigint NOT NULL,
    equipment_places_id bigint NOT NULL
);


ALTER TABLE boarding_position_equipment_places OWNER TO tiamat;

--
-- Name: boarding_position_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE boarding_position_key_values (
    boarding_position_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE boarding_position_key_values OWNER TO tiamat;

--
-- Name: boarding_position_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE boarding_position_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE boarding_position_seq OWNER TO tiamat;

--
-- Name: boarding_position_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE boarding_position_valid_betweens (
    boarding_position_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE boarding_position_valid_betweens OWNER TO tiamat;

--
-- Name: check_constraint; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE check_constraint (
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


ALTER TABLE check_constraint OWNER TO tiamat;

--
-- Name: check_constraint_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE check_constraint_key_values (
    check_constraint_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE check_constraint_key_values OWNER TO tiamat;

--
-- Name: check_constraint_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE check_constraint_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE check_constraint_seq OWNER TO tiamat;

--
-- Name: check_constraint_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE check_constraint_valid_betweens (
    check_constraint_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE check_constraint_valid_betweens OWNER TO tiamat;

--
-- Name: destination_display_view; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE destination_display_view (
    id bigint NOT NULL,
    branding_ref bytea,
    public_code character varying(255),
    short_code character varying(255),
    name_id bigint
);


ALTER TABLE destination_display_view OWNER TO tiamat;

--
-- Name: equipment_place; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE equipment_place (
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
    changed_by character varying(255)
);


ALTER TABLE equipment_place OWNER TO tiamat;

--
-- Name: equipment_place_equipment_positions; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE equipment_place_equipment_positions (
    equipment_place_id bigint NOT NULL,
    equipment_positions_id bigint NOT NULL
);


ALTER TABLE equipment_place_equipment_positions OWNER TO tiamat;

--
-- Name: equipment_place_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE equipment_place_key_values (
    equipment_place_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE equipment_place_key_values OWNER TO tiamat;

--
-- Name: equipment_place_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE equipment_place_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_place_seq OWNER TO tiamat;

--
-- Name: equipment_place_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE equipment_place_valid_betweens (
    equipment_place_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE equipment_place_valid_betweens OWNER TO tiamat;

--
-- Name: equipment_position; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE equipment_position (
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


ALTER TABLE equipment_position OWNER TO tiamat;

--
-- Name: equipment_position_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE equipment_position_key_values (
    equipment_position_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE equipment_position_key_values OWNER TO tiamat;

--
-- Name: equipment_position_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE equipment_position_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_position_seq OWNER TO tiamat;

--
-- Name: equipment_position_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE equipment_position_valid_betweens (
    equipment_position_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE equipment_position_valid_betweens OWNER TO tiamat;

--
-- Name: explicit_equipments_rel_structure; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE explicit_equipments_rel_structure (
    id character varying(255) NOT NULL
);


ALTER TABLE explicit_equipments_rel_structure OWNER TO tiamat;

--
-- Name: explicit_equipments_rel_structure_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE explicit_equipments_rel_structure_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE explicit_equipments_rel_structure_seq OWNER TO tiamat;

--
-- Name: export_job; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE export_job (
    id bigint NOT NULL,
    file_name character varying(255),
    finished timestamp without time zone,
    job_url character varying(255),
    message character varying(255),
    started timestamp without time zone,
    status integer
);


ALTER TABLE export_job OWNER TO tiamat;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE hibernate_sequence OWNER TO tiamat;

--
-- Name: id_generator; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE id_generator (
    table_name character varying(50),
    id_value bigint
);


ALTER TABLE id_generator OWNER TO tiamat;

--
-- Name: installed_equipment; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE installed_equipment (
    id character varying(255) NOT NULL,
    item_type character varying(255),
    equipment_id integer NOT NULL
);


ALTER TABLE installed_equipment OWNER TO tiamat;

--
-- Name: installed_equipment_version_structure; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE installed_equipment_version_structure (
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


ALTER TABLE installed_equipment_version_structure OWNER TO tiamat;

--
-- Name: installed_equipment_version_structure_installed_equipment; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE installed_equipment_version_structure_installed_equipment (
    place_equipment_id bigint NOT NULL,
    installed_equipment_id bigint NOT NULL
);


ALTER TABLE installed_equipment_version_structure_installed_equipment OWNER TO tiamat;

--
-- Name: installed_equipment_version_structure_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE installed_equipment_version_structure_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE installed_equipment_version_structure_seq OWNER TO tiamat;

--
-- Name: installed_equipment_version_structure_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE installed_equipment_version_structure_valid_betweens (
    installed_equipment_version_structure_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE installed_equipment_version_structure_valid_betweens OWNER TO tiamat;

--
-- Name: level; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE level (
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


ALTER TABLE level OWNER TO tiamat;

--
-- Name: level_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE level_key_values (
    level_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE level_key_values OWNER TO tiamat;

--
-- Name: level_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE level_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE level_seq OWNER TO tiamat;

--
-- Name: level_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE level_valid_betweens (
    level_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE level_valid_betweens OWNER TO tiamat;

--
-- Name: multilingual_string_entity; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE multilingual_string_entity (
    id bigint NOT NULL,
    lang character varying(5),
    value character varying(255)
);


ALTER TABLE multilingual_string_entity OWNER TO tiamat;

--
-- Name: navigation_path; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE navigation_path (
    id character varying(255) NOT NULL,
    item_type character varying(255),
    path_id integer NOT NULL
);


ALTER TABLE navigation_path OWNER TO tiamat;

--
-- Name: navigation_paths_rel_structure; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE navigation_paths_rel_structure (
    id character varying(255) NOT NULL
);


ALTER TABLE navigation_paths_rel_structure OWNER TO tiamat;

--
-- Name: navigation_paths_rel_structure_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE navigation_paths_rel_structure_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE navigation_paths_rel_structure_seq OWNER TO tiamat;

--
-- Name: parking; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking (
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


ALTER TABLE parking OWNER TO tiamat;

--
-- Name: parking_alternative_names; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_alternative_names (
    parking_id bigint NOT NULL,
    alternative_names_id bigint NOT NULL
);


ALTER TABLE parking_alternative_names OWNER TO tiamat;

--
-- Name: parking_area; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_area (
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


ALTER TABLE parking_area OWNER TO tiamat;

--
-- Name: parking_area_alternative_names; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_area_alternative_names (
    parking_area_id bigint NOT NULL,
    alternative_names_id bigint NOT NULL
);


ALTER TABLE parking_area_alternative_names OWNER TO tiamat;

--
-- Name: parking_area_check_constraints; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_area_check_constraints (
    parking_area_id bigint NOT NULL,
    check_constraints_id bigint NOT NULL
);


ALTER TABLE parking_area_check_constraints OWNER TO tiamat;

--
-- Name: parking_area_equipment_places; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_area_equipment_places (
    parking_area_id bigint NOT NULL,
    equipment_places_id bigint NOT NULL
);


ALTER TABLE parking_area_equipment_places OWNER TO tiamat;

--
-- Name: parking_area_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_area_key_values (
    parking_area_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE parking_area_key_values OWNER TO tiamat;

--
-- Name: parking_area_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE parking_area_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE parking_area_seq OWNER TO tiamat;

--
-- Name: parking_area_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_area_valid_betweens (
    parking_area_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE parking_area_valid_betweens OWNER TO tiamat;

--
-- Name: parking_capacity; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_capacity (
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
    changed_by character varying(255)
);


ALTER TABLE parking_capacity OWNER TO tiamat;

--
-- Name: parking_capacity_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE parking_capacity_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE parking_capacity_seq OWNER TO tiamat;

--
-- Name: parking_capacity_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_capacity_valid_betweens (
    parking_capacity_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE parking_capacity_valid_betweens OWNER TO tiamat;

--
-- Name: parking_equipment_places; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_equipment_places (
    parking_id bigint NOT NULL,
    equipment_places_id bigint NOT NULL
);


ALTER TABLE parking_equipment_places OWNER TO tiamat;

--
-- Name: parking_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_key_values (
    parking_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE parking_key_values OWNER TO tiamat;

--
-- Name: parking_parking_areas; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_parking_areas (
    parking_id bigint NOT NULL,
    parking_areas_id bigint NOT NULL
);


ALTER TABLE parking_parking_areas OWNER TO tiamat;

--
-- Name: parking_parking_properties; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_parking_properties (
    parking_id bigint NOT NULL,
    parking_properties_id bigint NOT NULL
);


ALTER TABLE parking_parking_properties OWNER TO tiamat;

--
-- Name: parking_parking_vehicle_types; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_parking_vehicle_types (
    parking_id bigint NOT NULL,
    parking_vehicle_types character varying(255)
);


ALTER TABLE parking_parking_vehicle_types OWNER TO tiamat;

--
-- Name: parking_properties; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_properties (
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


ALTER TABLE parking_properties OWNER TO tiamat;

--
-- Name: parking_properties_parking_user_types; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_properties_parking_user_types (
    parking_properties_id bigint NOT NULL,
    parking_user_types character varying(255)
);


ALTER TABLE parking_properties_parking_user_types OWNER TO tiamat;

--
-- Name: parking_properties_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE parking_properties_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE parking_properties_seq OWNER TO tiamat;

--
-- Name: parking_properties_spaces; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_properties_spaces (
    parking_properties_id bigint NOT NULL,
    spaces_id bigint NOT NULL
);


ALTER TABLE parking_properties_spaces OWNER TO tiamat;

--
-- Name: parking_properties_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_properties_valid_betweens (
    parking_properties_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE parking_properties_valid_betweens OWNER TO tiamat;

--
-- Name: parking_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE parking_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE parking_seq OWNER TO tiamat;

--
-- Name: parking_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE parking_valid_betweens (
    parking_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE parking_valid_betweens OWNER TO tiamat;

--
-- Name: path_junction; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE path_junction (
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


ALTER TABLE path_junction OWNER TO tiamat;

--
-- Name: path_junction_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE path_junction_key_values (
    path_junction_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE path_junction_key_values OWNER TO tiamat;

--
-- Name: path_junction_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE path_junction_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE path_junction_seq OWNER TO tiamat;

--
-- Name: path_junction_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE path_junction_valid_betweens (
    path_junction_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE path_junction_valid_betweens OWNER TO tiamat;

--
-- Name: path_junctions_rel_structure; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE path_junctions_rel_structure (
    id character varying(255) NOT NULL
);


ALTER TABLE path_junctions_rel_structure OWNER TO tiamat;

--
-- Name: path_junctions_rel_structure_path_junction_ref_or_path_junction; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE path_junctions_rel_structure_path_junction_ref_or_path_junction (
    path_junctions_rel_structure_id character varying(255) NOT NULL,
    path_junction_ref_or_path_junction bytea
);


ALTER TABLE path_junctions_rel_structure_path_junction_ref_or_path_junction OWNER TO tiamat;

--
-- Name: path_junctions_rel_structure_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE path_junctions_rel_structure_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE path_junctions_rel_structure_seq OWNER TO tiamat;

--
-- Name: path_link; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE path_link (
    id bigint NOT NULL,
    netex_id character varying(255),
    changed timestamp without time zone,
    created timestamp without time zone,
    from_date timestamp without time zone,
    to_date timestamp without time zone,
    version bigint NOT NULL,
    version_comment character varying(255),
    line_string geometry,
    default_duration bytea,
    frequent_traveller_duration bytea,
    mobility_restricted_traveller_duration bytea,
    occasional_traveller_duration bytea,
    from_id bigint,
    to_id bigint,
    changed_by character varying(255)
);


ALTER TABLE path_link OWNER TO tiamat;

--
-- Name: path_link_end; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE path_link_end (
    id bigint NOT NULL,
    netex_id character varying(255),
    place_ref character varying(255),
    place_version character varying(255),
    path_junction_id bigint
);


ALTER TABLE path_link_end OWNER TO tiamat;

--
-- Name: path_link_end_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE path_link_end_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE path_link_end_seq OWNER TO tiamat;

--
-- Name: path_link_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE path_link_key_values (
    path_link_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE path_link_key_values OWNER TO tiamat;

--
-- Name: path_link_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE path_link_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE path_link_seq OWNER TO tiamat;

--
-- Name: path_link_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE path_link_valid_betweens (
    path_link_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE path_link_valid_betweens OWNER TO tiamat;

--
-- Name: persistable_polygon; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE persistable_polygon (
    id bigint NOT NULL,
    polygon geometry
);


ALTER TABLE persistable_polygon OWNER TO tiamat;

--
-- Name: persistable_polygon_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE persistable_polygon_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE persistable_polygon_seq OWNER TO tiamat;

--
-- Name: quay; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE quay (
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
    all_areas_wheelchair_accessible boolean,
    covered integer,
    level_ref character varying(255),
    level_ref_version character varying(255),
    site_ref character varying(255),
    site_ref_version character varying(255),
    lang character varying(5),
    value character varying(255),
    compass_bearing real,
    public_code character varying(255),
    polygon_id bigint,
    accessibility_assessment_id bigint,
    place_equipments_id bigint,
    changed_by character varying(255)
);


ALTER TABLE quay OWNER TO tiamat;

--
-- Name: quay_alternative_names; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE quay_alternative_names (
    quay_id bigint NOT NULL,
    alternative_names_id bigint NOT NULL
);


ALTER TABLE quay_alternative_names OWNER TO tiamat;

--
-- Name: quay_boarding_positions; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE quay_boarding_positions (
    quay_id bigint NOT NULL,
    boarding_positions_id bigint NOT NULL
);


ALTER TABLE quay_boarding_positions OWNER TO tiamat;

--
-- Name: quay_check_constraints; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE quay_check_constraints (
    quay_id bigint NOT NULL,
    check_constraints_id bigint NOT NULL
);


ALTER TABLE quay_check_constraints OWNER TO tiamat;

--
-- Name: quay_equipment_places; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE quay_equipment_places (
    quay_id bigint NOT NULL,
    equipment_places_id bigint NOT NULL
);


ALTER TABLE quay_equipment_places OWNER TO tiamat;

--
-- Name: quay_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE quay_key_values (
    quay_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE quay_key_values OWNER TO tiamat;

--
-- Name: quay_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE quay_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE quay_seq OWNER TO tiamat;

--
-- Name: quay_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE quay_valid_betweens (
    quay_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE quay_valid_betweens OWNER TO tiamat;

--
-- Name: quays_rel_structure; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE quays_rel_structure (
    id character varying(255) NOT NULL
);


ALTER TABLE quays_rel_structure OWNER TO tiamat;

--
-- Name: quays_rel_structure_quay_ref_or_quay; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE quays_rel_structure_quay_ref_or_quay (
    quays_rel_structure_id character varying(255) NOT NULL,
    quay_ref_or_quay_id bigint NOT NULL
);


ALTER TABLE quays_rel_structure_quay_ref_or_quay OWNER TO tiamat;

--
-- Name: quays_rel_structure_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE quays_rel_structure_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE quays_rel_structure_seq OWNER TO tiamat;

--
-- Name: road_address; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE road_address (
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
    bearing_compass character varying(255),
    bearing_degrees numeric(19,2),
    gis_feature_ref character varying(255),
    road_number character varying(255),
    polygon_id bigint,
    road_name_id bigint,
    changed_by character varying(255)
);


ALTER TABLE road_address OWNER TO tiamat;

--
-- Name: road_address_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE road_address_key_values (
    road_address_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE road_address_key_values OWNER TO tiamat;

--
-- Name: road_address_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE road_address_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE road_address_seq OWNER TO tiamat;

--
-- Name: road_address_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE road_address_valid_betweens (
    road_address_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE road_address_valid_betweens OWNER TO tiamat;

--
-- Name: seq_multilingual_string_entity; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE seq_multilingual_string_entity
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE seq_multilingual_string_entity OWNER TO tiamat;

--
-- Name: site_path_links_rel_structure; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE site_path_links_rel_structure (
    id character varying(255) NOT NULL
);


ALTER TABLE site_path_links_rel_structure OWNER TO tiamat;

--
-- Name: site_path_links_rel_structure_path_link_ref_or_site_path_link; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE site_path_links_rel_structure_path_link_ref_or_site_path_link (
    site_path_links_rel_structure_id character varying(255) NOT NULL,
    path_link_ref_or_site_path_link bytea
);


ALTER TABLE site_path_links_rel_structure_path_link_ref_or_site_path_link OWNER TO tiamat;

--
-- Name: site_path_links_rel_structure_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE site_path_links_rel_structure_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE site_path_links_rel_structure_seq OWNER TO tiamat;

--
-- Name: stop_place; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE stop_place (
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
    changed_by character varying(255)
);


ALTER TABLE stop_place OWNER TO tiamat;

--
-- Name: stop_place_access_spaces; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE stop_place_access_spaces (
    stop_place_id bigint NOT NULL,
    access_spaces_id bigint NOT NULL
);


ALTER TABLE stop_place_access_spaces OWNER TO tiamat;

--
-- Name: stop_place_alternative_names; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE stop_place_alternative_names (
    stop_place_id bigint NOT NULL,
    alternative_names_id bigint NOT NULL
);


ALTER TABLE stop_place_alternative_names OWNER TO tiamat;

--
-- Name: stop_place_equipment_places; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE stop_place_equipment_places (
    stop_place_id bigint NOT NULL,
    equipment_places_id bigint NOT NULL
);


ALTER TABLE stop_place_equipment_places OWNER TO tiamat;

--
-- Name: stop_place_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE stop_place_key_values (
    stop_place_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE stop_place_key_values OWNER TO tiamat;

--
-- Name: stop_place_quays; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE stop_place_quays (
    stop_place_id bigint NOT NULL,
    quays_id bigint NOT NULL
);


ALTER TABLE stop_place_quays OWNER TO tiamat;

--
-- Name: stop_place_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE stop_place_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stop_place_seq OWNER TO tiamat;

--
-- Name: stop_place_tariff_zones; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE stop_place_tariff_zones (
    stop_place_id bigint NOT NULL,
    tariff_zones_id bigint NOT NULL
);


ALTER TABLE stop_place_tariff_zones OWNER TO tiamat;

--
-- Name: stop_place_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE stop_place_valid_betweens (
    stop_place_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE stop_place_valid_betweens OWNER TO tiamat;

--
-- Name: tariff_zone; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE tariff_zone (
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
    changed_by character varying(255)
);


ALTER TABLE tariff_zone OWNER TO tiamat;

--
-- Name: tariff_zone_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE tariff_zone_key_values (
    tariff_zone_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE tariff_zone_key_values OWNER TO tiamat;

--
-- Name: tariff_zone_ref; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE tariff_zone_ref (
    id bigint NOT NULL,
    ref character varying(255),
    version character varying(255)
);


ALTER TABLE tariff_zone_ref OWNER TO tiamat;

--
-- Name: tariff_zone_ref_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE tariff_zone_ref_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tariff_zone_ref_seq OWNER TO tiamat;

--
-- Name: tariff_zone_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE tariff_zone_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tariff_zone_seq OWNER TO tiamat;

--
-- Name: tariff_zone_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE tariff_zone_valid_betweens (
    tariff_zone_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE tariff_zone_valid_betweens OWNER TO tiamat;

--
-- Name: topographic_place; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE topographic_place (
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
    country_ref character varying(255),
    country_ref_value character varying(255),
    iso_code character varying(255),
    parent_ref character varying(255),
    parent_ref_version character varying(255),
    topographic_place_type character varying(255),
    polygon_id bigint,
    changed_by character varying(255)
);


ALTER TABLE topographic_place OWNER TO tiamat;

--
-- Name: topographic_place_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE topographic_place_key_values (
    topographic_place_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE topographic_place_key_values OWNER TO tiamat;

--
-- Name: topographic_place_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE topographic_place_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE topographic_place_seq OWNER TO tiamat;

--
-- Name: topographic_place_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE topographic_place_valid_betweens (
    topographic_place_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE topographic_place_valid_betweens OWNER TO tiamat;

--
-- Name: valid_between; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE valid_between (
    id bigint NOT NULL,
    netex_id character varying(255),
    changed timestamp without time zone,
    created timestamp without time zone,
    version bigint NOT NULL,
    version_comment character varying(255),
    description_lang character varying(255),
    description_value character varying(255),
    name_lang character varying(255),
    name_value character varying(255),
    from_date timestamp without time zone,
    to_date timestamp without time zone
);


ALTER TABLE valid_between OWNER TO tiamat;

--
-- Name: valid_between_key_values; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE valid_between_key_values (
    valid_between_id bigint NOT NULL,
    key_values_id bigint NOT NULL,
    key_values_key character varying(255) NOT NULL
);


ALTER TABLE valid_between_key_values OWNER TO tiamat;

--
-- Name: valid_between_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE valid_between_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE valid_between_seq OWNER TO tiamat;

--
-- Name: valid_between_valid_betweens; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE valid_between_valid_betweens (
    valid_between_id bigint NOT NULL,
    valid_betweens_id bigint NOT NULL
);


ALTER TABLE valid_between_valid_betweens OWNER TO tiamat;

--
-- Name: value; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE value (
    id bigint NOT NULL
);


ALTER TABLE value OWNER TO tiamat;

--
-- Name: value_items; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE value_items (
    value_id bigint NOT NULL,
    items character varying(255)
);


ALTER TABLE value_items OWNER TO tiamat;

--
-- Name: vehicle_stopping_places_rel_structure; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE vehicle_stopping_places_rel_structure (
    id character varying(255) NOT NULL
);


ALTER TABLE vehicle_stopping_places_rel_structure OWNER TO tiamat;

--
-- Name: vehicle_stopping_places_rel_structure_seq; Type: SEQUENCE; Schema: public; Owner: tiamat
--

CREATE SEQUENCE vehicle_stopping_places_rel_structure_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vehicle_stopping_places_rel_structure_seq OWNER TO tiamat;

--
-- Name: vehicle_stopping_places_rel_structure_vehicle_stopping_place_re; Type: TABLE; Schema: public; Owner: tiamat
--

CREATE TABLE vehicle_stopping_places_rel_structure_vehicle_stopping_place_re (
    vehicle_stopping_places_rel_structure_id character varying(255) NOT NULL,
    vehicle_stopping_place_ref_or_vehicle_stopping_place bytea
);


ALTER TABLE vehicle_stopping_places_rel_structure_vehicle_stopping_place_re OWNER TO tiamat;

--
-- Name: access_space_key_values access_space_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space_key_values
    ADD CONSTRAINT access_space_key_values_pkey PRIMARY KEY (access_space_id, key_values_key);


--
-- Name: access_space access_space_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space
    ADD CONSTRAINT access_space_pkey PRIMARY KEY (id);


--
-- Name: accesses_rel_structure accesses_rel_structure_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY accesses_rel_structure
    ADD CONSTRAINT accesses_rel_structure_pkey PRIMARY KEY (id);


--
-- Name: accessibility_assessment accessibility_assessment_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY accessibility_assessment
    ADD CONSTRAINT accessibility_assessment_pkey PRIMARY KEY (id);


--
-- Name: accessibility_limitation accessibility_limitation_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY accessibility_limitation
    ADD CONSTRAINT accessibility_limitation_pkey PRIMARY KEY (id);


--
-- Name: alternative_name alternative_name_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY alternative_name
    ADD CONSTRAINT alternative_name_pkey PRIMARY KEY (id);


--
-- Name: boarding_position_key_values boarding_position_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position_key_values
    ADD CONSTRAINT boarding_position_key_values_pkey PRIMARY KEY (boarding_position_id, key_values_key);


--
-- Name: boarding_position boarding_position_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position
    ADD CONSTRAINT boarding_position_pkey PRIMARY KEY (id);


--
-- Name: check_constraint_key_values check_constraint_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY check_constraint_key_values
    ADD CONSTRAINT check_constraint_key_values_pkey PRIMARY KEY (check_constraint_id, key_values_key);


--
-- Name: check_constraint check_constraint_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY check_constraint
    ADD CONSTRAINT check_constraint_pkey PRIMARY KEY (id);


--
-- Name: destination_display_view destination_display_view_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY destination_display_view
    ADD CONSTRAINT destination_display_view_pkey PRIMARY KEY (id);


--
-- Name: equipment_place_key_values equipment_place_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_place_key_values
    ADD CONSTRAINT equipment_place_key_values_pkey PRIMARY KEY (equipment_place_id, key_values_key);


--
-- Name: equipment_place equipment_place_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_place
    ADD CONSTRAINT equipment_place_pkey PRIMARY KEY (id);


--
-- Name: equipment_position_key_values equipment_position_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_position_key_values
    ADD CONSTRAINT equipment_position_key_values_pkey PRIMARY KEY (equipment_position_id, key_values_key);


--
-- Name: equipment_position equipment_position_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_position
    ADD CONSTRAINT equipment_position_pkey PRIMARY KEY (id);


--
-- Name: explicit_equipments_rel_structure explicit_equipments_rel_structure_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY explicit_equipments_rel_structure
    ADD CONSTRAINT explicit_equipments_rel_structure_pkey PRIMARY KEY (id);


--
-- Name: export_job export_job_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY export_job
    ADD CONSTRAINT export_job_pkey PRIMARY KEY (id);


--
-- Name: id_generator id_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY id_generator
    ADD CONSTRAINT id_constraint UNIQUE (table_name, id_value);


--
-- Name: installed_equipment_version_structure installed_equipment_version_structure_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY installed_equipment_version_structure
    ADD CONSTRAINT installed_equipment_version_structure_pkey PRIMARY KEY (id);


--
-- Name: level_key_values level_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY level_key_values
    ADD CONSTRAINT level_key_values_pkey PRIMARY KEY (level_id, key_values_key);


--
-- Name: level level_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY level
    ADD CONSTRAINT level_pkey PRIMARY KEY (id);


--
-- Name: multilingual_string_entity multilingual_string_entity_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY multilingual_string_entity
    ADD CONSTRAINT multilingual_string_entity_pkey PRIMARY KEY (id);


--
-- Name: navigation_paths_rel_structure navigation_paths_rel_structure_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY navigation_paths_rel_structure
    ADD CONSTRAINT navigation_paths_rel_structure_pkey PRIMARY KEY (id);


--
-- Name: parking_area_key_values parking_area_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area_key_values
    ADD CONSTRAINT parking_area_key_values_pkey PRIMARY KEY (parking_area_id, key_values_key);


--
-- Name: parking_area parking_area_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area
    ADD CONSTRAINT parking_area_pkey PRIMARY KEY (id);


--
-- Name: parking_capacity parking_capacity_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_capacity
    ADD CONSTRAINT parking_capacity_pkey PRIMARY KEY (id);


--
-- Name: parking_key_values parking_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_key_values
    ADD CONSTRAINT parking_key_values_pkey PRIMARY KEY (parking_id, key_values_key);


--
-- Name: parking parking_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking
    ADD CONSTRAINT parking_pkey PRIMARY KEY (id);


--
-- Name: parking_properties parking_properties_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_properties
    ADD CONSTRAINT parking_properties_pkey PRIMARY KEY (id);


--
-- Name: path_junction_key_values path_junction_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_junction_key_values
    ADD CONSTRAINT path_junction_key_values_pkey PRIMARY KEY (path_junction_id, key_values_key);


--
-- Name: path_junction path_junction_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_junction
    ADD CONSTRAINT path_junction_pkey PRIMARY KEY (id);


--
-- Name: path_junctions_rel_structure path_junctions_rel_structure_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_junctions_rel_structure
    ADD CONSTRAINT path_junctions_rel_structure_pkey PRIMARY KEY (id);


--
-- Name: path_link_end path_link_end_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_link_end
    ADD CONSTRAINT path_link_end_pkey PRIMARY KEY (id);


--
-- Name: path_link_key_values path_link_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_link_key_values
    ADD CONSTRAINT path_link_key_values_pkey PRIMARY KEY (path_link_id, key_values_key);


--
-- Name: path_link path_link_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_link
    ADD CONSTRAINT path_link_pkey PRIMARY KEY (id);


--
-- Name: persistable_polygon persistable_polygon_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY persistable_polygon
    ADD CONSTRAINT persistable_polygon_pkey PRIMARY KEY (id);


--
-- Name: quay_key_values quay_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_key_values
    ADD CONSTRAINT quay_key_values_pkey PRIMARY KEY (quay_id, key_values_key);


--
-- Name: quay quay_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay
    ADD CONSTRAINT quay_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: quay quay_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay
    ADD CONSTRAINT quay_pkey PRIMARY KEY (id);


--
-- Name: quays_rel_structure quays_rel_structure_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quays_rel_structure
    ADD CONSTRAINT quays_rel_structure_pkey PRIMARY KEY (id);


--
-- Name: road_address_key_values road_address_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY road_address_key_values
    ADD CONSTRAINT road_address_key_values_pkey PRIMARY KEY (road_address_id, key_values_key);


--
-- Name: road_address road_address_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY road_address
    ADD CONSTRAINT road_address_pkey PRIMARY KEY (id);


--
-- Name: site_path_links_rel_structure site_path_links_rel_structure_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY site_path_links_rel_structure
    ADD CONSTRAINT site_path_links_rel_structure_pkey PRIMARY KEY (id);


--
-- Name: stop_place_key_values stop_place_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_key_values
    ADD CONSTRAINT stop_place_key_values_pkey PRIMARY KEY (stop_place_id, key_values_key);


--
-- Name: stop_place stop_place_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place
    ADD CONSTRAINT stop_place_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: stop_place stop_place_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place
    ADD CONSTRAINT stop_place_pkey PRIMARY KEY (id);


--
-- Name: stop_place_quays stop_place_quays_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_quays
    ADD CONSTRAINT stop_place_quays_pkey PRIMARY KEY (stop_place_id, quays_id);


--
-- Name: stop_place_tariff_zones stop_place_tariff_zones_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_tariff_zones
    ADD CONSTRAINT stop_place_tariff_zones_pkey PRIMARY KEY (stop_place_id, tariff_zones_id);


--
-- Name: tariff_zone_key_values tariff_zone_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY tariff_zone_key_values
    ADD CONSTRAINT tariff_zone_key_values_pkey PRIMARY KEY (tariff_zone_id, key_values_key);


--
-- Name: tariff_zone tariff_zone_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY tariff_zone
    ADD CONSTRAINT tariff_zone_pkey PRIMARY KEY (id);


--
-- Name: tariff_zone_ref tariff_zone_ref_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY tariff_zone_ref
    ADD CONSTRAINT tariff_zone_ref_pkey PRIMARY KEY (id);


--
-- Name: topographic_place_key_values topographic_place_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY topographic_place_key_values
    ADD CONSTRAINT topographic_place_key_values_pkey PRIMARY KEY (topographic_place_id, key_values_key);


--
-- Name: topographic_place topographic_place_netex_id_version_constraint; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY topographic_place
    ADD CONSTRAINT topographic_place_netex_id_version_constraint UNIQUE (netex_id, version);


--
-- Name: topographic_place topographic_place_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY topographic_place
    ADD CONSTRAINT topographic_place_pkey PRIMARY KEY (id);


--
-- Name: access_space_equipment_places uk_15g5ep156j0s0m3dmh1by6dof; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space_equipment_places
    ADD CONSTRAINT uk_15g5ep156j0s0m3dmh1by6dof UNIQUE (equipment_places_id);


--
-- Name: access_space_valid_betweens uk_1dl0sl7qgx0ekaxtc4sa3yosw; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space_valid_betweens
    ADD CONSTRAINT uk_1dl0sl7qgx0ekaxtc4sa3yosw UNIQUE (valid_betweens_id);


--
-- Name: road_address_key_values uk_1np4afp1fxetn44d0f98n6tix; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY road_address_key_values
    ADD CONSTRAINT uk_1np4afp1fxetn44d0f98n6tix UNIQUE (key_values_id);


--
-- Name: quay_check_constraints uk_1tirlnmtpwtd5i69kn8hy05v6; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_check_constraints
    ADD CONSTRAINT uk_1tirlnmtpwtd5i69kn8hy05v6 UNIQUE (check_constraints_id);


--
-- Name: parking_area_check_constraints uk_1vh5s3bg8ag28aip9fbx1l32r; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area_check_constraints
    ADD CONSTRAINT uk_1vh5s3bg8ag28aip9fbx1l32r UNIQUE (check_constraints_id);


--
-- Name: boarding_position_alternative_names uk_250rbh3vi00fvoca1dqy5dnwa; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position_alternative_names
    ADD CONSTRAINT uk_250rbh3vi00fvoca1dqy5dnwa UNIQUE (alternative_names_id);


--
-- Name: quay_equipment_places uk_2kygsfeskolk0dcv3580xknh4; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_equipment_places
    ADD CONSTRAINT uk_2kygsfeskolk0dcv3580xknh4 UNIQUE (equipment_places_id);


--
-- Name: stop_place_alternative_names uk_2mabhvrur7dd4xuqf7be5tq6h; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_alternative_names
    ADD CONSTRAINT uk_2mabhvrur7dd4xuqf7be5tq6h UNIQUE (alternative_names_id);


--
-- Name: parking_properties_spaces uk_2rhu1u10q5achulke0kwg4e0o; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_properties_spaces
    ADD CONSTRAINT uk_2rhu1u10q5achulke0kwg4e0o UNIQUE (spaces_id);


--
-- Name: access_space_check_constraints uk_35wb7oemdnk85n1hg680228tv; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space_check_constraints
    ADD CONSTRAINT uk_35wb7oemdnk85n1hg680228tv UNIQUE (check_constraints_id);


--
-- Name: level_key_values uk_4eghmku46yje2lg3f1u6p949e; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY level_key_values
    ADD CONSTRAINT uk_4eghmku46yje2lg3f1u6p949e UNIQUE (key_values_id);


--
-- Name: quays_rel_structure_quay_ref_or_quay uk_53tvnkst6i59fxx7kt1rjqmlr; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quays_rel_structure_quay_ref_or_quay
    ADD CONSTRAINT uk_53tvnkst6i59fxx7kt1rjqmlr UNIQUE (quay_ref_or_quay_id);


--
-- Name: stop_place_key_values uk_54aj7c8yuc5751x4c7qly6e5t; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_key_values
    ADD CONSTRAINT uk_54aj7c8yuc5751x4c7qly6e5t UNIQUE (key_values_id);


--
-- Name: accessibility_assessment_valid_betweens uk_5uyvf8r9ck1uggsa96r0nbm3x; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY accessibility_assessment_valid_betweens
    ADD CONSTRAINT uk_5uyvf8r9ck1uggsa96r0nbm3x UNIQUE (valid_betweens_id);


--
-- Name: parking_parking_areas uk_66npakygxb5mjymo8x06yf9sj; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_parking_areas
    ADD CONSTRAINT uk_66npakygxb5mjymo8x06yf9sj UNIQUE (parking_areas_id);


--
-- Name: quay_alternative_names uk_6h2bs7xhqq2ca64hjpp8can1w; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_alternative_names
    ADD CONSTRAINT uk_6h2bs7xhqq2ca64hjpp8can1w UNIQUE (alternative_names_id);


--
-- Name: path_junction_key_values uk_8au15celles62v9ug5bvq2t4x; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_junction_key_values
    ADD CONSTRAINT uk_8au15celles62v9ug5bvq2t4x UNIQUE (key_values_id);


--
-- Name: stop_place_tariff_zones uk_8ybr6imk0qaj2qffwdcraa8uy; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_tariff_zones
    ADD CONSTRAINT uk_8ybr6imk0qaj2qffwdcraa8uy UNIQUE (tariff_zones_id);


--
-- Name: check_constraint_valid_betweens uk_98ukm2hh8fd68ubjt9f7oix9x; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY check_constraint_valid_betweens
    ADD CONSTRAINT uk_98ukm2hh8fd68ubjt9f7oix9x UNIQUE (valid_betweens_id);


--
-- Name: parking_equipment_places uk_9sg6v3vst7yq7nvli7tt317wg; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_equipment_places
    ADD CONSTRAINT uk_9sg6v3vst7yq7nvli7tt317wg UNIQUE (equipment_places_id);


--
-- Name: equipment_place_equipment_positions uk_a3yu015il8xu4ty68idmk8csl; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_place_equipment_positions
    ADD CONSTRAINT uk_a3yu015il8xu4ty68idmk8csl UNIQUE (equipment_positions_id);


--
-- Name: accessibility_assessment_limitations uk_aeu5728ehva06k95lioaubr8s; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY accessibility_assessment_limitations
    ADD CONSTRAINT uk_aeu5728ehva06k95lioaubr8s UNIQUE (limitations_id);


--
-- Name: path_junction_valid_betweens uk_bsgfke5ffpxuuh3cyvqoox24r; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_junction_valid_betweens
    ADD CONSTRAINT uk_bsgfke5ffpxuuh3cyvqoox24r UNIQUE (valid_betweens_id);


--
-- Name: tariff_zone_valid_betweens uk_d39n8acn65jct462t6p8f7srl; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY tariff_zone_valid_betweens
    ADD CONSTRAINT uk_d39n8acn65jct462t6p8f7srl UNIQUE (valid_betweens_id);


--
-- Name: quay_valid_betweens uk_dblosogjiilnish16n5xlhkhb; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_valid_betweens
    ADD CONSTRAINT uk_dblosogjiilnish16n5xlhkhb UNIQUE (valid_betweens_id);


--
-- Name: valid_between_key_values uk_de17h8ecjda6gm8uaheykevxh; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY valid_between_key_values
    ADD CONSTRAINT uk_de17h8ecjda6gm8uaheykevxh UNIQUE (key_values_id);


--
-- Name: equipment_place_valid_betweens uk_emwfqf3w17jcyme3cop0awqi1; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_place_valid_betweens
    ADD CONSTRAINT uk_emwfqf3w17jcyme3cop0awqi1 UNIQUE (valid_betweens_id);


--
-- Name: stop_place_quays uk_f684i92mysvn6hqigs0j3m2nr; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_quays
    ADD CONSTRAINT uk_f684i92mysvn6hqigs0j3m2nr UNIQUE (quays_id);


--
-- Name: equipment_place_key_values uk_fyyde9f6a3dq1436v1wykpur2; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_place_key_values
    ADD CONSTRAINT uk_fyyde9f6a3dq1436v1wykpur2 UNIQUE (key_values_id);


--
-- Name: boarding_position_valid_betweens uk_gd8o8x5d6t3wccds6mvc0k1qo; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position_valid_betweens
    ADD CONSTRAINT uk_gd8o8x5d6t3wccds6mvc0k1qo UNIQUE (valid_betweens_id);


--
-- Name: boarding_position_equipment_places uk_gq09mcv5i3kkrwltbnj3120j5; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position_equipment_places
    ADD CONSTRAINT uk_gq09mcv5i3kkrwltbnj3120j5 UNIQUE (equipment_places_id);


--
-- Name: check_constraint_key_values uk_gsegfx5ipotsd45aqbmq7kux0; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY check_constraint_key_values
    ADD CONSTRAINT uk_gsegfx5ipotsd45aqbmq7kux0 UNIQUE (key_values_id);


--
-- Name: parking_area_alternative_names uk_hb8tvxumnj3j12b5i3a161lcm; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area_alternative_names
    ADD CONSTRAINT uk_hb8tvxumnj3j12b5i3a161lcm UNIQUE (alternative_names_id);


--
-- Name: equipment_position_key_values uk_hw9nq847b38qyxa25ide9ltyy; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_position_key_values
    ADD CONSTRAINT uk_hw9nq847b38qyxa25ide9ltyy UNIQUE (key_values_id);


--
-- Name: parking_key_values uk_iteh0to4gqim61p74lq2ugc2k; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_key_values
    ADD CONSTRAINT uk_iteh0to4gqim61p74lq2ugc2k UNIQUE (key_values_id);


--
-- Name: parking_parking_properties uk_j9vtca7vmg7ee8588wdseipvv; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_parking_properties
    ADD CONSTRAINT uk_j9vtca7vmg7ee8588wdseipvv UNIQUE (parking_properties_id);


--
-- Name: parking_area_valid_betweens uk_jdy9utvmvxwylyqgvedm5dos3; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area_valid_betweens
    ADD CONSTRAINT uk_jdy9utvmvxwylyqgvedm5dos3 UNIQUE (valid_betweens_id);


--
-- Name: boarding_position_key_values uk_jilhh4jbyloqka3r1xpv88lpb; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position_key_values
    ADD CONSTRAINT uk_jilhh4jbyloqka3r1xpv88lpb UNIQUE (key_values_id);


--
-- Name: access_space_key_values uk_kcsgl47aba68824kjdceo60ql; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space_key_values
    ADD CONSTRAINT uk_kcsgl47aba68824kjdceo60ql UNIQUE (key_values_id);


--
-- Name: path_link_key_values uk_kn4m9f3l3gdgyg7mdus6qd1r1; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_link_key_values
    ADD CONSTRAINT uk_kn4m9f3l3gdgyg7mdus6qd1r1 UNIQUE (key_values_id);


--
-- Name: road_address_valid_betweens uk_lhxytq6yrya40euhfxecpbhbu; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY road_address_valid_betweens
    ADD CONSTRAINT uk_lhxytq6yrya40euhfxecpbhbu UNIQUE (valid_betweens_id);


--
-- Name: valid_between_valid_betweens uk_lm5dl4quqhh5t8gkiqo25f9ge; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY valid_between_valid_betweens
    ADD CONSTRAINT uk_lm5dl4quqhh5t8gkiqo25f9ge UNIQUE (valid_betweens_id);


--
-- Name: parking_area_equipment_places uk_lpu10934dkewquqflehpo95ye; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area_equipment_places
    ADD CONSTRAINT uk_lpu10934dkewquqflehpo95ye UNIQUE (equipment_places_id);


--
-- Name: quay_boarding_positions uk_lx6ql0b834b5l0agvouh1w860; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_boarding_positions
    ADD CONSTRAINT uk_lx6ql0b834b5l0agvouh1w860 UNIQUE (boarding_positions_id);


--
-- Name: topographic_place_valid_betweens uk_m4i65tb22asrk1cdixner5d39; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY topographic_place_valid_betweens
    ADD CONSTRAINT uk_m4i65tb22asrk1cdixner5d39 UNIQUE (valid_betweens_id);


--
-- Name: level_valid_betweens uk_mj4jpgl0f24b81lgby5fkhfhp; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY level_valid_betweens
    ADD CONSTRAINT uk_mj4jpgl0f24b81lgby5fkhfhp UNIQUE (valid_betweens_id);


--
-- Name: stop_place_equipment_places uk_mnksrduwpe1bfxskob1pkbi28; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_equipment_places
    ADD CONSTRAINT uk_mnksrduwpe1bfxskob1pkbi28 UNIQUE (equipment_places_id);


--
-- Name: tariff_zone_key_values uk_n3n61qrmgry87uoc7sho0nphm; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY tariff_zone_key_values
    ADD CONSTRAINT uk_n3n61qrmgry87uoc7sho0nphm UNIQUE (key_values_id);


--
-- Name: parking_valid_betweens uk_n3w1cx9chahe5xpqf6ydbf4ii; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_valid_betweens
    ADD CONSTRAINT uk_n3w1cx9chahe5xpqf6ydbf4ii UNIQUE (valid_betweens_id);


--
-- Name: accessibility_limitation_valid_betweens uk_o14whpqpjvsamtn7a5wfw9fox; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY accessibility_limitation_valid_betweens
    ADD CONSTRAINT uk_o14whpqpjvsamtn7a5wfw9fox UNIQUE (valid_betweens_id);


--
-- Name: installed_equipment_version_structure_valid_betweens uk_oxepctl7tykkut1o280slkov9; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY installed_equipment_version_structure_valid_betweens
    ADD CONSTRAINT uk_oxepctl7tykkut1o280slkov9 UNIQUE (valid_betweens_id);


--
-- Name: boarding_position_check_constraints uk_pcbtfcjcauaikel1s4uqjfldp; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position_check_constraints
    ADD CONSTRAINT uk_pcbtfcjcauaikel1s4uqjfldp UNIQUE (check_constraints_id);


--
-- Name: quay_key_values uk_plgcx1aoolr4vngts8ifkrse6; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_key_values
    ADD CONSTRAINT uk_plgcx1aoolr4vngts8ifkrse6 UNIQUE (key_values_id);


--
-- Name: stop_place_valid_betweens uk_q5ji9gx7hj719d39r6ntcrrw5; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_valid_betweens
    ADD CONSTRAINT uk_q5ji9gx7hj719d39r6ntcrrw5 UNIQUE (valid_betweens_id);


--
-- Name: parking_capacity_valid_betweens uk_q5ylsbm98vfu6bqe16tihdsvx; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_capacity_valid_betweens
    ADD CONSTRAINT uk_q5ylsbm98vfu6bqe16tihdsvx UNIQUE (valid_betweens_id);


--
-- Name: equipment_position_valid_betweens uk_qjb05t28jumd7j5ap3sl6knpr; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_position_valid_betweens
    ADD CONSTRAINT uk_qjb05t28jumd7j5ap3sl6knpr UNIQUE (valid_betweens_id);


--
-- Name: access_space_alternative_names uk_qvw904jxmey0b5c2oenaks4o6; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space_alternative_names
    ADD CONSTRAINT uk_qvw904jxmey0b5c2oenaks4o6 UNIQUE (alternative_names_id);


--
-- Name: parking_alternative_names uk_rlf4rns9qabhhdins8l3y89fo; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_alternative_names
    ADD CONSTRAINT uk_rlf4rns9qabhhdins8l3y89fo UNIQUE (alternative_names_id);


--
-- Name: parking_area_key_values uk_rxv53i59u1pf70kxtdchlxird; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area_key_values
    ADD CONSTRAINT uk_rxv53i59u1pf70kxtdchlxird UNIQUE (key_values_id);


--
-- Name: installed_equipment_version_structure_installed_equipment uk_s4px36fd2jutbf4p8lagcocbd; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY installed_equipment_version_structure_installed_equipment
    ADD CONSTRAINT uk_s4px36fd2jutbf4p8lagcocbd UNIQUE (installed_equipment_id);


--
-- Name: alternative_name_valid_betweens uk_s5bgkw8nxr3gupw1v1n63ee5p; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY alternative_name_valid_betweens
    ADD CONSTRAINT uk_s5bgkw8nxr3gupw1v1n63ee5p UNIQUE (valid_betweens_id);


--
-- Name: path_link_valid_betweens uk_s7dlt7ydrvxyj3iiscoqj9aud; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_link_valid_betweens
    ADD CONSTRAINT uk_s7dlt7ydrvxyj3iiscoqj9aud UNIQUE (valid_betweens_id);


--
-- Name: stop_place_access_spaces uk_stiis59w04hmptq2wkpsfjpb8; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_access_spaces
    ADD CONSTRAINT uk_stiis59w04hmptq2wkpsfjpb8 UNIQUE (access_spaces_id);


--
-- Name: parking_properties_valid_betweens uk_t9d85xdrq9ipw4v93fo3vkwji; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_properties_valid_betweens
    ADD CONSTRAINT uk_t9d85xdrq9ipw4v93fo3vkwji UNIQUE (valid_betweens_id);


--
-- Name: topographic_place_key_values uk_tq5dgj811w1k4w86m4x66iwso; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY topographic_place_key_values
    ADD CONSTRAINT uk_tq5dgj811w1k4w86m4x66iwso UNIQUE (key_values_id);


--
-- Name: valid_between_key_values valid_between_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY valid_between_key_values
    ADD CONSTRAINT valid_between_key_values_pkey PRIMARY KEY (valid_between_id, key_values_key);


--
-- Name: valid_between valid_between_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY valid_between
    ADD CONSTRAINT valid_between_pkey PRIMARY KEY (id);


--
-- Name: value value_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY value
    ADD CONSTRAINT value_pkey PRIMARY KEY (id);


--
-- Name: vehicle_stopping_places_rel_structure vehicle_stopping_places_rel_structure_pkey; Type: CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY vehicle_stopping_places_rel_structure
    ADD CONSTRAINT vehicle_stopping_places_rel_structure_pkey PRIMARY KEY (id);


--
-- Name: id_value_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX id_value_index ON id_generator USING btree (id_value);


--
-- Name: items_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX items_index ON value_items USING btree (items);


--
-- Name: items_trgm_gin_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX items_trgm_gin_index ON value_items USING gin (items gin_trgm_ops);


--
-- Name: lower_case_stop_place_name_value; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX lower_case_stop_place_name_value ON stop_place USING btree (lower((name_value)::text));


--
-- Name: persistable_polygon_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX persistable_polygon_index ON persistable_polygon USING gist (polygon);


--
-- Name: quay_netex_id_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX quay_netex_id_index ON quay USING btree (netex_id);


--
-- Name: quay_version_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX quay_version_index ON quay USING btree (version);


--
-- Name: stop_place_centroid_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX stop_place_centroid_index ON stop_place USING gist (centroid);


--
-- Name: stop_place_name_value_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX stop_place_name_value_index ON stop_place USING btree (name_value);


--
-- Name: stop_place_netex_id_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX stop_place_netex_id_index ON stop_place USING btree (netex_id);


--
-- Name: stop_place_type_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX stop_place_type_index ON stop_place USING btree (stop_place_type);


--
-- Name: stop_place_version_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX stop_place_version_index ON stop_place USING btree (version);


--
-- Name: table_name_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX table_name_index ON id_generator USING btree (table_name);


--
-- Name: topographic_place_name_value_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX topographic_place_name_value_index ON topographic_place USING btree (name_value);


--
-- Name: trgm_idx; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX trgm_idx ON stop_place USING gist (name_value gist_trgm_ops);


--
-- Name: value_id_index; Type: INDEX; Schema: public; Owner: tiamat
--

CREATE INDEX value_id_index ON value_items USING btree (value_id);


--
-- Name: quay fk10uxphnmebvjuua8n3erlo1n1; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay
    ADD CONSTRAINT fk10uxphnmebvjuua8n3erlo1n1 FOREIGN KEY (accessibility_assessment_id) REFERENCES accessibility_assessment(id);


--
-- Name: parking_parking_areas fk117owm5bl41inj5bxrwp84awc; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_parking_areas
    ADD CONSTRAINT fk117owm5bl41inj5bxrwp84awc FOREIGN KEY (parking_areas_id) REFERENCES parking_area(id);


--
-- Name: access_space fk1fidaattdqbcu4jlypwcf4p2m; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space
    ADD CONSTRAINT fk1fidaattdqbcu4jlypwcf4p2m FOREIGN KEY (polygon_id) REFERENCES persistable_polygon(id);


--
-- Name: stop_place_quays fk22h93cna6b2o9o8vybqb1i9qb; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_quays
    ADD CONSTRAINT fk22h93cna6b2o9o8vybqb1i9qb FOREIGN KEY (stop_place_id) REFERENCES stop_place(id);


--
-- Name: road_address_valid_betweens fk26l1117lde2skl59m7a9pnjtn; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY road_address_valid_betweens
    ADD CONSTRAINT fk26l1117lde2skl59m7a9pnjtn FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: quay_check_constraints fk2a38aoc67evygl1e6xk0iybta; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_check_constraints
    ADD CONSTRAINT fk2a38aoc67evygl1e6xk0iybta FOREIGN KEY (check_constraints_id) REFERENCES check_constraint(id);


--
-- Name: boarding_position_equipment_places fk2afgri1bcaay1etgl7sw5wljq; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position_equipment_places
    ADD CONSTRAINT fk2afgri1bcaay1etgl7sw5wljq FOREIGN KEY (equipment_places_id) REFERENCES equipment_place(id);


--
-- Name: equipment_place_key_values fk2hxfx966yjfx66s7e0rwkc807; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_place_key_values
    ADD CONSTRAINT fk2hxfx966yjfx66s7e0rwkc807 FOREIGN KEY (key_values_id) REFERENCES value(id);


--
-- Name: installed_equipment fk2vtm888bpalre9vtw62crif92; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY installed_equipment
    ADD CONSTRAINT fk2vtm888bpalre9vtw62crif92 FOREIGN KEY (id) REFERENCES explicit_equipments_rel_structure(id);


--
-- Name: parking_area_equipment_places fk32yrc89194bun5bwjy1u6pan4; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area_equipment_places
    ADD CONSTRAINT fk32yrc89194bun5bwjy1u6pan4 FOREIGN KEY (equipment_places_id) REFERENCES equipment_place(id);


--
-- Name: parking_properties_valid_betweens fk35p0yeb9jo2713cgbduado87r; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_properties_valid_betweens
    ADD CONSTRAINT fk35p0yeb9jo2713cgbduado87r FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: stop_place_valid_betweens fk38f2hoi2fn3ao3vaqutdhckee; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_valid_betweens
    ADD CONSTRAINT fk38f2hoi2fn3ao3vaqutdhckee FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: stop_place_alternative_names fk38wmuiuq889ldydpbyrybc7od; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_alternative_names
    ADD CONSTRAINT fk38wmuiuq889ldydpbyrybc7od FOREIGN KEY (alternative_names_id) REFERENCES alternative_name(id);


--
-- Name: stop_place_tariff_zones fk3j2paa5yrolwcuvpsx15jx9x9; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_tariff_zones
    ADD CONSTRAINT fk3j2paa5yrolwcuvpsx15jx9x9 FOREIGN KEY (stop_place_id) REFERENCES stop_place(id);


--
-- Name: boarding_position_alternative_names fk3kk76shxnjbca405imdodyx5x; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position_alternative_names
    ADD CONSTRAINT fk3kk76shxnjbca405imdodyx5x FOREIGN KEY (boarding_position_id) REFERENCES boarding_position(id);


--
-- Name: parking_properties_spaces fk3whnrr5j2addxg4vv3vnx4x4e; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_properties_spaces
    ADD CONSTRAINT fk3whnrr5j2addxg4vv3vnx4x4e FOREIGN KEY (parking_properties_id) REFERENCES parking_properties(id);


--
-- Name: accesses_rel_structure_access_ref_or_access fk4513k7hxo6ya27u77tf6fyw4b; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY accesses_rel_structure_access_ref_or_access
    ADD CONSTRAINT fk4513k7hxo6ya27u77tf6fyw4b FOREIGN KEY (accesses_rel_structure_id) REFERENCES accesses_rel_structure(id);


--
-- Name: road_address fk47skmegkxltud8u4iv6e6dbyf; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY road_address
    ADD CONSTRAINT fk47skmegkxltud8u4iv6e6dbyf FOREIGN KEY (road_name_id) REFERENCES multilingual_string_entity(id);


--
-- Name: tariff_zone_valid_betweens fk4anxg0h47lwrq9ssrm349tprr; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY tariff_zone_valid_betweens
    ADD CONSTRAINT fk4anxg0h47lwrq9ssrm349tprr FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: path_junction_key_values fk4cgeli3mja440oxomtpvjed6t; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_junction_key_values
    ADD CONSTRAINT fk4cgeli3mja440oxomtpvjed6t FOREIGN KEY (path_junction_id) REFERENCES path_junction(id);


--
-- Name: check_constraint_key_values fk4sgvgvx1dy8kvkyhvmpb4b0u5; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY check_constraint_key_values
    ADD CONSTRAINT fk4sgvgvx1dy8kvkyhvmpb4b0u5 FOREIGN KEY (check_constraint_id) REFERENCES check_constraint(id);


--
-- Name: quay_equipment_places fk543bcymfury929rotvx4qsvjp; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_equipment_places
    ADD CONSTRAINT fk543bcymfury929rotvx4qsvjp FOREIGN KEY (quay_id) REFERENCES quay(id);


--
-- Name: stop_place_equipment_places fk5fxel42imvy86cikp0d27uubw; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_equipment_places
    ADD CONSTRAINT fk5fxel42imvy86cikp0d27uubw FOREIGN KEY (stop_place_id) REFERENCES stop_place(id);


--
-- Name: stop_place_alternative_names fk5h4utprl88fnjm48xnwhuly5q; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_alternative_names
    ADD CONSTRAINT fk5h4utprl88fnjm48xnwhuly5q FOREIGN KEY (stop_place_id) REFERENCES stop_place(id);


--
-- Name: quay fk5pa1d6xv2ad8gd33gcubl1dhb; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay
    ADD CONSTRAINT fk5pa1d6xv2ad8gd33gcubl1dhb FOREIGN KEY (place_equipments_id) REFERENCES installed_equipment_version_structure(id);


--
-- Name: installed_equipment_version_structure_valid_betweens fk5w2ducmkuo2swd4hfdawj2xyd; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY installed_equipment_version_structure_valid_betweens
    ADD CONSTRAINT fk5w2ducmkuo2swd4hfdawj2xyd FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: accessibility_assessment_valid_betweens fk68hrxhopogkq98ow69wyhuc0s; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY accessibility_assessment_valid_betweens
    ADD CONSTRAINT fk68hrxhopogkq98ow69wyhuc0s FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: boarding_position fk698fd5rwmie70j91ngq0jfau2; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position
    ADD CONSTRAINT fk698fd5rwmie70j91ngq0jfau2 FOREIGN KEY (accessibility_assessment_id) REFERENCES accessibility_assessment(id);


--
-- Name: boarding_position_key_values fk6pdfundcf9ro8reay93b03l4f; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position_key_values
    ADD CONSTRAINT fk6pdfundcf9ro8reay93b03l4f FOREIGN KEY (boarding_position_id) REFERENCES boarding_position(id);


--
-- Name: quays_rel_structure_quay_ref_or_quay fk6r7nb1umrkunlk2e6ccm7qjf8; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quays_rel_structure_quay_ref_or_quay
    ADD CONSTRAINT fk6r7nb1umrkunlk2e6ccm7qjf8 FOREIGN KEY (quays_rel_structure_id) REFERENCES quays_rel_structure(id);


--
-- Name: stop_place_key_values fk6v8qe1uxjok2wrexhprfusrpy; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_key_values
    ADD CONSTRAINT fk6v8qe1uxjok2wrexhprfusrpy FOREIGN KEY (key_values_id) REFERENCES value(id);


--
-- Name: stop_place_equipment_places fk6ywi2d8ytfi5m3e5emowdysen; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_equipment_places
    ADD CONSTRAINT fk6ywi2d8ytfi5m3e5emowdysen FOREIGN KEY (equipment_places_id) REFERENCES equipment_place(id);


--
-- Name: parking_parking_properties fk70urag9nyyejkivm7bodubg0l; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_parking_properties
    ADD CONSTRAINT fk70urag9nyyejkivm7bodubg0l FOREIGN KEY (parking_id) REFERENCES parking(id);


--
-- Name: accessibility_assessment_limitations fk71lv2d2xdl6il9t8lxhiw2oxr; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY accessibility_assessment_limitations
    ADD CONSTRAINT fk71lv2d2xdl6il9t8lxhiw2oxr FOREIGN KEY (limitations_id) REFERENCES accessibility_limitation(id);


--
-- Name: path_junction_key_values fk79i9vyw1kl8vv6qt5t5ag37ib; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_junction_key_values
    ADD CONSTRAINT fk79i9vyw1kl8vv6qt5t5ag37ib FOREIGN KEY (key_values_id) REFERENCES value(id);


--
-- Name: parking_alternative_names fk7l8e26etcpfmcd63bqvioh7bt; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_alternative_names
    ADD CONSTRAINT fk7l8e26etcpfmcd63bqvioh7bt FOREIGN KEY (alternative_names_id) REFERENCES alternative_name(id);


--
-- Name: parking_area_alternative_names fk83bhdbmy03cgn1v7vfm2hc25g; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area_alternative_names
    ADD CONSTRAINT fk83bhdbmy03cgn1v7vfm2hc25g FOREIGN KEY (alternative_names_id) REFERENCES alternative_name(id);


--
-- Name: check_constraint_valid_betweens fk88yo3st5oqbqhfq3gb6njy51u; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY check_constraint_valid_betweens
    ADD CONSTRAINT fk88yo3st5oqbqhfq3gb6njy51u FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: level fk8h6xjcugey2fg2dhix15v4lsm; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY level
    ADD CONSTRAINT fk8h6xjcugey2fg2dhix15v4lsm FOREIGN KEY (description_id) REFERENCES multilingual_string_entity(id);


--
-- Name: level fk8icvbp3tjonuijcbajsgdvkjp; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY level
    ADD CONSTRAINT fk8icvbp3tjonuijcbajsgdvkjp FOREIGN KEY (name_id) REFERENCES multilingual_string_entity(id);


--
-- Name: tariff_zone_key_values fk8ulw0u13r8x9tytsrvo6tr8ak; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY tariff_zone_key_values
    ADD CONSTRAINT fk8ulw0u13r8x9tytsrvo6tr8ak FOREIGN KEY (tariff_zone_id) REFERENCES tariff_zone(id);


--
-- Name: parking fk96p5d5t8n4r0mqxjhkwktud4; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking
    ADD CONSTRAINT fk96p5d5t8n4r0mqxjhkwktud4 FOREIGN KEY (polygon_id) REFERENCES persistable_polygon(id);


--
-- Name: level_key_values fk99i09lflmm3dtvdh479gybs5g; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY level_key_values
    ADD CONSTRAINT fk99i09lflmm3dtvdh479gybs5g FOREIGN KEY (key_values_id) REFERENCES value(id);


--
-- Name: navigation_path fk9m5x0ndxgplk60o86dsr1da8m; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY navigation_path
    ADD CONSTRAINT fk9m5x0ndxgplk60o86dsr1da8m FOREIGN KEY (id) REFERENCES navigation_paths_rel_structure(id);


--
-- Name: path_link_key_values fk9qwypjgswmctp5fn2tahr0l7o; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_link_key_values
    ADD CONSTRAINT fk9qwypjgswmctp5fn2tahr0l7o FOREIGN KEY (path_link_id) REFERENCES path_link(id);


--
-- Name: stop_place_access_spaces fk9v547jr8nnbcfvv4tsi229kxn; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_access_spaces
    ADD CONSTRAINT fk9v547jr8nnbcfvv4tsi229kxn FOREIGN KEY (access_spaces_id) REFERENCES access_space(id);


--
-- Name: parking_key_values fk9y6amcojpo70vg2ydp3p5il9s; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_key_values
    ADD CONSTRAINT fk9y6amcojpo70vg2ydp3p5il9s FOREIGN KEY (key_values_id) REFERENCES value(id);


--
-- Name: parking_area fka20x6t18riu82sahee5mn0g7n; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area
    ADD CONSTRAINT fka20x6t18riu82sahee5mn0g7n FOREIGN KEY (parking_properties_id) REFERENCES parking_properties(id);


--
-- Name: check_constraint_key_values fka88lssnbb1kj1s052nckd6dn4; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY check_constraint_key_values
    ADD CONSTRAINT fka88lssnbb1kj1s052nckd6dn4 FOREIGN KEY (key_values_id) REFERENCES value(id);


--
-- Name: equipment_position_key_values fka9k2btoq1hylsxgdc2r4y7c6h; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_position_key_values
    ADD CONSTRAINT fka9k2btoq1hylsxgdc2r4y7c6h FOREIGN KEY (equipment_position_id) REFERENCES equipment_position(id);


--
-- Name: equipment_place_equipment_positions fkabblwcd7yqvqcueubgds9g79p; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_place_equipment_positions
    ADD CONSTRAINT fkabblwcd7yqvqcueubgds9g79p FOREIGN KEY (equipment_positions_id) REFERENCES equipment_position(id);


--
-- Name: access_space_equipment_places fkahpl2sexrde229vbbli7grrp4; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space_equipment_places
    ADD CONSTRAINT fkahpl2sexrde229vbbli7grrp4 FOREIGN KEY (access_space_id) REFERENCES access_space(id);


--
-- Name: alternative_name_valid_betweens fkb76kc1l21vk6d4mj6to9wf4rt; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY alternative_name_valid_betweens
    ADD CONSTRAINT fkb76kc1l21vk6d4mj6to9wf4rt FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: equipment_position_key_values fkb9i1yjga0o91xdhcb3rvpvkx9; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_position_key_values
    ADD CONSTRAINT fkb9i1yjga0o91xdhcb3rvpvkx9 FOREIGN KEY (key_values_id) REFERENCES value(id);


--
-- Name: quay_key_values fkc18wd399ytds57bsuuip2pl41; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_key_values
    ADD CONSTRAINT fkc18wd399ytds57bsuuip2pl41 FOREIGN KEY (key_values_id) REFERENCES value(id);


--
-- Name: topographic_place_key_values fkc3wle51cxccfkwwkpiu2ekkim; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY topographic_place_key_values
    ADD CONSTRAINT fkc3wle51cxccfkwwkpiu2ekkim FOREIGN KEY (topographic_place_id) REFERENCES topographic_place(id);


--
-- Name: quay_alternative_names fkc51iijml1n53m15a3o1uytbxv; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_alternative_names
    ADD CONSTRAINT fkc51iijml1n53m15a3o1uytbxv FOREIGN KEY (alternative_names_id) REFERENCES alternative_name(id);


--
-- Name: valid_between_valid_betweens fkc53kxwl8c9ui9yr2f95sc0b2x; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY valid_between_valid_betweens
    ADD CONSTRAINT fkc53kxwl8c9ui9yr2f95sc0b2x FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: parking_properties_spaces fkc6bu1c3cyaxa6w9s096rurn7b; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_properties_spaces
    ADD CONSTRAINT fkc6bu1c3cyaxa6w9s096rurn7b FOREIGN KEY (spaces_id) REFERENCES parking_capacity(id);


--
-- Name: stop_place_access_spaces fkcfh3y91gw4ulh0x6tohnevsxt; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_access_spaces
    ADD CONSTRAINT fkcfh3y91gw4ulh0x6tohnevsxt FOREIGN KEY (stop_place_id) REFERENCES stop_place(id);


--
-- Name: parking_key_values fkchgifjy5ltx55l2riu92p8rah; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_key_values
    ADD CONSTRAINT fkchgifjy5ltx55l2riu92p8rah FOREIGN KEY (parking_id) REFERENCES parking(id);


--
-- Name: boarding_position fkckcw3qglsese2680ss59lq9yy; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position
    ADD CONSTRAINT fkckcw3qglsese2680ss59lq9yy FOREIGN KEY (place_equipments_id) REFERENCES installed_equipment_version_structure(id);


--
-- Name: equipment_place_valid_betweens fkcusybby9eld3y0iika2juwq5l; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_place_valid_betweens
    ADD CONSTRAINT fkcusybby9eld3y0iika2juwq5l FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: access_space_check_constraints fkd0f8scim513pkmar0rrtgmvk0; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space_check_constraints
    ADD CONSTRAINT fkd0f8scim513pkmar0rrtgmvk0 FOREIGN KEY (check_constraints_id) REFERENCES check_constraint(id);


--
-- Name: check_constraint fkd7kx2g2kfknuq180s9psrxheg; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY check_constraint
    ADD CONSTRAINT fkd7kx2g2kfknuq180s9psrxheg FOREIGN KEY (description_id) REFERENCES multilingual_string_entity(id);


--
-- Name: quay_boarding_positions fkddggv25j677uyu93kjcejrkoy; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_boarding_positions
    ADD CONSTRAINT fkddggv25j677uyu93kjcejrkoy FOREIGN KEY (quay_id) REFERENCES quay(id);


--
-- Name: installed_equipment_version_structure_installed_equipment fkdjkqt8xtfm3betwint02yr8ul; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY installed_equipment_version_structure_installed_equipment
    ADD CONSTRAINT fkdjkqt8xtfm3betwint02yr8ul FOREIGN KEY (installed_equipment_id) REFERENCES installed_equipment_version_structure(id);


--
-- Name: access_space fkdkxpwi2gsjyno21hnp5vxjcqx; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space
    ADD CONSTRAINT fkdkxpwi2gsjyno21hnp5vxjcqx FOREIGN KEY (accessibility_assessment_id) REFERENCES accessibility_assessment(id);


--
-- Name: quay_key_values fke06xofaj85jd2715l5wvgcewf; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_key_values
    ADD CONSTRAINT fke06xofaj85jd2715l5wvgcewf FOREIGN KEY (quay_id) REFERENCES quay(id);


--
-- Name: boarding_position_check_constraints fke3k0ye5cmkahhxwrarv52sxgg; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position_check_constraints
    ADD CONSTRAINT fke3k0ye5cmkahhxwrarv52sxgg FOREIGN KEY (boarding_position_id) REFERENCES boarding_position(id);


--
-- Name: destination_display_view fke4decqec3uijjvob9y6nm1m4n; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY destination_display_view
    ADD CONSTRAINT fke4decqec3uijjvob9y6nm1m4n FOREIGN KEY (name_id) REFERENCES multilingual_string_entity(id);


--
-- Name: quay_equipment_places fkeaggpvk1rtleplvo725msboob; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_equipment_places
    ADD CONSTRAINT fkeaggpvk1rtleplvo725msboob FOREIGN KEY (equipment_places_id) REFERENCES equipment_place(id);


--
-- Name: valid_between_valid_betweens fkec6fpjqub9jsmgybs0yg3mvev; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY valid_between_valid_betweens
    ADD CONSTRAINT fkec6fpjqub9jsmgybs0yg3mvev FOREIGN KEY (valid_between_id) REFERENCES valid_between(id);


--
-- Name: parking_area_alternative_names fked2qtdvpkkrp456ttsr04fdtd; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area_alternative_names
    ADD CONSTRAINT fked2qtdvpkkrp456ttsr04fdtd FOREIGN KEY (parking_area_id) REFERENCES parking_area(id);


--
-- Name: boarding_position_alternative_names fkeo5gxbxqw4nujvef4rbqlupjo; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position_alternative_names
    ADD CONSTRAINT fkeo5gxbxqw4nujvef4rbqlupjo FOREIGN KEY (alternative_names_id) REFERENCES alternative_name(id);


--
-- Name: parking_valid_betweens fkeqdps969jigulg1m28bwy5ajb; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_valid_betweens
    ADD CONSTRAINT fkeqdps969jigulg1m28bwy5ajb FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: boarding_position_check_constraints fkexpjw5brnj2x19mke33ahb4v1; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position_check_constraints
    ADD CONSTRAINT fkexpjw5brnj2x19mke33ahb4v1 FOREIGN KEY (check_constraints_id) REFERENCES check_constraint(id);


--
-- Name: tariff_zone_key_values fkf0utp1a034yhbyt80kv99lu84; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY tariff_zone_key_values
    ADD CONSTRAINT fkf0utp1a034yhbyt80kv99lu84 FOREIGN KEY (key_values_id) REFERENCES value(id);


--
-- Name: path_link_valid_betweens fkf51j0wp0kufnmgghpllvie8kx; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_link_valid_betweens
    ADD CONSTRAINT fkf51j0wp0kufnmgghpllvie8kx FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: parking_area_key_values fkfavrlym6c13ftqqhybqdypaa0; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area_key_values
    ADD CONSTRAINT fkfavrlym6c13ftqqhybqdypaa0 FOREIGN KEY (key_values_id) REFERENCES value(id);


--
-- Name: stop_place fkfb9cw77oshl15ax3v7o4x5ndb; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place
    ADD CONSTRAINT fkfb9cw77oshl15ax3v7o4x5ndb FOREIGN KEY (topographic_place_id) REFERENCES topographic_place(id);


--
-- Name: topographic_place_key_values fkfdntaoibnwcm1tjita53n8wp5; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY topographic_place_key_values
    ADD CONSTRAINT fkfdntaoibnwcm1tjita53n8wp5 FOREIGN KEY (key_values_id) REFERENCES value(id);


--
-- Name: access_space_alternative_names fkfrvjdit8u3jfuwrfjj5kv7ej8; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space_alternative_names
    ADD CONSTRAINT fkfrvjdit8u3jfuwrfjj5kv7ej8 FOREIGN KEY (access_space_id) REFERENCES access_space(id);


--
-- Name: road_address_key_values fkfyaj3pe40wjw4ahh1686nva2f; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY road_address_key_values
    ADD CONSTRAINT fkfyaj3pe40wjw4ahh1686nva2f FOREIGN KEY (key_values_id) REFERENCES value(id);


--
-- Name: equipment_position fkg7tekxdtcgxc9nuqg1gtbfub; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_position
    ADD CONSTRAINT fkg7tekxdtcgxc9nuqg1gtbfub FOREIGN KEY (description_id) REFERENCES multilingual_string_entity(id);


--
-- Name: quay fkga2n69n19frbpsm112vnv7ujp; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay
    ADD CONSTRAINT fkga2n69n19frbpsm112vnv7ujp FOREIGN KEY (polygon_id) REFERENCES persistable_polygon(id);


--
-- Name: access_space_alternative_names fkggy00kekruqt7vq13o0hrishg; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space_alternative_names
    ADD CONSTRAINT fkggy00kekruqt7vq13o0hrishg FOREIGN KEY (alternative_names_id) REFERENCES alternative_name(id);


--
-- Name: access_space_valid_betweens fkgtc4r7fqv35djtplnk0a4y9wk; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space_valid_betweens
    ADD CONSTRAINT fkgtc4r7fqv35djtplnk0a4y9wk FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: access_space_key_values fkguduxo8jkx68ewnlb9vkg6asj; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space_key_values
    ADD CONSTRAINT fkguduxo8jkx68ewnlb9vkg6asj FOREIGN KEY (access_space_id) REFERENCES access_space(id);


--
-- Name: accessibility_limitation_valid_betweens fkgx8u7us15lfxbft4ld766shjs; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY accessibility_limitation_valid_betweens
    ADD CONSTRAINT fkgx8u7us15lfxbft4ld766shjs FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: path_junctions_rel_structure_path_junction_ref_or_path_junction fkh0tejbxbyy99cmnjx82tsmfyf; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_junctions_rel_structure_path_junction_ref_or_path_junction
    ADD CONSTRAINT fkh0tejbxbyy99cmnjx82tsmfyf FOREIGN KEY (path_junctions_rel_structure_id) REFERENCES path_junctions_rel_structure(id);


--
-- Name: vehicle_stopping_places_rel_structure_vehicle_stopping_place_re fkh0yygn1jyuukkp0lasxsw4dvo; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY vehicle_stopping_places_rel_structure_vehicle_stopping_place_re
    ADD CONSTRAINT fkh0yygn1jyuukkp0lasxsw4dvo FOREIGN KEY (vehicle_stopping_places_rel_structure_id) REFERENCES vehicle_stopping_places_rel_structure(id);


--
-- Name: parking_area fkh6h52ajwscge6qctip0056ja5; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area
    ADD CONSTRAINT fkh6h52ajwscge6qctip0056ja5 FOREIGN KEY (accessibility_assessment_id) REFERENCES accessibility_assessment(id);


--
-- Name: boarding_position_valid_betweens fkhnnap1wlq56ra6sl2nfehbd60; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position_valid_betweens
    ADD CONSTRAINT fkhnnap1wlq56ra6sl2nfehbd60 FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: access_space_equipment_places fkhvudykavfk476h6yvqbe8uke7; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space_equipment_places
    ADD CONSTRAINT fkhvudykavfk476h6yvqbe8uke7 FOREIGN KEY (equipment_places_id) REFERENCES equipment_place(id);


--
-- Name: path_link_key_values fki5ewaofkg7dqyrfxmv05up23k; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_link_key_values
    ADD CONSTRAINT fki5ewaofkg7dqyrfxmv05up23k FOREIGN KEY (key_values_id) REFERENCES value(id);


--
-- Name: quays_rel_structure_quay_ref_or_quay fkieeq2yxpnhnb8r091jwdpos6n; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quays_rel_structure_quay_ref_or_quay
    ADD CONSTRAINT fkieeq2yxpnhnb8r091jwdpos6n FOREIGN KEY (quay_ref_or_quay_id) REFERENCES quay(id);


--
-- Name: topographic_place fkin7vu25nr46n8tufgnrjdjpna; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY topographic_place
    ADD CONSTRAINT fkin7vu25nr46n8tufgnrjdjpna FOREIGN KEY (polygon_id) REFERENCES persistable_polygon(id);


--
-- Name: parking_equipment_places fkjdjk7xlyh5j4wwkdj7b55wwvr; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_equipment_places
    ADD CONSTRAINT fkjdjk7xlyh5j4wwkdj7b55wwvr FOREIGN KEY (parking_id) REFERENCES parking(id);


--
-- Name: parking fkjdu5ypd58h2k87dgguvfx904d; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking
    ADD CONSTRAINT fkjdu5ypd58h2k87dgguvfx904d FOREIGN KEY (place_equipments_id) REFERENCES installed_equipment_version_structure(id);


--
-- Name: equipment_place fkji5u4pfe7mk8cb9m02xj9mydb; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_place
    ADD CONSTRAINT fkji5u4pfe7mk8cb9m02xj9mydb FOREIGN KEY (polygon_id) REFERENCES persistable_polygon(id);


--
-- Name: quay_valid_betweens fkju5f5s6y8x8ey1bqjbih8snew; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_valid_betweens
    ADD CONSTRAINT fkju5f5s6y8x8ey1bqjbih8snew FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: check_constraint fkjyh851p5h9hcg8fftuejisrbh; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY check_constraint
    ADD CONSTRAINT fkjyh851p5h9hcg8fftuejisrbh FOREIGN KEY (name_id) REFERENCES multilingual_string_entity(id);


--
-- Name: stop_place fkk40r06hkuvmp9bn3nqh3hc72p; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place
    ADD CONSTRAINT fkk40r06hkuvmp9bn3nqh3hc72p FOREIGN KEY (place_equipments_id) REFERENCES installed_equipment_version_structure(id);


--
-- Name: parking_capacity_valid_betweens fkk4js0atx4ji42j7dbrhjm0vki; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_capacity_valid_betweens
    ADD CONSTRAINT fkk4js0atx4ji42j7dbrhjm0vki FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: access_space fkkaorr2thiqfib1tp2u8hy7qjj; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space
    ADD CONSTRAINT fkkaorr2thiqfib1tp2u8hy7qjj FOREIGN KEY (place_equipments_id) REFERENCES installed_equipment_version_structure(id);


--
-- Name: quay_boarding_positions fkkclc1cagtjsudx3cw7lseovcg; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_boarding_positions
    ADD CONSTRAINT fkkclc1cagtjsudx3cw7lseovcg FOREIGN KEY (boarding_positions_id) REFERENCES boarding_position(id);


--
-- Name: accessibility_assessment_limitations fkkghye5kl3gcgb4446yva0hqib; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY accessibility_assessment_limitations
    ADD CONSTRAINT fkkghye5kl3gcgb4446yva0hqib FOREIGN KEY (accessibility_assessment_id) REFERENCES accessibility_assessment(id);


--
-- Name: path_junction_valid_betweens fkkgi976775pm6mmpmqwvh4e385; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_junction_valid_betweens
    ADD CONSTRAINT fkkgi976775pm6mmpmqwvh4e385 FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: parking_parking_areas fklfn6esk2xmgy0abtbkxleg94h; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_parking_areas
    ADD CONSTRAINT fklfn6esk2xmgy0abtbkxleg94h FOREIGN KEY (parking_id) REFERENCES parking(id);


--
-- Name: level fkliqsellqidqhi01xnad6d009h; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY level
    ADD CONSTRAINT fkliqsellqidqhi01xnad6d009h FOREIGN KEY (short_name_id) REFERENCES multilingual_string_entity(id);


--
-- Name: parking_area_equipment_places fklyjyw910bdsck4ia3cu5u02b7; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area_equipment_places
    ADD CONSTRAINT fklyjyw910bdsck4ia3cu5u02b7 FOREIGN KEY (parking_area_id) REFERENCES parking_area(id);


--
-- Name: quay_check_constraints fkm3br6dbr6lfeirt677vgwhbkt; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_check_constraints
    ADD CONSTRAINT fkm3br6dbr6lfeirt677vgwhbkt FOREIGN KEY (quay_id) REFERENCES quay(id);


--
-- Name: path_link fkmdata7200nfbkhwfiwbdr89gx; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_link
    ADD CONSTRAINT fkmdata7200nfbkhwfiwbdr89gx FOREIGN KEY (from_id) REFERENCES path_link_end(id);


--
-- Name: parking_parking_vehicle_types fkmdeyc63w7rq4oahia4ekbhpu9; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_parking_vehicle_types
    ADD CONSTRAINT fkmdeyc63w7rq4oahia4ekbhpu9 FOREIGN KEY (parking_id) REFERENCES parking(id);


--
-- Name: road_address_key_values fkmg898c5r0h3uvwbnltd4oh14y; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY road_address_key_values
    ADD CONSTRAINT fkmg898c5r0h3uvwbnltd4oh14y FOREIGN KEY (road_address_id) REFERENCES road_address(id);


--
-- Name: installed_equipment_version_structure_installed_equipment fkmjjjgsh44ditp528hx9nynkby; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY installed_equipment_version_structure_installed_equipment
    ADD CONSTRAINT fkmjjjgsh44ditp528hx9nynkby FOREIGN KEY (place_equipment_id) REFERENCES installed_equipment_version_structure(id);


--
-- Name: valid_between_key_values fkmqgoyep22m5k8w4dc32mbenkp; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY valid_between_key_values
    ADD CONSTRAINT fkmqgoyep22m5k8w4dc32mbenkp FOREIGN KEY (valid_between_id) REFERENCES valid_between(id);


--
-- Name: road_address fkms7te4qixovkuhj7fbl846a3g; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY road_address
    ADD CONSTRAINT fkms7te4qixovkuhj7fbl846a3g FOREIGN KEY (polygon_id) REFERENCES persistable_polygon(id);


--
-- Name: equipment_place_equipment_positions fkn4tdt95gfsvqido4rq1mjb8h8; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_place_equipment_positions
    ADD CONSTRAINT fkn4tdt95gfsvqido4rq1mjb8h8 FOREIGN KEY (equipment_place_id) REFERENCES equipment_place(id);


--
-- Name: boarding_position fkndqqqjgyacwmv60e3qhsnkbhb; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position
    ADD CONSTRAINT fkndqqqjgyacwmv60e3qhsnkbhb FOREIGN KEY (polygon_id) REFERENCES persistable_polygon(id);


--
-- Name: equipment_place_key_values fkns6x8o4fyw73gopona4fiihdu; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_place_key_values
    ADD CONSTRAINT fkns6x8o4fyw73gopona4fiihdu FOREIGN KEY (equipment_place_id) REFERENCES equipment_place(id);


--
-- Name: value_items fknuulrwd9o0m7ocvcntkig5csj; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY value_items
    ADD CONSTRAINT fknuulrwd9o0m7ocvcntkig5csj FOREIGN KEY (value_id) REFERENCES value(id);


--
-- Name: quay_alternative_names fknvsb6xd4x2jkguqx9hfnfsy74; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY quay_alternative_names
    ADD CONSTRAINT fknvsb6xd4x2jkguqx9hfnfsy74 FOREIGN KEY (quay_id) REFERENCES quay(id);


--
-- Name: parking_alternative_names fknx3vhtx356nwc4fwg1dct6ic3; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_alternative_names
    ADD CONSTRAINT fknx3vhtx356nwc4fwg1dct6ic3 FOREIGN KEY (parking_id) REFERENCES parking(id);


--
-- Name: parking_area_check_constraints fkny2owjxs13whgj345oo4ghxco; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area_check_constraints
    ADD CONSTRAINT fkny2owjxs13whgj345oo4ghxco FOREIGN KEY (check_constraints_id) REFERENCES check_constraint(id);


--
-- Name: parking_properties_parking_user_types fko1j5k835d8tdh5x4ldjkvnjfv; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_properties_parking_user_types
    ADD CONSTRAINT fko1j5k835d8tdh5x4ldjkvnjfv FOREIGN KEY (parking_properties_id) REFERENCES parking_properties(id);


--
-- Name: parking_area_check_constraints fko9rxbvt9bqk35ggadptkxsrna; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area_check_constraints
    ADD CONSTRAINT fko9rxbvt9bqk35ggadptkxsrna FOREIGN KEY (parking_area_id) REFERENCES parking_area(id);


--
-- Name: access_space_check_constraints fkob3boul2jifb3vekl11asof4v; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space_check_constraints
    ADD CONSTRAINT fkob3boul2jifb3vekl11asof4v FOREIGN KEY (access_space_id) REFERENCES access_space(id);


--
-- Name: parking_area_key_values fkoj56x7qga6tilll3mor88hsru; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area_key_values
    ADD CONSTRAINT fkoj56x7qga6tilll3mor88hsru FOREIGN KEY (parking_area_id) REFERENCES parking_area(id);


--
-- Name: stop_place_key_values fkolek3mod8n2ncbfyp6t9nj1qh; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_key_values
    ADD CONSTRAINT fkolek3mod8n2ncbfyp6t9nj1qh FOREIGN KEY (stop_place_id) REFERENCES stop_place(id);


--
-- Name: parking fkpbhyj09qvbw33cmw9wshgsg7y; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking
    ADD CONSTRAINT fkpbhyj09qvbw33cmw9wshgsg7y FOREIGN KEY (topographic_place_id) REFERENCES topographic_place(id);


--
-- Name: boarding_position_key_values fkqd4jmt7qmq7ecblajntgrae11; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position_key_values
    ADD CONSTRAINT fkqd4jmt7qmq7ecblajntgrae11 FOREIGN KEY (key_values_id) REFERENCES value(id);


--
-- Name: site_path_links_rel_structure_path_link_ref_or_site_path_link fkqht0wwe7ddpjcxp22vq5x6wcs; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY site_path_links_rel_structure_path_link_ref_or_site_path_link
    ADD CONSTRAINT fkqht0wwe7ddpjcxp22vq5x6wcs FOREIGN KEY (site_path_links_rel_structure_id) REFERENCES site_path_links_rel_structure(id);


--
-- Name: level_key_values fkql9va96wfvxdtrsar93qoqapf; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY level_key_values
    ADD CONSTRAINT fkql9va96wfvxdtrsar93qoqapf FOREIGN KEY (level_id) REFERENCES level(id);


--
-- Name: stop_place_quays fkr5tlsd2as2q238g2phacwql72; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_quays
    ADD CONSTRAINT fkr5tlsd2as2q238g2phacwql72 FOREIGN KEY (quays_id) REFERENCES quay(id);


--
-- Name: parking_parking_properties fkr8c26brhce4cxkxse6hsxb4ph; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_parking_properties
    ADD CONSTRAINT fkr8c26brhce4cxkxse6hsxb4ph FOREIGN KEY (parking_properties_id) REFERENCES parking_properties(id);


--
-- Name: parking fkrksfqb92ty4tpgf0aegaijlal; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking
    ADD CONSTRAINT fkrksfqb92ty4tpgf0aegaijlal FOREIGN KEY (accessibility_assessment_id) REFERENCES accessibility_assessment(id);


--
-- Name: parking_area fkrksrhan2eax7wt85lu0khpuqn; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area
    ADD CONSTRAINT fkrksrhan2eax7wt85lu0khpuqn FOREIGN KEY (place_equipments_id) REFERENCES installed_equipment_version_structure(id);


--
-- Name: parking_area fkrm29xbq5804fmta4ttiki0qql; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area
    ADD CONSTRAINT fkrm29xbq5804fmta4ttiki0qql FOREIGN KEY (polygon_id) REFERENCES persistable_polygon(id);


--
-- Name: topographic_place_valid_betweens fkrr5uj74b1scqktbxs2qbx3bkp; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY topographic_place_valid_betweens
    ADD CONSTRAINT fkrr5uj74b1scqktbxs2qbx3bkp FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: stop_place_tariff_zones fkrtg0y1vlnv40qupj0bkwp7k00; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place_tariff_zones
    ADD CONSTRAINT fkrtg0y1vlnv40qupj0bkwp7k00 FOREIGN KEY (tariff_zones_id) REFERENCES tariff_zone_ref(id);


--
-- Name: access_space_key_values fks5ltc1fbi2mbo1arc7ttr5u7t; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY access_space_key_values
    ADD CONSTRAINT fks5ltc1fbi2mbo1arc7ttr5u7t FOREIGN KEY (key_values_id) REFERENCES value(id);


--
-- Name: tariff_zone fksiof3uxjddw8koviv4omgimwc; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY tariff_zone
    ADD CONSTRAINT fksiof3uxjddw8koviv4omgimwc FOREIGN KEY (polygon_id) REFERENCES persistable_polygon(id);


--
-- Name: equipment_position_valid_betweens fkslxc7uh5ns9koh4tkdlqkp1fb; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY equipment_position_valid_betweens
    ADD CONSTRAINT fkslxc7uh5ns9koh4tkdlqkp1fb FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: path_link fksyksujopo02932so13dt18a3n; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_link
    ADD CONSTRAINT fksyksujopo02932so13dt18a3n FOREIGN KEY (to_id) REFERENCES path_link_end(id);


--
-- Name: path_link_end fkt1jkprb71o8k98r1k9gl9767c; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY path_link_end
    ADD CONSTRAINT fkt1jkprb71o8k98r1k9gl9767c FOREIGN KEY (path_junction_id) REFERENCES path_junction(id);


--
-- Name: boarding_position_equipment_places fkt6b6123yefqwpog332fptv4wc; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY boarding_position_equipment_places
    ADD CONSTRAINT fkt6b6123yefqwpog332fptv4wc FOREIGN KEY (boarding_position_id) REFERENCES boarding_position(id);


--
-- Name: stop_place fkt75b2x29642ei9s99c7pue6h5; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place
    ADD CONSTRAINT fkt75b2x29642ei9s99c7pue6h5 FOREIGN KEY (polygon_id) REFERENCES persistable_polygon(id);


--
-- Name: parking_area_valid_betweens fktmc4w66yn4udcky45jvcutwap; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_area_valid_betweens
    ADD CONSTRAINT fktmc4w66yn4udcky45jvcutwap FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: stop_place fktmqm9d5a1fuxiivaxmjluis6g; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY stop_place
    ADD CONSTRAINT fktmqm9d5a1fuxiivaxmjluis6g FOREIGN KEY (accessibility_assessment_id) REFERENCES accessibility_assessment(id);


--
-- Name: level_valid_betweens fkw40gfrfmxln7arsxe9cdlqty; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY level_valid_betweens
    ADD CONSTRAINT fkw40gfrfmxln7arsxe9cdlqty FOREIGN KEY (valid_betweens_id) REFERENCES valid_between(id);


--
-- Name: parking_equipment_places fkwgs9h00yk2uvhgsstupeyxii; Type: FK CONSTRAINT; Schema: public; Owner: tiamat
--

ALTER TABLE ONLY parking_equipment_places
    ADD CONSTRAINT fkwgs9h00yk2uvhgsstupeyxii FOREIGN KEY (equipment_places_id) REFERENCES equipment_place(id);


--
-- PostgreSQL database dump complete
--
