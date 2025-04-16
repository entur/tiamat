--- For basic search by label
CREATE INDEX quay_public_code_index ON quay(public_code);

-- For loading stops to the map
CREATE INDEX quay_centroid_index ON quay USING gist (centroid);
