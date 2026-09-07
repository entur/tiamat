-- Persists Parking.infoLinks (DPO-4810). Per decision 0c, infoLinks is declared @Transient
-- on GroupOfEntities_VersionStructure (matching NeTEx's own placement on the abstract
-- GroupOfEntities_VersionStructure), and shadowed as a persisted @ElementCollection on
-- Parking only, so exactly one collection table is created rather than one per subclass.
--
-- typeOfInfoLink stores a single core enum value (uppercase, @Enumerated(STRING) per decision
-- 0g) even though NeTEx's InfoLinkStructure.typeOfInfoLink is an XML list attribute — only the
-- first declared value is persisted, matching every current producer (exactly one type is ever
-- supplied) and the shipped ext V4.

CREATE TABLE parking_info_links (
    parking_id        bigint NOT NULL,
    uri               varchar(512) NOT NULL,
    type_of_info_link varchar(255) CHECK (type_of_info_link IN (
                          'CONTACT', 'RESOURCE', 'INFO', 'IMAGE', 'DOCUMENT',
                          'TIMETABLE_DOCUMENT', 'FARE_SHEET', 'DATA_LICENCE',
                          'MOBILE_APP_DOWNLOAD', 'MOBILE_APP_INSTALL_CHECK', 'MAP', 'ICON', 'OTHER'))
);

ALTER TABLE parking_info_links
    ADD CONSTRAINT parking_info_links_parking_fk
        FOREIGN KEY (parking_id) REFERENCES parking;

CREATE INDEX parking_info_links_parking_id_index
    ON parking_info_links (parking_id);
