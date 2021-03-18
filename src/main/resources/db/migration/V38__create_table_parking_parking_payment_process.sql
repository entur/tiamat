CREATE TABLE parking_parking_payment_process
(
    parking_id              bigint NOT NULL,
    parking_payment_process character varying(255)
);

ALTER TABLE parking_parking_payment_process
    ADD CONSTRAINT parking_parking_payment_process_fk FOREIGN KEY (parking_id) REFERENCES parking (id);
