CREATE TABLE quay_external_links (
    quay_id     BIGINT NOT NULL REFERENCES quay(id),
    order_num   INT NOT NULL,
    name        text,
    location    text
);
