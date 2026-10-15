-- Persists Parking.organisationRef: the operating organisation's reference, carried
-- by NeTEx as OperatorRef/AuthorityRef/GeneralOrganisationRef on Site_VersionStructure.
--
-- The field is free-form: no CHECK constraint, no foreign key. Tiamat does not hold an
-- organisation entity, so the reference is opaque here and only meaningful to the
-- systems that publish and consume it.

ALTER TABLE IF EXISTS parking
    ADD COLUMN organisation_ref varchar(255),
    ADD COLUMN organisation_ref_version varchar(255);
