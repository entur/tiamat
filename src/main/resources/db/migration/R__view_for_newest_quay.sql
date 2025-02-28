DROP VIEW IF EXISTS quay_newest_version;

CREATE VIEW quay_newest_version AS
SELECT DISTINCT ON (q.netex_id) q.*,
                                an.name_value        as location_swe,
                                streetAddress.items  as street_address,
                                priority.items       as priority,
                                validityStart.items  as validity_start,
                                validityEnd.items    as validity_end,
                                ELYCode.items        as ely_code,
                                postalCode.items     as postal_code,
                                functionalArea.items as functional_area,
                                sp.id                as stop_place_id,
                                sp.version           as stop_place_version

FROM quay AS q

         INNER JOIN stop_place_quays AS spq ON spq.quays_id = q.id

         INNER JOIN stop_place AS sp ON spq.stop_place_id = sp.id

         LEFT JOIN quay_alternative_names AS qan
                   ON q.id = qan.quay_id
         LEFT JOIN alternative_name as an
                   on qan.alternative_names_id = an.id AND an.name_type = 'OTHER' AND an.name_lang = 'swe'

         LEFT JOIN quay_key_values AS qkvAddress
                   ON q.id = qkvAddress.quay_id AND qkvAddress.key_values_key = 'streetAddress'
         LEFT JOIN value_items AS streetAddress ON qkvAddress.key_values_id = streetAddress.value_id

         LEFT JOIN quay_key_values AS qkvPriority
                   ON q.id = qkvPriority.quay_id AND qkvPriority.key_values_key = 'priority'
         LEFT JOIN value_items AS priority ON qkvPriority.key_values_id = priority.value_id

         LEFT JOIN quay_key_values AS qkvValidityStart ON
    q.id = qkvValidityStart.quay_id AND qkvValidityStart.key_values_key = 'validityStart'
         LEFT JOIN value_items AS validityStart ON qkvValidityStart.key_values_id = validityStart.value_id

         LEFT JOIN quay_key_values AS qkvValidityEnd ON
    q.id = qkvValidityEnd.quay_id AND qkvValidityEnd.key_values_key = 'validityEnd'
         LEFT JOIN value_items AS validityEnd ON qkvValidityEnd.key_values_id = validityEnd.value_id

         LEFT JOIN quay_key_values AS qkvELYCode ON
    q.id = qkvELYCode.quay_id AND qkvELYCode.key_values_key = 'elyNumber'
         LEFT JOIN value_items AS ELYCode ON qkvELYCode.key_values_id = ELYCode.value_id

         LEFT JOIN quay_key_values AS qkvPostalCode ON
    q.id = qkvPostalCode.quay_id AND qkvPostalCode.key_values_key = 'postalCode'
         LEFT JOIN value_items AS postalCode ON qkvPostalCode.key_values_id = postalCode.value_id

         LEFT JOIN quay_key_values AS qkvFunctionalArea ON
    q.id = qkvFunctionalArea.quay_id AND qkvFunctionalArea.key_values_key = 'functionalArea'
         LEFT JOIN value_items AS functionalArea ON qkvFunctionalArea.key_values_id = functionalArea.value_id


ORDER BY netex_id ASC, version DESC

