ALTER TABLE ONLY hsl_accessibility_properties
    ADD COLUMN accessibility_level character varying(255) NOT NULL DEFAULT 'unknown';

COMMENT ON COLUMN hsl_accessibility_properties.accessibility_level
        IS 'Esteettömyystaso: Täysin esteetön (fullyAccessible) / Vähäisiä esteitä (mostlyAccessible) / Osittain esteellinen (partiallyInaccessible) / Esteellinen (inaccessible) / Esteettömyystietoja puuttuu (unknown)';
