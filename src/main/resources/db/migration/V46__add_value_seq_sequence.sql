CREATE SEQUENCE value_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

SELECT setval('value_seq',  (SELECT MAX(id) + 1 FROM value));

ALTER TABLE value_seq OWNER TO tiamat;


CREATE SEQUENCE export_job_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
SELECT setval('export_job_seq',  (SELECT MAX(id) + 1 FROM export_job));
ALTER TABLE export_job_seq OWNER TO tiamat;
