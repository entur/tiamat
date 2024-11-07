DROP VIEW IF EXISTS stop_place_newest_version;

CREATE VIEW stop_place_newest_version AS
SELECT DISTINCT ON (sp.netex_id)
    sp.*,
    quay.public_code AS quay_public_code,
    address_value.items as street_address

FROM stop_place AS sp

         INNER JOIN stop_place_quays AS spq ON sp.id = spq.stop_place_id
         INNER JOIN quay ON spq.quays_id = quay.id

         LEFT JOIN stop_place_key_values AS spkv ON
             sp.id = spkv.stop_place_id AND spkv.key_values_key = 'streetAddress'
         LEFT JOIN value_items AS address_value ON spkv.key_values_id = address_value.value_id

ORDER BY netex_id ASC, version DESC

