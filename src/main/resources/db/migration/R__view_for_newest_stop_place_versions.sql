DROP VIEW IF EXISTS stop_place_newest_version;

CREATE VIEW stop_place_newest_version AS
SELECT DISTINCT ON (sp.netex_id)
    sp.*,
    quay.public_code AS quay_public_code,
    streetAddress.items AS street_address,
    priority.items AS priority,
    validityStart.items AS validity_start,
    validityEnd.items AS validity_end

FROM stop_place AS sp

         INNER JOIN stop_place_quays AS spq ON sp.id = spq.stop_place_id
         INNER JOIN quay ON spq.quays_id = quay.id

         LEFT JOIN stop_place_key_values AS spkvAddress ON
             sp.id = spkvAddress.stop_place_id AND spkvAddress.key_values_key = 'streetAddress'
         LEFT JOIN value_items AS streetAddress ON spkvAddress.key_values_id = streetAddress.value_id

         LEFT JOIN stop_place_key_values AS spkvPriority ON
            sp.id = spkvPriority.stop_place_id AND spkvPriority.key_values_key = 'priority'
         LEFT JOIN value_items AS priority ON spkvPriority.key_values_id = priority.value_id

         LEFT JOIN stop_place_key_values AS spkvValidityStart ON
            sp.id = spkvValidityStart.stop_place_id AND spkvValidityStart.key_values_key = 'validityStart'
         LEFT JOIN value_items AS validityStart ON spkvValidityStart.key_values_id = validityStart.value_id

         LEFT JOIN stop_place_key_values AS spkvValidityEnd ON
            sp.id = spkvValidityEnd.stop_place_id AND spkvValidityEnd.key_values_key = 'validityEnd'
         LEFT JOIN value_items AS validityEnd ON spkvValidityEnd.key_values_id = validityEnd.value_id

ORDER BY netex_id ASC, version DESC

