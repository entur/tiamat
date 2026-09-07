CREATE TABLE public.parking_payment_methods
(
    parking_id      bigint NOT NULL,
    payment_methods character varying(255)
);

ALTER TABLE public.parking_payment_methods
    OWNER to tiamat;

ALTER TABLE ONLY parking_payment_methods
    ADD CONSTRAINT parking_payment_methods_fk FOREIGN KEY (parking_id) REFERENCES parking (id);
